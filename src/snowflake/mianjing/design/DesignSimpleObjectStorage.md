# Design a Simple S3-Like Object Store (with Deduplication)

> snowflake · S3-style design, scoped down · focus: **avoid storing the same bytes twice**

Design an object storage service like S3, but **simpler** than the real product:
no multi-region replication, no fine-grained IAM, no lifecycle/storage classes,
no S3 Select. The interview wants a working `PUT / GET / DELETE / LIST` and
then a deep dive on **how to avoid saving duplicate file content** so the
storage bill stays small.

The single guiding principle:

> **The user-facing namespace (bucket/key) is decoupled from the physical
> bytes. Many keys can point at the same blob. Each unique blob is stored
> exactly once.**

Everything else — chunking, refcounting, GC, race handling — falls out of that
one decision.

---

## Phase 1: Scope & Requirements

### Functional Requirements (the simplified surface)

| API | Purpose |
| --- | ------- |
| `PUT  /bucket/key`              | Upload an object. |
| `GET  /bucket/key`              | Download an object. |
| `DELETE /bucket/key`            | Delete an object (the *logical* mapping). |
| `LIST /bucket?prefix=...`       | List object keys in a bucket. |
| `HEAD /bucket/key`              | Get metadata only. |

We **don't** support (out of scope):

- Versioning, lifecycle rules, storage classes (Standard/IA/Glacier), MFA delete.
- Multi-region replication; assume one region.
- Fine-grained IAM; assume bucket-level auth.
- S3 Select / Athena-style server-side query.
- Multipart upload as a *user-visible* feature — we'll still chunk internally,
  but the API is a single PUT.

### Non-Functional Requirements

| Requirement | Target | Why |
| ----------- | ------ | --- |
| **Durability** | 11 nines (`99.999999999%`) | Object stores promise this; users design around it. |
| **Availability** | 99.95% | Standard for a single-region object store. |
| **Read latency (P99)** | < 100 ms for small objects | Hot path for most apps. |
| **Write throughput** | scales horizontally | Bottleneck must not be one node. |
| **Storage efficiency (the ask)** | ≥ 30% reduction via dedup | Drives the entire design. |
| **Strong read-after-write** | for the *same key* | "I uploaded, now read it" must work. |

### Capacity Math (rough)

| Metric | Value |
| ------ | ----- |
| Tenants | 10 K |
| Objects per tenant (avg) | 1 M |
| Total objects | 10 B |
| Avg object size | 1 MB |
| Logical data | 10 PB |
| **Expected dedup ratio** | 30–60% (mixed corp data) |
| Physical data after dedup | 4–7 PB |
| Read QPS | 100 K |
| Write QPS | 20 K |

So we plan for **~5 PB of physical bytes** behind ~10 PB of logical data, and
~10 B small metadata rows. Storage savings is the win we're optimizing for.

---

## Phase 2: The Two-Layer Data Model

The whole design hinges on splitting the **namespace layer** from the **blob
layer**:

```
┌──────────────────────────┐         ┌──────────────────────────┐
│   Object metadata DB     │         │   Blob index DB          │
│   ("the namespace")      │         │   ("the content store")  │
│                          │         │                          │
│   bucket + key           │ ──ref──►│   content hash           │
│   → list of chunk hashes │         │   → physical location    │
│   + size, mime, owner,   │         │   + refcount             │
│     created_at, etc.     │         │   + size                 │
└──────────────────────────┘         └──────────────────────────┘
                                              │
                                              ▼
                                    ┌──────────────────────────┐
                                    │  Physical Blob Storage   │
                                    │  (object store / disk +  │
                                    │   replication / EC)      │
                                    └──────────────────────────┘
```

### Tables

```text
ObjectMeta                        # the user-facing namespace
├── bucket               (PK)
├── key                  (PK)
├── object_id            UUID, immutable
├── size                 bytes
├── mime
├── chunk_list           [ chunk_hash_1, chunk_hash_2, ... ]   # ordered
├── created_at, etag, owner, ...

BlobIndex                         # one row per UNIQUE chunk
├── chunk_hash           (PK)     # SHA-256 of the chunk bytes
├── size
├── physical_location    (e.g. blob_volume_42 / offset / object id)
├── refcount             integer  # how many ObjectMeta rows reference it
├── created_at
```

### Why this split is the entire design

- The **ObjectMeta** row is small (a few hundred bytes) — millions of "copies"
  of the same file each cost only a metadata row, **not** the file bytes.
- The **BlobIndex** is the dedup table: keyed by content hash, one entry per
  unique chunk. Its `refcount` tells us when it's safe to delete the bytes.
- Two clients uploading **the same file** don't double-write the bytes; they
  both end up creating an `ObjectMeta` row that references the same chunk
  hashes. Storage cost: ~0 for the second upload.

---

## Phase 3: Deduplication Strategy

### Step 1 — Content-address the bytes (SHA-256)

Every chunk's identity = `SHA-256(bytes)`. SHA-256 is preferred over MD5/SHA-1
because:

- Collision-resistant; the whole design's correctness rests on
  "same hash ⇒ same content".
- Hardware-accelerated on modern CPUs (SHA-NI / ARM Crypto Ext.).
- Cheap enough that we can hash on the client before upload.

**Collision concern:** with SHA-256 the probability of an accidental collision
across 10 B unique chunks is far below the probability of undetected disk
errors. For the truly paranoid (or for adversarial inputs), add a second
algorithm — e.g. store `(SHA-256, BLAKE3)` and require both to match. The
interview-acceptable answer is: "SHA-256 is enough; if we ever need higher
assurance, add a second hash."

### Step 2 — Chunk large files (variable-size CDC)

Hashing the **entire file** dedupes only on identical files. That misses the
biggest savings: a 10 GB log file growing by 1 line a day would be re-uploaded
in full every time.

Use **content-defined chunking** (CDC, e.g. Rabin fingerprinting / FastCDC):

- Sliding window over the bytes; cut at points where a rolling hash matches
  a pattern (e.g. low N bits all zero).
- Average chunk size ≈ 1–4 MB; min ≈ 256 KB, max ≈ 8 MB.
- Insertion or deletion shifts only the affected chunks — the rest still hit
  the dedup table. This is the Borg / restic / Dropbox approach.

For tiny objects (< chunk size) we just hash the whole object and store one
chunk; CDC is only worth it past a threshold.

### Step 3 — "Do you already have this chunk?" (HEAD-before-PUT)

Client-driven dedup turns most "uploads" into a metadata-only operation.

```
Client                                Service
  │                                     │
  │── compute SHA-256 of each chunk ────│
  │                                     │
  │── HEAD /blobs/<hash> (batched) ────►│
  │                                     │   look up BlobIndex
  │◄── { hash1: HAVE, hash2: MISSING }──│
  │                                     │
  │── PUT  /blobs/<hash2> (bytes)  ────►│   write to physical store
  │── PUT  /objects/bucket/key   ──────►│   write ObjectMeta + bump refcounts
  │                                     │
  │◄────── 200 OK + etag ───────────────│
```

The client only sends the **bytes that don't already exist**. For a
re-uploaded file that's already in the store, this is a few KB of metadata
instead of a few GB of bytes. **This is the cost win.**

### Step 4 — Server-side dedup as backstop

Don't trust the client to be honest. After a chunk is written, the server:

1. Recomputes its hash.
2. If the hash matches an existing `BlobIndex` entry, drops the new bytes and
   points `ObjectMeta` at the existing blob.
3. If it doesn't match the hash the client claimed, rejects the upload.

This catches buggy clients, in-flight corruption, and malicious clients trying
to poison the dedup table.

### Step 5 — Refcounting and garbage collection

Deleting an `ObjectMeta` row must NOT immediately delete the underlying bytes
— another object might still reference them.

```text
DELETE bucket/key:
  for each chunk_hash in object_meta.chunk_list:
      BlobIndex.refcount[chunk_hash] -= 1
  delete object_meta row
  if refcount hit 0: enqueue for async GC (don't delete inline)
```

**Why async GC:**
- Avoids race with a concurrent `PUT` of the same content (see Phase 5).
- Avoids deleting bytes only to re-upload them seconds later (common pattern:
  delete-then-replace).
- A "tombstone with grace period" (e.g. 24 h) is enough to absorb both.

A nightly GC job:
- Scans `BlobIndex` for `refcount = 0 AND tombstoned_at < now - 24h`.
- Deletes the physical blob and the index row.

---

## Phase 4: Write Path (PUT) — End to End

```
1. Client splits file into CDC chunks; computes SHA-256 of each.
2. Client → coordinator:    POST /upload-prepare { bucket, key, [hashes...], sizes }
3. Coordinator looks up BlobIndex for each hash:
     existing  → "skip"
     missing   → "upload"
4. Client uploads only the missing chunks (parallel).
     Each chunk is written to the physical store; the storage node
     returns its physical location.
5. Client → coordinator:    POST /upload-commit { bucket, key, [hashes...] }
6. Coordinator atomically:
     - bumps refcount for every hash (existing + new) by 1
     - inserts/updates ObjectMeta row
     - returns etag = SHA-256 of the concatenated chunk hashes
```

### Atomicity rules

The two writes that *must* be atomic together are:

- **Bumping refcount on a hash** and
- **Inserting the ObjectMeta row that references it.**

Otherwise GC could see refcount=0 and delete a blob that ObjectMeta is about to
reference. Solutions:

- Single transactional metadata DB (Spanner, CockroachDB, or
  per-shard Postgres) covering both `BlobIndex` and `ObjectMeta`.
- Or: write `ObjectMeta` with status=`pending`, bump refcounts, flip status
  to `committed` last. GC ignores `pending` rows beyond a TTL.

### Concurrent uploads of the same bytes

Two clients PUT the same chunk at the same time:

- Both compute the same hash, both see "missing" from HEAD, both upload.
- The storage node accepts both writes (different physical locations are fine).
- The coordinator does an `INSERT … ON CONFLICT (chunk_hash) DO NOTHING`.
- The loser's bytes are now orphaned (no `BlobIndex` row points at them) — GC
  cleans them up.

We accept this small waste in exchange for not needing distributed locks on
the hot upload path.

---

## Phase 5: Read Path (GET)

```
1. Client → coordinator: GET /bucket/key
2. Coordinator reads ObjectMeta → list of chunk hashes.
3. Coordinator resolves each hash via BlobIndex → physical_location.
4. Coordinator (or client, via signed URLs) streams the chunks back in order.
```

Optimizations:

- **Signed URLs**: hand the client signed, time-limited URLs to the chunks and
  let it fetch directly from storage nodes / a CDN. Coordinator never moves
  the bytes.
- **CDN in front of GET**: dedup is a perfect match for CDN caching — the URL
  for a chunk is its hash, so cache hit ratios are high and invalidation is
  trivial (the bytes never change).
- **Read-after-write**: because the commit step is transactional, the
  ObjectMeta row appears only when its chunks are durable. Same key reads see
  the new value immediately.

---

## Phase 6: Storage Layer

### Physical layout

We do **not** put one chunk = one file on disk (millions of tiny files = filesystem
death). Instead:

- Append chunks into large **blob volumes** (e.g. 10 GB segment files), like
  Haystack / Bigtable SSTable / S3's internal layout.
- `physical_location = (volume_id, offset, length)`.
- Volumes are immutable once sealed. GC compacts a volume by copying live
  chunks to a new volume and dropping the old one.

### Durability

- Replication factor 3 across racks/AZs, **or**
- Reed-Solomon erasure coding (e.g. `10+4`) for cold data — same durability
  with ~1.4× overhead instead of 3×.

### Why this matters for cost

| Layer | Trick | Savings |
| ----- | ----- | ------- |
| Dedup | Don't store the same chunk twice | 30–60% |
| Erasure coding | 1.4× vs 3× for cold data | ~50% |
| Compression | zstd within blob volumes for compressible data | 2–5× on text |
| Tiered storage | Move cold blob volumes to cheaper media | ~5× |

These compose multiplicatively on **physical bytes**. The dedup layer is what
makes the rest worth doing — there's no point compressing and erasure-coding
the same file 100 times.

---

## Phase 7: Failure Modes & Edge Cases

| Scenario | What goes wrong | Mitigation |
| -------- | --------------- | ---------- |
| **Hash collision** | Two distinct chunks share a hash → wrong bytes returned. | SHA-256 makes this astronomically unlikely; optional second hash for paranoid mode. |
| **Concurrent dedup of same chunk** | Two uploaders write the same bytes twice. | `INSERT ... ON CONFLICT`; orphaned write is GC'd. |
| **Race between DELETE and PUT** | DELETE drops refcount to 0, GC starts; a PUT of the same content arrives mid-GC. | **Tombstone-with-grace**: GC waits 24 h after refcount hits 0. PUT during the grace window resurrects the row instead of writing fresh. |
| **Partial upload** | Client crashes mid-upload, ObjectMeta never committed. | `pending` ObjectMeta rows expire after a TTL; their refcount bumps are rolled back. |
| **Client lies about hash** | Tries to reference content it doesn't actually have, hoping for someone else's data. | Server recomputes hash on every PUT; require auth on the BlobIndex (tenants can't reference each other's blobs without going through a real PUT). |
| **Cross-tenant dedup leak** | Tenant A's bytes deduped against tenant B's; B can probe `HEAD` to learn what A has. | If this matters, scope dedup *within* a tenant only, **OR** use convergent encryption (see below). |
| **Encryption breaks dedup** | If every tenant encrypts with their own key, identical plaintexts produce different ciphertexts → no dedup. | **Convergent encryption**: encryption key = hash of the plaintext. Identical plaintexts → identical ciphertexts → still dedupable. Trade-off: vulnerable to "confirmation of file" attacks. |
| **Hot blob** | One chunk referenced by millions of objects gets read at huge QPS. | CDN caching keyed by chunk hash; replicate hot chunks across more storage nodes. |

---

## Phase 8: Scaling the Metadata Tier

`BlobIndex` and `ObjectMeta` are where the QPS lives. The bytes scale by
adding storage nodes; the metadata scales by sharding.

| Table | Shard key | Why |
| ----- | --------- | --- |
| `ObjectMeta` | `hash(bucket)` or `hash(bucket, key)` | LIST queries are bucket-scoped. |
| `BlobIndex` | `hash(chunk_hash)` (first byte of the SHA, basically) | Even distribution; HEAD lookups are point queries. |

`BlobIndex` is the **read-heavy** table because every PUT does a HEAD on every
chunk hash. Cache it aggressively (Redis / on-box cache); a hash that exists
in the index is immutable, so cache invalidation is trivial.

---

## Phase 9: Cost Walk-Through

To make the dedup ROI concrete, contrast naive vs deduped storage for a tenant
with 1 TB logical data and 50% duplication:

| Approach | Physical bytes | $/month @ $0.023/GB |
| -------- | -------------- | ------------------- |
| Naive (one copy per upload, 3× replicated) | 3 TB | ~$70 |
| Naive + erasure coded (1.4×) | 1.4 TB | ~$33 |
| **Dedup + EC + zstd (assume 2× compression)** | ~0.35 TB | **~$8** |

That's an **~9× cost reduction**, dominated by the dedup step. Across 10 K
tenants the savings compound into millions per year — which is what justifies
the engineering complexity.

---

## Common Mistakes

| Mistake | Why it's bad | Fix |
| ------- | ------------ | --- |
| Hashing the whole file only | Misses dedup on near-identical files (logs, VM images, datasets). | Chunk with CDC. |
| Trusting the client's hash | One bad client can poison the dedup table for everyone. | Recompute on the server. |
| Deleting bytes inline on DELETE | Race with a concurrent PUT of the same content; also fights re-upload patterns. | Tombstone + async GC with grace period. |
| Per-tenant key encryption + cross-tenant dedup | Either dedup breaks or you leak across tenants. | Pick: per-tenant dedup only, OR convergent encryption with explicit awareness of its caveats. |
| Storing each chunk as a separate file on disk | Inode explosion; filesystem unusable. | Pack chunks into large append-only blob volumes. |
| Distributed lock on every PUT to dedup | Kills write QPS. | Lock-free; tolerate occasional duplicate writes and GC them later. |
| One DB row per object only (no separate BlobIndex) | Have to scan all objects to know if bytes are unique. | Two-layer model is the entire point. |

---

## Key Concepts for the Interview

| Topic | What to say |
| ----- | ----------- |
| **Two-layer data model** | "Namespace (bucket/key → chunk hashes) is separate from the blob layer (hash → bytes + refcount). Dedup is just *not* inserting into the blob layer when the hash already exists." |
| **Content-addressed storage** | "Every chunk is identified by its SHA-256. Same content ⇒ same address ⇒ stored once." |
| **CDC chunking** | "Variable-size content-defined chunks so insertions don't shift everything; that's how we dedup files that *almost* match, not just identical ones." |
| **HEAD-before-PUT** | "Client asks 'do you have this hash?' first; only uploads what's missing. Most re-uploads become metadata-only." |
| **Server-side verification** | "Re-hash on the server before committing — never trust the client's claim." |
| **Refcount + tombstoned GC** | "DELETE just decrements; bytes are reclaimed asynchronously after a grace period to avoid races and re-upload thrash." |
| **Convergent encryption** | "If we want client-side encryption *and* dedup across tenants: encrypt each chunk with a key derived from its own plaintext hash. Trade-off: confirmation-of-file attacks." |
| **Cost stack** | "Dedup × erasure coding × compression × tiering. Dedup is the multiplier that makes the rest worthwhile." |

---

## Wrap-Up

| Aspect | Solution | Why |
| ------ | -------- | --- |
| Avoid duplicate bytes | Two-layer model: ObjectMeta → BlobIndex → physical | One namespace, one blob copy. |
| Dedup granularity | CDC chunks, SHA-256 addressed | Catches near-duplicates, not just identical files. |
| Avoid uploading bytes the server already has | Client-side hash + HEAD-before-PUT | Most re-uploads cost ~0 bandwidth and storage. |
| Trustworthy dedup | Server re-hashes on PUT | Buggy/malicious clients can't poison the index. |
| Safe deletes | Refcount + grace-period tombstone GC | No race with concurrent PUTs, no thrash on delete-then-replace. |
| Atomicity of meta + refcount | Single transactional metadata DB or 2-phase commit (`pending` → `committed`) | GC never deletes a blob someone's about to reference. |
| Storage layer cost | Pack into blob volumes; replication or EC; compression; tiering | Multiply on top of dedup. |
| Hot reads | Hash-keyed CDN | Immutable content = perfect cacheability. |
| Scale metadata | Shard `ObjectMeta` by bucket; shard `BlobIndex` by hash prefix; cache `BlobIndex` reads | Bytes scale with disks; metadata scales with shards. |
