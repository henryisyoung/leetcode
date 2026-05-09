# Design a Key-Value Store (Single-Node → Distributed)

> snowflake · classic storage interview · focus: **durability, fault tolerance,
> then horizontal scale, versioning, and strong consistency**

Two design questions, one progression. Part 1 is a **single-machine KV store**
where the entire question is "how do you survive crashes and disk corruption."
Part 2 takes the same KV store and asks "now make it distributed, globally
versioned, time-travelable, and strongly consistent." Most real interviews ask
the first one and then *follow up* with pieces of the second.

The single guiding principle for Part 1:

> **A write is not durable until it is on disk in a place we can find again
> after a crash.** Everything else — append-only files, WAL, in-memory index,
> compaction — falls out of that one rule.

The single guiding principle for Part 2:

> **Every key has a history, not a value.** Once you accept that, time travel,
> versioning, and replication-with-consensus all become the same problem:
> ordering writes into a single log per key range.

---

# Part 1: Single-Node KV Store with Durability & Fault Tolerance

## Phase 1: Scope & Requirements

### Functional Requirements

| API | Purpose |
| --- | ------- |
| `PUT(key, value)`    | Insert or overwrite a value for a key. |
| `GET(key)`           | Return the latest value (or `not found`). |
| `DELETE(key)`        | Remove the mapping for a key. |
| (optional) `SCAN`    | Range scan — out of scope for hash-index version, doable with sorted variant. |

### Non-Functional Requirements (the actual interview)

| Requirement | Target | Why |
| ----------- | ------ | --- |
| **Durability of acknowledged writes** | 100% under single-machine crash | The whole question is about this. |
| **Crash recovery time** | Bounded — proportional to recent log, not total data | Long recovery = long downtime. |
| **GET latency** | One disk seek (large value) or zero (small value cached) | Index lives in memory. |
| **PUT latency** | One sequential disk append + one fsync | Sequential writes are ~100× faster than random. |
| **Memory usage** | Index fits in RAM, **values do not** | Stated in the prompt. |

### Assumptions from the Prompt

- Memory is **large enough to hold all keys + small per-key metadata in an in-memory hash table**.
- Values can be **large** — they do **not** all fit in memory.
- Single machine, single disk. No replication yet (that's Part 2).

### Capacity Math (rough, to size the index)

| Metric | Value |
| ------ | ----- |
| Number of keys                | 1 B |
| Avg key size                  | 32 B |
| Per-entry index overhead      | `key + (file_id u32) + (offset u64) + (size u32)` ≈ 50–60 B |
| In-memory index size          | ~60 GB |
| Avg value size                | varies, say 10 KB |
| On-disk data size             | ~10 TB |

So **all keys** fit in RAM (roughly), **all values** absolutely do not.

---

## Phase 2: Architecture — Bitcask-Style Log-Structured Store

This is the **Bitcask** design (Riak's KV engine, also the basis of many
log-structured KV stores). It maps almost 1-for-1 onto what was described in
the prompt.

```
        PUT(k, v)
            │
            ▼
   ┌─────────────────────┐
   │   In-memory index   │   key → (file_id, offset, size, ts)
   │   (hash table)      │
   └─────────┬───────────┘
             │
             ▼ (1) append v to active data file
   ┌─────────────────────┐         ┌─────────────────────┐
   │   Write-Ahead Log   │ ◄────── │ Active append-only  │
   │  (sequential ops)   │   (2)   │ data file (segment) │
   └─────────────────────┘ record  └─────────────────────┘
                                          │ rolls over at ~1 GB
                                          ▼
                                   ┌─────────────────────┐
                                   │ Sealed segments     │
                                   │ (immutable)         │ ← compaction input
                                   └─────────────────────┘
```

### Three storage components

| Component | What it stores | Why it exists |
| --------- | -------------- | ------------- |
| **In-memory hash table (the index)** | `key → (file_id, offset, size, ts)` | Lookups need O(1) without touching disk. |
| **Append-only data files (segments)** | Actual key/value bytes, append-only | Sequential writes are fast; immutability makes compaction & recovery trivial. |
| **Write-Ahead Log (WAL)** | Operation records: `<PUT, k, loc>` / `<DEL, k>` | Lets us rebuild the in-memory index after a crash. |

> **Note on a common simplification:** In classic Bitcask, the data files
> *are* the WAL — every record on disk is `(crc, ts, key_size, val_size, key,
> value)` and recovery is done by replaying those data files directly. The
> prompt presents WAL as a separate file, which is also valid (and clearer to
> explain). I'll describe the "separate WAL" version since that's what the
> prompt asks for, then note the simplification at the end.

---

## Phase 3: The Hot Path

### `PUT(key, value)`

```
1. Append value to active data file at offset O.
   - sequential write
   - returns location loc = (active_file_id, O, len(value))
2. Append <PUT, key, loc, ts> to WAL.
3. fsync the WAL.                          ← durability boundary
4. Update in-memory index:  index[key] = loc
5. Return OK.
```

The **`fsync` in step 3 is the durability boundary**. Once fsync returns, the
operation survives a crash; before that, it might not.

### `GET(key)`

```
1. loc = index[key]            ← O(1) in RAM
2. If not found → return NotFound
3. Read `loc.size` bytes from `loc.file_id` at `loc.offset`
4. Return value
```

One disk seek per GET (or zero if the value is in OS page cache).

### `DELETE(key)`

```
1. Append <DEL, key, ts> to WAL, fsync.
2. Remove key from in-memory index.
3. Return OK.
```

We do **not** scrub the value from the data file — that's compaction's job.
The WAL tombstone record is what makes the delete survive a crash.

---

## Phase 4: Crash Recovery

When the process restarts:

```
in_memory_index = {}
for record in scan(WAL, start=last_checkpoint):
    if record.op == PUT:
        in_memory_index[record.key] = record.loc
    elif record.op == DEL:
        in_memory_index.pop(record.key, None)
```

Properties:

- **Idempotent.** Replaying any prefix of the WAL twice yields the same index.
- **Bounded.** WAL is checkpointed periodically (see below) so we don't replay
  from the beginning of time.
- **Self-validating.** Each WAL record has a CRC — torn writes from a crash
  mid-record are detected and the WAL is truncated at the last valid record.

### What about a torn write inside the *data* file?

If we crashed between step 1 (data append) and step 3 (WAL fsync), the value
bytes are sitting in the data file but no WAL record references them. They're
just garbage that compaction will reclaim. **This is safe** — the client never
got an OK, so the operation effectively didn't happen.

### Checkpointing the WAL

Without checkpoints, the WAL grows forever and recovery time grows with it.

```
periodically (every N writes or T seconds):
   1. snapshot the in-memory index → `index.snapshot.<n>`
   2. record `<CHECKPOINT, wal_offset>` in WAL
   3. delete WAL records before that offset (rotate WAL file)
```

Recovery now does:

```
load latest index.snapshot.<n>
replay WAL from offset stored in CHECKPOINT
```

Recovery cost = `O(writes since last checkpoint)`, not `O(all writes ever)`.

---

## Phase 5: Compaction (Garbage Collection)

After many `PUT`s and `DELETE`s, the data files contain lots of stale records:

```
Segment 1:  [ k1=v1 ] [ k2=v2 ] [ k1=v1' ]   ← v1 is dead
Segment 2:  [ k3=v3 ] [ k2=DEL ] [ k4=v4 ]   ← v2 is dead
Segment 3:  [ k1=v1'' ] ...                  ← v1' is dead
```

The in-memory index points only at the latest version, but disk holds them
all. We need **compaction** — a background process that rewrites segments
keeping only live records.

### Offline compaction (the easy version)

```
1. Stop the world (or at least pause writes).
2. For each key in the in-memory index:
     read its value from (file_id, offset)
     append it to a new compacted segment
3. Atomically swap: delete old segments, rename new segment.
```

Simple and correct, but pauses writes — unacceptable for a real KV store.

### Online compaction (the follow-up question)

The trick: **compaction is just another stream of writes**, so we feed it
through the same WAL we already have.

```
1. Pick a set of input segments S = {s1, s2, ...} to compact.
2. Open a new output segment s_new.
3. For each live record (key, loc) in the in-memory index where loc.file_id ∈ S:
      a. Read value from old location (loc).
      b. Append value to s_new at new offset O.
      c. Append <COMPACT_MOVE, key, old_loc, new_loc> to WAL, fsync.
      d. CAS-style update of the index:
           if index[key] == old_loc:    # still pointing at the old version
               index[key] = new_loc     # safe to swap
           else:
               # a concurrent PUT already moved the key to a newer version.
               # The bytes we wrote in s_new are now garbage; ignore.
4. After all keys processed, append <COMPACT_DONE, S, s_new> to WAL, fsync.
5. Delete s1, s2, ... from disk.
```

Why this works:

- **Concurrent writes are safe.** A foreground `PUT(k, v')` happens while
  compaction is mid-flight. If the `PUT` lands first, the index points at the
  newer location; the `COMPACT_MOVE`'s CAS check sees a mismatch and skips.
  If the `COMPACT_MOVE` lands first, the subsequent `PUT` simply overwrites
  the index normally. Either way, the index always points at the latest live
  value.
- **Crash during compaction is safe.** After a crash mid-compaction, recovery
  replays the WAL. Each `COMPACT_MOVE` is idempotent (same CAS check). If we
  crashed before `COMPACT_DONE`, the old segments still exist and we'll just
  reach a consistent state pointing at *either* old or new locations
  depending on what was applied. The output segment may be partially built;
  that's fine — it's just garbage we'll skip on the next compaction round.
- **Orphan bytes are reclaimable.** Any value bytes written to `s_new` that
  the index doesn't point to are dead and will be dropped by the *next*
  compaction. Cost = a bit of disk; correctness = preserved.

### Choosing what to compact

Standard heuristic — same as LSM trees:

| Policy | Idea |
| ------ | ---- |
| **Size-tiered** | Compact several similarly-sized segments into one big segment. |
| **Leveled** | Maintain levels L0, L1, ... where each level is K× the previous. Promote from L_i to L_{i+1}. |
| **Stale-ratio threshold** | Track per-segment "fraction of bytes that are dead." Compact when > 50%. |

For a single-node Bitcask-style store, **stale-ratio + size-tiered** is
simplest and good enough.

---

## Phase 6: Other Failure Modes

| Failure | Mitigation |
| ------- | ---------- |
| **Process crash** | WAL replay (Phase 4). |
| **OS crash / power loss** | `fsync` on WAL before ack. The OS may buffer further data-file writes; that's fine — only the WAL must be durable, and the data file's bytes are referenced by WAL only after fsync. |
| **Torn write** | Per-record CRC. Truncate WAL at the last valid record. |
| **Disk corruption (silent bit flip)** | Per-record CRC on data files too; on read mismatch, return error. With single disk there's no way to *recover* the bytes — that's why Part 2 introduces replication. |
| **Disk full** | Refuse new writes (return error to client). Don't crash. Background compaction frees space. |
| **Index doesn't fit in RAM** | Out of scope per the prompt. Real-world fix: switch to LSM with on-disk sorted files (LevelDB / RocksDB). |

---

## Phase 7: This Is Basically a Tiny LSM Tree

The prompt explicitly mentions "类似 log-structured merge tree的思路." Worth
making the connection explicit, because the interviewer probably wants you to:

| Bitcask-style (this design) | LSM tree (LevelDB / RocksDB) |
| --------------------------- | ---------------------------- |
| In-memory **hash** index over all keys | In-memory **sorted** structure (memtable, e.g. skiplist) — flushed to disk as SSTables |
| All keys must fit in RAM | Only the **memtable + bloom filters + sparse index** must fit in RAM |
| Append-only data files | Sorted, immutable SSTables |
| Compaction merges live records into a new file | Compaction merges sorted SSTables levelwise |
| Range scans hard (hash index) | Range scans natural (SSTables are sorted) |
| WAL replay rebuilds the hash index | WAL replay rebuilds the memtable |

When the interviewer asks "what if the keys *don't* fit in RAM?", the
answer is **"switch to an LSM tree."**

---

## Common Mistakes (Part 1)

| Mistake | Why it's bad | Fix |
| ------- | ------------ | --- |
| Mutate data files in place | Random writes are slow; partial-write corruption. | Append-only segments + compaction. |
| Acknowledge before fsync | Crash loses acknowledged writes. | fsync the WAL on the critical path. |
| fsync per record with no batching | ~100 µs per fsync ⇒ throughput ceiling. | Group commit: fsync once per batch of pending writes. |
| WAL grows forever | Recovery time unbounded. | Periodic checkpoint + truncate. |
| Stop-the-world compaction | Latency spikes; can't run on a live system. | Online compaction via WAL-recorded `COMPACT_MOVE`. |
| Forget the CAS during compaction | A concurrent `PUT` can be silently overwritten by a compaction move. | Compaction's index update must be conditional on `old_loc`. |
| No CRC on records | Silent corruption goes undetected. | CRC32 per record, validated on read. |

---

## Key Talking Points (Part 1)

| Topic | What to say |
| ----- | ----------- |
| **In-memory hash index + on-disk segments** | "Keys fit in RAM, values don't, so the index lives in RAM and points at offsets in immutable on-disk segments." |
| **Append-only + WAL** | "Sequential writes are 100× faster than random, and append-only makes recovery trivial — the WAL is the source of truth." |
| **fsync is the durability boundary** | "A write isn't durable until WAL fsync returns; that's the line we cross before ack." |
| **Recovery = WAL replay** | "Replay from the last checkpoint to rebuild the in-memory index." |
| **Online compaction via WAL** | "Treat each compaction move as a regular WAL-logged operation with a CAS update of the index — same recovery path handles it." |
| **Relationship to LSM** | "This is essentially Bitcask. If the keys don't fit in RAM, we'd switch to an LSM tree like LevelDB." |
| **Recommended reading** | *Designing Data-Intensive Applications*, Chapter 3 ("Storage and Retrieval"). |

---

# Part 2: Distributed KV Store with Versioning, Time Travel, and Strong Consistency

Now scale Part 1 horizontally and add three big requirements:

1. **Global versioning** — every write has a version; old versions are queryable.
2. **Time travel** — `GET(key, at_timestamp)` returns the value as of that point in time.
3. **Strong consistency** — linearizable reads and writes across the cluster.

This is essentially **Spanner / CockroachDB / FoundationDB territory**.

---

## Phase 1: Data Model — Keys Have Histories, Not Values

The biggest mental shift from Part 1: storage keys are
`(user_key, version)` pairs, not bare `user_key`s.

```
Logical view:                     Physical layout:
  k1  →  v1' (latest)               (k1, v1, ts=100)
                                    (k1, v2, ts=200)
                                    (k1, v3, ts=350)
  k2  →  v3 (latest)                (k2, v1, ts=120)
                                    (k2, v2, ts=400)
```

Reads:

| Query | Implementation |
| ----- | -------------- |
| `GET(k)`           | Read latest version: `MAX(ts) WHERE storage_key starts with k` |
| `GET(k, at_ts)`    | Read latest version `≤ at_ts` |
| `SCAN(k1..k2, at_ts)` | Range scan with per-key MVCC pick |

Writes:

| Op | Effect |
| -- | ------ |
| `PUT(k, v)`       | Append `(k, v, ts=now())` — never mutate older versions. |
| `DELETE(k)`       | Append `(k, ⟂tombstone, ts=now())` — older versions still readable. |

This is **MVCC** (multi-version concurrency control) — the same design
PostgreSQL, Spanner, and CockroachDB use. Time travel falls out for free.

### File storage layout

Use **sorted, immutable SSTables** keyed by `(user_key, version_desc)`:

```
SSTable on disk, sorted by (user_key ASC, version DESC):

  (k1, v=300, "world")     ← latest
  (k1, v=200, "hello")
  (k1, v=100, "hi")
  (k2, v=400, "...")
  (k2, v=120, ...)
  ...
```

Sorting by descending version means **a `GET(k, at_ts)` is one seek** —
binary-search to `(k, ⌊at_ts⌋)` and take the next record.

Old versions are eventually GC'd by a **retention policy** (e.g. "keep all
versions for 7 days, daily snapshots for 90 days") — exactly like
filesystem snapshots.

---

## Phase 2: Replication — Raft per Partition

For strong consistency under failures, each piece of data needs to be
replicated and ordered through a **consensus protocol**. Standard choice:
**Raft**.

```
        Replication Group for partition P (3 replicas)

      ┌─ replica A (LEADER) ─┐
      │   Raft log:          │ ──┐
      │   [op1][op2][op3]    │   │  AppendEntries (with quorum=2)
      └──────────────────────┘   │
                ▲                ├─►  ┌─ replica B (follower) ─┐
                │                │    │  Raft log: [op1][op2]  │
                │                │    └────────────────────────┘
                │                │
            client                ─►  ┌─ replica C (follower) ─┐
                                      │  Raft log: [op1][op2]  │
                                      └────────────────────────┘
```

### How Raft works (thumbnail)

1. **Leader election.** Replicas elect one leader via randomized election
   timeouts. Only the leader accepts writes.
2. **Log replication.** Client writes go to the leader → leader appends to
   its log → replicates to followers via `AppendEntries` RPC.
3. **Quorum commit.** A log entry is *committed* once a majority (≥ N/2 + 1)
   has persisted it. Then the leader applies it to the state machine
   (the KV store) and acks the client.
4. **Safety.** Raft guarantees: at most one leader per term, committed
   entries are never lost, all replicas apply the same log in the same
   order.
5. **Failure recovery.** A new leader is elected on timeout. It brings
   followers up to date by replaying its log.

For our KV store, **the Raft log entry IS the WAL entry from Part 1**:
`<PUT, key, value>` / `<DEL, key>`. Each replica applies the committed
entries to its local Bitcask/LSM-style storage. We get strong consistency
across replicas for free, because all replicas execute the same log in the
same order.

### Linearizable reads — three options

| Option | Cost | Guarantee |
| ------ | ---- | --------- |
| **Read from leader + heartbeat check** | 1 RTT to leader, leader checks it's still leader via a heartbeat round | Linearizable. Cheapest. |
| **Read index** (Raft optimization) | 1 round of `AppendEntries` heartbeats | Linearizable. Skips appending a no-op. |
| **Read from any replica with lease** | Local read, leader holds a time-bounded lease | Linearizable *if* clocks are bounded (Spanner-style). |

For an interview, "**reads go through the Raft leader, which confirms its
leadership via a heartbeat**" is the safe canonical answer.

---

## Phase 3: Partitioning (Sharding)

One Raft group can't hold the whole dataset (each replica needs the full
state). So the keyspace is partitioned, and **each partition is its own Raft
group** — exactly like Spanner's "tablet groups" or CockroachDB's "ranges."

```
keyspace  =  [    P1    |    P2    |    P3    |    P4    | ...]
                 │         │         │
                 ▼         ▼         ▼
              Raft RG1  Raft RG2  Raft RG3   ← independent Raft groups
              {A,B,C}   {C,D,E}   {A,D,F}    ← replicas spread across nodes
```

### Choice of partitioning scheme

| Scheme | Pros | Cons |
| ------ | ---- | ---- |
| **Hash(key) mod N** | Uniform load. | Range scans = fan-out to all partitions. Re-hashing on resize moves a lot of data. |
| **Consistent hashing** | Adding/removing a node moves only `1/N` of data. | Still kills range scans. |
| **Range partitioning** (Spanner-style) | Range scans are local. Splits naturally for hot ranges. | Skewed key distribution = hot partitions. |

For a system with **time-travel range scans**, range partitioning is the
clear winner. Each partition owns `[start_key, end_key)`. A central
**metadata service** (`shard_id → replica set`) tells clients where to find
a given key.

### Repartitioning with consistent hashing

If we go the hash-partitioned route, repartitioning works as follows:

```
1. Add a new node N with vnodes spread across the hash ring.
2. For each vnode now owned by N:
     a. The previous owner streams its data to N (still serving reads).
     b. Once N has caught up, the metadata service flips the assignment.
     c. N becomes the leader for that vnode's Raft group; the old owner
        leaves the group.
3. Old owner garbage-collects the data after a grace period.
```

Strong consistency is preserved because the assignment flip is itself a
consensus-replicated operation in the metadata service — clients see a
single, totally-ordered sequence of "who owns what."

### Splitting a hot range (Spanner-style)

```
1. Detect a hot range (e.g. > X req/s or > Y bytes).
2. Pick a split key (median of recent traffic).
3. Atomically (via Raft) split the range into two new ranges; the new range
   keeps the same replica set initially.
4. Optionally rebalance one of the halves to a less-loaded node.
```

---

## Phase 4: Versioning & Time Travel — The Cluster Clock

For **global** versioning to work, every write across the cluster needs a
totally ordered timestamp that is **monotonic** and **comparable across
partitions**.

| Approach | How | Used by |
| -------- | --- | ------- |
| **Single timestamp oracle** | One service hands out monotonically increasing timestamps. Simple but a global SPOF/bottleneck. | TiDB (early), Percolator |
| **TrueTime (bounded uncertainty clocks)** | Hardware-backed clocks (atomic + GPS) give `[earliest, latest]` bounds. Wait out the uncertainty before commit. | Spanner |
| **HLC (Hybrid Logical Clock)** | Combine wall clock + logical counter. Monotonic, captures causality, no special hardware. | CockroachDB, MongoDB |

For an interview, **HLC** is the modern answer: cheap, doesn't need GPS,
gives the same guarantees you need for snapshot reads ("everything I write
after T is visible at T'+ε").

A `GET(k, at_ts)` then becomes:

```
1. Route to the partition owning k.
2. Send GET to the leader (or any replica with a lease ≥ at_ts).
3. Replica's MVCC layer returns the latest version with ts ≤ at_ts.
4. Across-partition snapshot reads use the same at_ts everywhere → consistent.
```

---

## Phase 5: Failure Scenarios

| Failure | What happens |
| ------- | ------------ |
| **Single replica down** | Raft group has 2/3 replicas → still has quorum → no impact on availability. New replica added later. |
| **Leader down** | Followers timeout, election in 100–500 ms, new leader takes over. Brief unavailability per partition. |
| **Network partition** | Minority side cannot make progress (no quorum). Majority side keeps serving. **Strong consistency preserved** (no split brain). |
| **Whole node down** | All Raft groups it participated in run with one fewer replica. Membership change adds a fresh replica on another node. |
| **Disk corruption on one replica** | Detected via checksums on read; replica is removed from the group and rebuilt from peers. |
| **Metadata service down** | Clients use cached `key → partition → replicas` mappings; new placements paused until metadata service recovers. The metadata service is itself Raft-replicated. |
| **Whole datacenter down** | If replicas are spread across DCs (3 DCs, replication factor 3+), majority survives in the remaining DCs. Latency goes up because writes need a remote ack. |
| **Clock skew (HLC)** | HLC tolerates skew up to its bound; reads get a consistent snapshot anyway. With TrueTime, commit-wait absorbs the uncertainty. |
| **Stale read from old leader** | Heartbeat-confirmed reads detect that the old leader has been deposed; client retries on the new leader. |

---

## Phase 6: Service Discovery

Clients need to find:

1. **Where is the metadata service?** Bootstrap address (DNS, fixed config,
   or a Zookeeper-style anchor).
2. **For key `k`, which partition owns it?** Cached lookup against the
   metadata service. Cache invalidated on routing errors.
3. **For partition `P`, who is the current Raft leader?** Cached; on
   `NotLeader` error from a follower, the response includes a redirect to
   the current leader.

Metadata service options:

| Option | Notes |
| ------ | ----- |
| **etcd / ZooKeeper / Consul** | Off-the-shelf Raft/Paxos-based KV stores. Good for small, slow-changing config. |
| **Self-hosted on the same Raft stack** | Spanner/CockroachDB do this — the metadata is just one well-known partition (`Range 0`). |

Clients keep a **routing cache**; on a stale-cache miss they refresh from
the metadata service and retry. This is the same pattern as DNS or HBase's
`META` table.

---

## Phase 7: Wire Format — Why Protobuf?

> **Protobuf** = Protocol Buffers, Google's binary serialization format with
> a strongly-typed schema (`.proto` file).

Why every distributed KV store uses it (or something like it):

| Property | Why it matters here |
| -------- | ------------------- |
| **Strongly typed schema** | Servers in different languages (Go, C++, Java) agree on the message structure at compile time. |
| **Compact binary encoding** | 3–10× smaller than JSON on the wire — important for billions of replication messages. |
| **Backward / forward compatibility** | Add a field with a new tag number; old code ignores it, new code uses it. Critical for rolling upgrades of a long-lived cluster. |
| **Code generation** | `protoc` produces native types in every language → no hand-written parsers, no parser bugs. |
| **Pairs with gRPC** | Protobuf is the default IDL for gRPC, which gives you HTTP/2 streaming, deadlines, cancellation, auth — everything inter-node RPC needs. |

A typical message in this design:

```proto
message PutRequest {
  bytes  key       = 1;
  bytes  value     = 2;
  uint64 timestamp = 3;   // HLC
}

message AppendEntriesRequest {
  uint64 term          = 1;
  uint64 leader_id     = 2;
  uint64 prev_log_idx  = 3;
  uint64 prev_log_term = 4;
  repeated LogEntry entries = 5;
  uint64 leader_commit = 6;
}
```

Compared with JSON: same information in ~30% of the bytes, parsed at
gigabytes/sec, and a `.proto` file is your one source of truth that all
clients/servers compile against.

Alternatives worth knowing: **Thrift** (Facebook, similar idea), **FlatBuffers**
(zero-copy reads, used by games), **Avro** (Hadoop ecosystem,
schema-per-file). Protobuf is the default for new RPC-heavy systems.

---

## Phase 8: Time Range Search

The MVCC layout in Phase 1 already gives us **point-in-time** reads:

| Existing API | Cost |
| ------------ | ---- |
| `GET(k, at_ts)` | One seek (SSTable sorted by `(key, ts DESC)`). |
| `SCAN(k1..k2, at_ts)` | One sequential scan, MVCC-pick per key. |

What it does **not** give us cheaply:

| New query shape | Why the existing layout is bad for it |
| --------------- | ------------------------------------- |
| `HISTORY(k, [t1, t2])` — all versions of one key in a time window | OK on the primary index — bounded scan within the key's MVCC chain. |
| `CHANGED_KEYS([t1, t2])` — every key that had any write in the window | Disaster — you'd scan **every key** because the primary is keyed by user_key, not time. |
| `SCAN(k1..k2, [t1, t2])` — all `(k, v, ts)` where `k ∈ [k1, k2]` and `ts ∈ [t1, t2]` | OK if the *key range* is selective; bad if the *time range* is the selective dimension. |
| `DIFF(t1, t2)` — for each key, value at `t1` vs value at `t2` | Two snapshot reads + a per-key compare; expensive if the diff is small relative to the corpus. |
| Changefeed / CDC tail | Need to deliver writes in commit-time order across all keys. |

The fix is the standard one for any "wrong access pattern" problem: **add a
secondary index keyed by time**.

### A second sorted view, keyed by `(version, user_key)`

```
Primary index (Phase 1, unchanged) — sorted by (user_key ASC, version DESC):
  (k1, ts=300, "world")
  (k1, ts=200, "hello")
  (k1, ts=100, "hi")
  (k2, ts=400, ...)
  (k2, ts=120, ...)

Time index (new) — sorted by (version ASC, user_key ASC):
  (ts=100, k1, ptr_to_primary)
  (ts=120, k2, ptr_to_primary)
  (ts=200, k1, ptr_to_primary)
  (ts=300, k1, ptr_to_primary)
  (ts=400, k2, ptr_to_primary)
```

The time index is much smaller than the primary because each entry holds
**a pointer (or just the `(key, ts)` tuple)**, not the value itself. That
keeps the secondary's storage cost to ~5–10% of the primary.

### How the new queries are answered

| Query | Plan |
| ----- | ---- |
| `HISTORY(k, [t1, t2])` | Primary index: binary-search to `(k, t2)`, sequentially scan back to `(k, t1)`. |
| `CHANGED_KEYS([t1, t2])` | Time index: binary-search to `t1`, scan forward until `ts > t2`, dedupe keys. |
| `DIFF(t1, t2)` | Time index over `(t1, t2]`: every entry is one changed key. Optionally fetch its value at `t1` and `t2` from the primary. |
| `SCAN(k1..k2, [t1, t2])` | **Choose at plan time** — if `(k2 − k1)` is small, walk the primary; if `(t2 − t1)` is small, walk the secondary and filter by key range. (Standard cost-based selectivity.) |
| Changefeed | Long-poll the time index from the last seen timestamp; emit each new entry as a CDC event. |

### Keeping the two indexes consistent

Two writes per `PUT` is a write-amplification problem. The right solution
depends on what consistency you need.

| Approach | Mechanism | Trade-off |
| -------- | --------- | --------- |
| **Synchronous, in-Raft** | Both index updates are part of the same Raft log entry. Atomic by construction; replicas apply both or neither. | Write amplification, but consistency is free. **Recommended.** |
| **Asynchronous via WAL/Raft tail** | A background indexer tails the Raft log and builds the time index. | Cheap for the hot path, but the secondary lags and can disagree briefly. Fine for analytics; not for "last write must appear in the changefeed before ack." |
| **Synchronous to primary, eventually consistent secondary** | Primary fsync acks the writer; a separate streaming job builds time-bucketed secondary segments. | Same trade-off, lower coupling, easier to scale the indexer separately. |

### Time-bucketed segments — partitioning the secondary

A time index keeps growing monotonically by definition. Like CDC streams
and time-series databases (TimescaleDB hypertables, Druid time chunks),
**partition the secondary by time bucket**:

```
time-index/
  2026-05-07/00:00.sst           ← 1-hour bucket
  2026-05-07/01:00.sst
  2026-05-07/02:00.sst
  ...
  2026-05-07/index.manifest      ← maps time ranges → segment paths
```

Now `CHANGED_KEYS([t1, t2])` reads only the segments whose buckets overlap
`[t1, t2]`. A query for "the last hour" reads exactly one segment;
"yesterday" reads 24. This is **time pruning** — same idea as the
service-per-day index pattern in
`DesignDistributedMetricsSystemQuick.md` Deep Dive E.

Old time buckets tier to S3 just like Druid / Kafka tiered storage. The
manifest stays on the broker so reads transparently redirect to S3.

### Snapshot consistency for time-range reads — the watermark

Subtle but important: when you ask `CHANGED_KEYS([t1, now])`, what's the
"now"?

You must serve the query from a timestamp `ts_safe` such that **every
write committed at `ts ≤ ts_safe` is durably indexed**, otherwise you'd
return a result that misses still-in-flight writes.

```
ts_safe = min over all partitions of "latest committed log index, applied
                                       to time index, on a quorum"
```

In CockroachDB this is the **closed timestamp** mechanism. In streaming
systems it's a **watermark**. Both mean "no new events older than this
will appear."

For a time-range query:

```
1. Client requests CHANGED_KEYS([t1, t2]).
2. Coordinator computes ts_safe across all involved partitions.
3. If t2 > ts_safe, either:
     a. Block briefly until ts_safe ≥ t2  (bounded staleness, e.g. 1 s),
     b. OR cap the response at ts_safe and return "results up to ts_safe".
```

This is the same pattern Spanner uses for snapshot reads at user-supplied
timestamps.

### Distributed considerations — per-partition time indexes

With **one Raft group per partition** (Phase 2), each partition maintains
its own local time index. So the global view is a *union of N
time-sorted streams*:

```
       partition P0 time-index ──┐
       partition P1 time-index ──┤
                                  ├──►  Coordinator merges streams
       partition P2 time-index ──┤      (k-way merge by ts)
                                  │
       partition P3 time-index ──┘
```

For a `CHANGED_KEYS([t1, t2])`:

```
1. Client → Coordinator.
2. Coordinator routes via metadata service to the partitions whose
   key range intersects the predicate (or all partitions if no key
   filter is given).
3. Each partition returns its slice of the time-bucket(s) overlapping
   [t1, t2], sorted by ts.
4. Coordinator does a streaming k-way merge to produce a globally
   time-sorted result.
```

This is the **scatter-gather** pattern — same shape as the inverted-index
log query in `DesignDistributedMetricsSystemQuick.md` and the
doc-partitioned search in `DocPredicateSearch.java` follow-up 2. Tail
latency is dominated by the slowest partition; per-partition timeouts +
partial results bound the blast radius of one slow node.

### GC interplay — retention is now coupled

When MVCC GC reclaims an old version (per the retention policy), the
**time-index entry must be reclaimed in the same operation**. Otherwise
the secondary points at primary entries that no longer exist.

```
GC pass over partition P, watermark = "everything older than 30 days":
  1. Walk primary; for every (k, ts < watermark) version that is
     superseded by a newer one, mark it for deletion.
  2. Walk the time-bucket whose range falls below the watermark; mark
     every entry for deletion.
  3. Atomically rewrite both: new compacted primary + drop old time-bucket
     segment. The Raft membership service publishes the manifest swap.
```

Result: an entire time-bucket file is *truncated* once its range falls
below retention — sequential I/O, very cheap. Same property the
log-segmented designs throughout this folder rely on.

### Capacity sanity check

Continuing the Part 1 numbers:

| Item | Value |
| ---- | ----- |
| Writes/sec | 100 K |
| Time index entry size | ~50 B `(ts u64, key 32 B, ptr 16 B)` |
| Time index byte rate | 5 MB/sec ≈ 400 GB/day |
| 1-hour bucket size | ~18 GB |
| 30-day retention | ~12 TB total time-index volume |

Order of magnitude smaller than the primary (which holds full values).
**Don't skip the secondary because of cost; it's cheap relative to the
data it indexes.**

### When NOT to add a time index

- **Only point-in-time reads are needed.** If users only ask `GET(k, at_ts)`,
  the primary alone is sufficient. Don't pay for indexing you won't use.
- **Time range can be answered by the primary's MVCC chain.** For
  per-key history (one user's audit log), the primary is already keyed
  the right way.
- **An external CDC stream already exists.** If writes are also published
  to Kafka (e.g. via the outbox pattern from DDIA Ch 11), the time index
  is *already* materialized externally — point analytics queries at
  Kafka instead of duplicating.

The case for the time index is **internal cross-key time-window queries**
that need to be served from the same KV store, with the same consistency
guarantees as point reads.

### Use cases worth naming in an interview

| Use case | What this enables |
| -------- | ----------------- |
| **Audit log / compliance** | "Show me every write between 14:00 and 14:30 yesterday." |
| **CDC feed** | Stream every change to downstream consumers (search index, warehouse). |
| **Point-in-time backup** | A `RESTORE_TO(t)` is a snapshot read; a *delta* backup between `t1` and `t2` is exactly `CHANGED_KEYS([t1, t2])` joined with `GET(k, t2)`. |
| **Debugging** | "What changed in the 5 minutes before the incident at 14:23?" |
| **Cache invalidation** | Downstream cache subscribes to `CHANGED_KEYS`; invalidates only what actually changed. |

---

## Phase 9: Range Queries (Key Range Scans)

Phase 8 added a *time*-range index. This phase adds the orthogonal *key*-range
query — the workhorse of any non-trivial KV store:

| New API | Semantics |
| ------- | --------- |
| `SCAN(start_key, end_key, at_ts?, limit?, reverse?)` | Return all `(k, v)` with `start_key ≤ k < end_key` as of `at_ts` (default = now), in sorted order. |
| `PREFIX_SCAN("user:42:")` | Sugar for `SCAN("user:42:", "user:42;")` — scan every key with a given prefix. |
| `COUNT(start_key, end_key, at_ts?)` | Number of live keys in the range. Pushdown candidate. |
| `RANGE_DELETE(start_key, end_key)` | Delete an entire key range atomically (writes a single range tombstone). |

The 2-D combo `SCAN(k1..k2, [t1, t2])` from Phase 8 falls out by
intersecting this phase's plan with the time-index plan; we'll touch that
at the end.

### Single-node: the storage engine has to be sorted

Part 1's design used an **in-memory hash table** as the primary index.
Hash tables answer `GET(k)` in O(1) but cannot answer `SCAN(a, b)` —
hashing destroys ordering by construction. So adding range scans forces
one of three changes on the single-node design:

| Option | Cost | Lookup latency | Range scan |
| ------ | ---- | -------------- | ---------- |
| **A. Replace hash with sorted in-memory index** (skiplist / red-black tree / B-tree) | Same memory budget, just a different structure | `O(log n)` instead of `O(1)` | `O(log n + k)` — natural |
| **B. Switch to an LSM tree** (Section 3.1 of DDIA) | Index no longer needs to fit in RAM; only memtable + Bloom filters do | `O(log n)` average + Bloom-filter pruning | Native — SSTables are sorted on disk |
| **C. Keep both** (hash + sorted secondary) | 2× memory for the index | `O(1)` for point | `O(log n + k)` |

For an interview, the canonical answer is **B (LSM tree)** because:

- It's already mentioned in Part 1 Phase 7 as "what to do when keys don't fit in RAM."
- SSTables are *intrinsically sorted*, so range scans require no extra index.
- The combined-with-Phase-8 secondary time index also benefits from the same LSM machinery.

Option A is fine when the prompt's "all keys fit in RAM" assumption still
holds — it's a one-line change (`HashMap` → `TreeMap` / skiplist) and
keeps point lookups fast.

### The merging iterator (the core mechanic)

Once the data is sorted, every range scan reduces to **a single primitive**:
walk multiple sorted sources in lock-step, emit each key once at its
newest visible version.

```
SCAN(a, b, at_ts):

  iterators:
     mem_iter   = memtable.iterator(start=a)
     l0_iters   = [s.iterator(start=a) for s in L0 SSTables newest..oldest]
     l1_iter    = manifest.lookup(L1, range=[a,b]).iterator()
     l2_iter    = manifest.lookup(L2, range=[a,b]).iterator()
     ...

  heap = min-heap of (current_key, level, version)

  while heap not empty:
      (k, level, v) = heap.pop()
      if k >= b:        break
      if first time we've seen k:
          if version_visible_at(at_ts, v) and not tombstone:
              emit (k, value_at(level, v))
          # else MVCC says skip / it's a tombstone
      advance the iterator we just popped from
      push the next entry from that iterator into the heap
```

Three properties that fall out for free:

- **Newest-version-wins semantics.** The merging iterator emits each key
  exactly once, at its newest visible version. Stale versions in lower
  levels are absorbed into the dedup logic.
- **Tombstones suppress lower levels.** A range tombstone in the memtable
  hides everything in matching SSTables below — same idea as a per-key
  delete, just over a range.
- **MVCC `at_ts` filter is a free add-on.** Skip versions newer than
  `at_ts`; emit the highest visible one. This is what makes
  `SCAN(a, b, at_ts)` a snapshot read.

This is the same primitive **compaction** uses — just exposed as a query
interface. Building one merging iterator and using it for both reads and
compaction is what every modern LSM engine (RocksDB, LevelDB, Pebble) does.

---

### Distributed: range partitioning becomes mandatory

Hash partitioning destroys range information by construction. A
`SCAN("user:1000", "user:5000")` over a hash-partitioned cluster has no
choice but to **scatter-gather to every partition** — even partitions
that hold zero keys in the range.

```
Hash partitioning (BAD for range scans):
  SCAN(a, b) ──► fan out to ALL N partitions ──► merge ──► return
  Tail latency = slowest of N partitions, regardless of N.

Range partitioning (GOOD for range scans):
  SCAN(a, b) ──► metadata: which partitions overlap [a, b]?
              ──► [P3, P4, P5]  (only the 3 that intersect)
              ──► sub-scan each, k-way merge results
```

This is why Spanner, CockroachDB, HBase, Bigtable, and FoundationDB all
use **range partitioning** — for any system whose dominant query shape
is a range or prefix scan, hash partitioning is a non-starter.

#### A concrete walkthrough

```
Client: SCAN(start="user:1000", end="user:5000", at_ts=now, limit=10000)

1. Coordinator → metadata service:
     "Which partitions overlap [user:1000, user:5000)?"
   → [P3 (owns [user:0500, user:2000)),
      P4 (owns [user:2000, user:3500)),
      P5 (owns [user:3500, user:5500))]

2. Coordinator dispatches a sub-scan to each partition's leader,
   each with the *trimmed* range that lies inside that partition:
     P3: SCAN("user:1000", "user:2000", at_ts, partition_limit=10000)
     P4: SCAN("user:2000", "user:3500", at_ts, partition_limit=10000)
     P5: SCAN("user:3500", "user:5000", at_ts, partition_limit=10000)

3. Each partition leader runs the merging iterator locally,
   streams sorted (k, v) pairs back over a single gRPC stream.

4. Coordinator does an N-way merge across the streams:
     - min-heap keyed by (current_key, partition_id)
     - emits to client in sorted order
     - stops when limit reached or all streams exhausted
     - back-pressures slow partitions via stream-level flow control

5. Client receives sorted, paginated results.
```

Because the partitions are **already sorted internally** AND **each owns a
disjoint key range**, the coordinator's merge is a simple k-way merge.
There's no cross-partition reordering — partition order *is* key order
when ranges are laid out in sequence.

#### Streaming + pagination via cursors

A range scan can match millions of rows. Don't buffer them in the
coordinator. Instead:

- Stream results via gRPC server-side streaming.
- Apply a **page size** (e.g. 1000 rows per page).
- Return a **continuation cursor** = the next key after the last emitted
  one. Continuation: `SCAN(cursor, end, at_ts)`.

Cursor-based pagination is critical because **OFFSET-based pagination is
O(N) per page** — the partition has to walk past every row it already
emitted. Cursors are O(log N) per page (binary search to the cursor key,
resume scan).

#### Mid-scan partition splits

Partitions can split or merge while a long-running scan is in progress:

| Event | What happens to the in-flight scan |
| ----- | ---------------------------------- |
| **Split during scan** (`P4` → `P4a` ∪ `P4b`) | The leader of `P4` returns "scan completed up to key K"; coordinator refreshes the partition map and re-routes the remainder to `P4a`/`P4b` from `K`. |
| **Merge during scan** (`P4a` + `P4b` → `P4`) | Old leader rejects with `RangeMoved`; coordinator routes to `P4`'s new leader and resumes from cursor. |
| **Leader fail-over mid-scan** | Stream errors out; coordinator retries on the new leader from the last-seen cursor. |

The cursor is the **idempotency key** that makes mid-scan failures safe
to retry — a stream interruption is just "scan more starting from K."

This is the same trick CockroachDB and Spanner use; they call it
"resumable scans."

#### Snapshot consistency (`at_ts`)

If the client doesn't pass `at_ts`, **don't** let each partition use its
local clock — that produces an inconsistent global view ("partition
P4 saw a write that P5 didn't, even though they both happened before
the scan").

Instead, the coordinator picks a single `ts_safe` (the closed-timestamp /
watermark from Phase 8) and passes it to every partition. Each partition
serves the scan **as if** at exactly `ts_safe`. The result is a globally
consistent snapshot.

This is also why distributed range scans on a hash-partitioned
eventually-consistent store (Cassandra-style) are so awkward — there's
no closed timestamp to anchor the scan to.

---

### Pushdown — the optimization that earns its keep

A range scan can match millions of rows. Without pushdown, every byte
crosses the network from partition leader → coordinator → client. With
pushdown, the partition leader does the filtering / counting / aggregation
locally and ships only the answer.

| Operation | Naive | With pushdown |
| --------- | ----- | ------------- |
| `SELECT v FROM kv WHERE k IN [a,b) AND v > 100` | Ship all rows; filter at coordinator. | Each partition filters; ships only matching rows. |
| `COUNT(k IN [a, b))` | Ship all rows; coordinator counts. | Each partition returns its count; coordinator sums (4 ints back, not 4M rows). |
| `SUM(v) WHERE k IN [a, b)` | Ship all values. | Each partition returns its partial sum. |
| `LIMIT 100` | Each partition returns 1000s of rows. | Each partition returns ≤ 100; coordinator stops as soon as it has 100 globally. |
| `MIN(k) WHERE k IN [a,b)` | Walk the entire range. | Each partition returns its minimum; coordinator picks one. |

Pushdown turns range scans from "every byte traverses the network" into
"only the answer traverses the network." Required at any meaningful scale.

This is also the bridge from KV-store-as-storage-layer to
KV-store-as-database-engine: once you have pushdown of `LIMIT`, simple
filters, and aggregates, you have ~80% of what an OLTP query layer
actually does.

---

### Hot range mitigation

A range scan can light up one partition while leaving every other
partition idle:

```
P0 [a..g]      cold
P1 [g..n]      cold
P2 [n..t]      cold
P3 [t..z]      cold

SCAN("hadoop_logs:0000", "hadoop_logs:9999")  → all in P1.  P1 is on fire.
```

Mitigations, layered:

1. **Adaptive range split.** When P1's load crosses a threshold, the
   range manager (Phase 3) splits it. Now multiple partitions own the
   hot range; subsequent scans parallelize across them.
2. **Read from any replica with a lease.** Range scans don't need
   linearizability per row — they need a consistent snapshot, which any
   replica with a valid lease at `ts_safe` can serve. This horizontally
   scales reads across the replicas of one Raft group.
3. **Per-replica chunking.** A long scan over one partition can split
   the range across the partition's replicas (replica A scans
   `[t..v)`, replica B scans `[v..x)`, replica C scans `[x..z)`),
   then the coordinator merges. Used by FoundationDB.
4. **Result caching.** If the same range scan recurs (a dashboard query),
   cache results at `ts_safe` and reuse them within a TTL.
5. **Per-tenant query budgets** — same pattern as the log-query budget
   in `DesignDistributedMetricsSystemQuick.md` Deep Dive E. One bad
   client's `SCAN("", "")` should not melt the cluster.

---

### Composing with Phase 8 — the 2-D query

`SCAN(k1..k2, [t1, t2])` — every `(k, v, ts)` where `k ∈ [k1, k2)` and
`ts ∈ [t1, t2]`. Two indexes, pick whichever is more selective:

| Selectivity | Plan |
| ----------- | ---- |
| **Key range narrow, time range wide** (e.g. one user's full history) | Walk the **primary** key-sorted index over `[k1, k2)`, filter each MVCC chain by `ts ∈ [t1, t2]`. |
| **Time range narrow, key range wide** (e.g. last hour of changes across users) | Walk the **time-index** time-bucket(s) overlapping `[t1, t2]`, filter each entry by `k ∈ [k1, k2)`, hydrate values from the primary if needed. |
| **Both narrow** | Either index works; pick the cheaper based on cost-model estimates (smaller estimated scan size wins). |
| **Both wide** | Acknowledge it's expensive; require a per-tenant query budget; consider denying or sampling. |

This is the same plan-time choice every column store / search engine
makes between two access paths — a classic cost-based query plan
decision.

---

### Capacity sanity check (range scan path)

| Scenario | Throughput / Latency |
| -------- | -------------------- |
| Single-partition scan of 100 K keys (avg 1 KB each) | ~100 MB sequential read = single-digit seconds on SSD; sub-second on cache. |
| 8-partition scan of 1 M keys total | k-way merge at the coordinator; bottleneck is network egress, not disk. |
| Concurrent range scans | Each replica serves O(N concurrent streams); flow-control prevents OOM. |

The dominant cost at scale is **network egress + serialization**, not
disk I/O — which is why pushdown matters more than disk-IO tuning.

---

### When NOT to add full range scan support

Be explicit with the interviewer about the boundary:

- **If the workload is purely point lookups by hash key**, range partitioning
  pays a cost (uneven distribution, hot ranges) for a feature you don't
  use. Stick with hash + the existing `GET`. DynamoDB defaulted this way
  for years.
- **If range scans are rare ad-hoc analytics**, a CDC stream into a
  warehouse (Snowflake, BigQuery) is cheaper than building a first-class
  scan API. The KV store stays simple.
- **If the access pattern is "all keys with prefix X, where X is random"**,
  you're describing a search problem — use an inverted index
  (Elasticsearch / your `DocPredicateSearch.java`) rather than a sorted
  KV store.

The case for first-class range scans is **online queries that need
strong consistency, low latency, and key-ordered results** — exactly the
combination that drove Spanner / CockroachDB / FoundationDB to make this
the centerpiece of their design.

---

## Common Mistakes (Part 2)

| Mistake | Why it's bad | Fix |
| ------- | ------------ | --- |
| Single global Raft group for the whole cluster | Doesn't scale — all writes serialize through one leader. | One Raft group per partition. |
| Read from any follower without lease/heartbeat | Stale reads → not linearizable. | Read through leader (with heartbeat) or via lease. |
| Wall clock as version | Clock skew → out-of-order writes. | HLC or TrueTime. |
| Repartition by destructively re-hashing all keys | Massive data movement, downtime. | Consistent hashing or range splits + Raft membership change. |
| Ignore version retention | Storage grows forever. | Time-bounded retention + periodic GC of old versions. |
| Treat metadata service as best-effort | Routes drift, split brain at the routing layer. | Metadata service is itself consensus-replicated. |
| Implement time-range search by scanning the primary | "Find changes in last hour" becomes O(all keys) every time. | Build a secondary index keyed by `(ts, key)` with time-bucketed segments. |
| Async secondary index without a watermark | `CHANGED_KEYS` may miss in-flight writes silently. | Serve time-range queries at `ts_safe` (closed timestamp / watermark). |
| Forget to GC the secondary alongside the primary | Secondary points at deleted primary versions; reads break. | Coordinated GC pass — rewrite both indexes in the same retention sweep. |
| Range scans on a hash-partitioned cluster | Every scan becomes a fan-out to ALL partitions, regardless of selectivity. | Range partitioning, or at least a hybrid (hash for point + a sorted secondary for scans). |
| OFFSET-based pagination of range scans | Each page re-walks past every previous page → O(N²) total. | Cursor = last-key-seen; each page is O(log N + page_size). |
| Letting each partition pick its own snapshot timestamp | Inconsistent global view — same write visible on one shard, missing on another. | Coordinator picks one `ts_safe` (closed timestamp) and passes it to every shard. |
| No pushdown for LIMIT / filters / aggregates | Every matching byte crosses the network; coordinator OOMs on large ranges. | Push `LIMIT`, predicates, and aggregates down to the partition leaders. |

---

## Key Talking Points (Part 2)

| Topic | What to say |
| ----- | ----------- |
| **MVCC = time travel for free** | "Every write becomes a new version keyed by `(user_key, ts)`; old versions are read-only and GC'd by retention policy." |
| **Storage layout: SSTables sorted by (key, ts DESC)** | "Latest version is the first hit; `GET(k, at_ts)` is one binary search." |
| **Raft per partition** | "Each shard is its own Raft group, like Spanner tablet groups or CockroachDB ranges." |
| **Linearizable reads via leader heartbeat** | "Leader confirms it's still leader before serving the read; alternatives are read-index and bounded leases." |
| **Range partitioning beats hash for snapshot scans** | "Range scans become local to one partition instead of fanning out." |
| **HLC for global ordering** | "Hybrid Logical Clocks give us a monotonic, partition-aware timestamp without needing GPS-backed clocks." |
| **Consistent hashing for elastic membership** | "Adding/removing a node moves `O(1/N)` of the data; the assignment flip itself is replicated through the metadata Raft group." |
| **Protobuf + gRPC for the wire** | "Compact, typed, schema-evolvable — table stakes for inter-node RPC at this scale." |
| **Secondary time index** for time-range queries | "The primary `(key, ts DESC)` is great for `GET(k, at_ts)`; for `CHANGED_KEYS([t1, t2])` I add a secondary keyed by `(ts, key)` with hourly time-bucketed segments — same idea as Druid time chunks or CockroachDB's rangefeed." |
| **Closed timestamp / watermark** for time-range consistency | "Time-range queries are served at `ts_safe`, the latest timestamp where every partition has durably indexed all writes — borrowed from streaming watermarks." |
| **Range scans need range partitioning** | "Hash partitioning destroys ordering, so any `SCAN(a, b)` becomes scatter-gather to every shard. Range partitioning lets us send the scan only to the partitions whose key range overlaps `[a, b]`." |
| **Merging iterator** — same primitive for compaction and scans | "A scan is a k-way merge over memtable + L0 SSTables + L1 + L2..., emitting newest-version-wins. Compaction reuses the same iterator." |
| **Cursor pagination** for streaming scans | "Page boundary is the last key seen; resuming is O(log N), and stream interruptions become safe retries from the cursor." |
| **Pushdown** of LIMIT / filters / aggregates | "Filters and limits run on the partition leader; only the result crosses the network — same insight that turns a KV store into an OLTP query layer." |

---

## Wrap-Up

| Aspect | Single-node (Part 1) | Distributed (Part 2) |
| ------ | -------------------- | -------------------- |
| Index | In-memory hash table | Same, plus shard routing cache on the client |
| Storage | Append-only segments + WAL | SSTables keyed by `(key, version DESC)` per replica |
| Durability | `fsync` of WAL before ack | Raft quorum commit + local fsync |
| Recovery | WAL replay rebuilds in-memory index | Same per replica; stragglers catch up via Raft log replication |
| Compaction | Online, WAL-logged moves with CAS | Same per replica + cross-partition rebalancing |
| Versioning | Optional (latest only) | Mandatory, MVCC, time-travel via `(key, ts)` |
| Time-range search | N/A | Secondary index keyed by `(ts, key)`, time-bucketed segments, served at a watermark |
| Key-range scan | LSM merging iterator (or sorted in-mem index) | Range partitioning + per-partition merging iterator + coordinator k-way merge + pushdown |
| Consistency | Single process — trivially linearizable | Linearizable via Raft leader reads / read-index |
| Scale | One machine, all keys in RAM | Range-partitioned with one Raft group per range |
| Failure model | Crash / power loss | Above + node loss, network partitions, DC loss, clock skew |

---

## Recommended Reading

- **Designing Data-Intensive Applications** by Martin Kleppmann — Chapter 3
  (Storage and Retrieval) is exactly Part 1; Chapter 5 (Replication) and
  Chapter 9 (Consistency and Consensus) cover Part 2.
- **The Bitcask paper** (Riak) — concrete reference for the single-node design.
- **The LevelDB design doc** — for the LSM-tree alternative when keys don't fit in RAM.
- **The Raft paper** ("In Search of an Understandable Consensus Algorithm").
- **The Spanner paper** — TrueTime, Paxos groups per tablet, MVCC at scale.
- **CockroachDB architecture docs** — most accessible real-world reference for
  "Spanner without TrueTime" using HLC + Raft.
