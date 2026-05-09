# Design a Distributed Message Queue

> Following the canonical interview walkthrough
> ([YouTube: *System Design Interview — Distributed Message Queue*](https://www.youtube.com/watch?v=iJLL-KPqBpM)),
> with cross-references to your other design docs in this folder.

This is **the** general-purpose system-design problem that touches every other
distributed-systems concept (replication, partitioning, consensus, durability,
ordering, delivery semantics). Think Kafka, SQS, RabbitMQ, Pulsar.

The single guiding principle:

> **The producer's `send` is decoupled from the consumer's `receive` by a
> durable, replicated, partitioned log.** Every other decision — push vs.
> pull, ordering, exactly-once, retention — falls out of how strict that
> log's properties need to be.

---

## Phase 1: Scope & Requirements

### Functional Requirements

| API | Purpose |
| --- | ------- |
| `sendMessage(queue, payload)` | Producer enqueues one message. Returns when the system has accepted (durably stored) the message. |
| `receiveMessage(queue)` | Consumer dequeues one message; the message is hidden for some visibility timeout. |
| `deleteMessage(queue, receiptHandle)` | Consumer acknowledges processing — message is permanently removed (or its offset advances). |
| `createQueue(name, opts)` / `deleteQueue(name)` | Admin operations. |

What we **don't** support in v1 (mention to scope down):

- Message routing by topic / pub-sub fan-out (that's a separate "topic + subscriber" feature, like SNS or Kafka consumer groups).
- Strict global ordering (we'll do per-partition ordering).
- Exactly-once delivery as a hard guarantee (we'll do at-least-once + idempotent receivers).
- Server-side message filtering / SQL-style queries.

### Non-Functional Requirements

| Requirement | Target | Why |
| ----------- | ------ | --- |
| **Scalability** | Horizontal — millions of messages/sec across the cluster | Every cloud queue is a multi-tenant firehose. |
| **High availability** | 99.99% per region | Producers can't be blocked by individual node failures. |
| **Performance** | `sendMessage` p99 < 10 ms; `receiveMessage` p99 < 50 ms | Hot path; many apps `send` synchronously inside request handlers. |
| **Durability** | Acknowledged messages survive disk + node + AZ failure | A lost message in a payment queue = a lost transaction. |

### Capacity Math (rough)

| Metric | Value |
| ------ | ----- |
| Tenants                       | 100 K   |
| Avg messages/sec (steady state) | 1 M  |
| Burst messages/sec             | 10 M   |
| Avg message size               | 4 KB   |
| Steady-state bandwidth         | 4 GB/s |
| Daily volume                   | ~340 TB/day |
| Retention (default)            | 4 days |
| Resident data                  | ~1.4 PB |

So the design has to **scale horizontally** at write time and **tier storage** for retention.

---

## Phase 2: The Public API

### Message lifecycle (the SQS-style model)

```
                 ┌──────────┐
                 │ producer │
                 └────┬─────┘
                      │ sendMessage(q, payload)
                      ▼
   ┌────────────────────────────────────────────────────┐
   │     durable, replicated, partitioned log           │
   │     [m1] [m2] [m3] [m4] [m5] [m6] [m7] [m8] ...    │
   └────────────────────────────────────────────────────┘
                      ▲                              │
                      │ deleteMessage(q, receipt)    │ receiveMessage(q)
                      │                              ▼
                                              ┌──────────┐
                                              │ consumer │
                                              └──────────┘
```

When `receiveMessage` returns a message, the message is **invisible** for a
configurable **visibility timeout**:

- Consumer processes and calls `deleteMessage` → permanently removed.
- Consumer crashes / doesn't ack within the timeout → message becomes
  visible again, another consumer can take it (at-least-once delivery).

This single primitive is the simplest queue contract; Kafka uses a slight
variant where consumers track an **offset** instead of explicitly deleting.

### Two rules to state up front

1. **The producer is decoupled from the consumer** — only the queue knows
   where messages are; producers don't talk to consumers directly.
2. **Acknowledgement happens at two layers** — the broker acks the
   producer only after durable storage; the consumer acks the broker only
   after successful processing.

---

## Phase 3: High-Level Architecture

```
                 ┌────────────────────────┐
                 │   Front-End Service    │
                 │ (stateless, autoscaled)│
   producer ──►  │   - auth/authz         │
                 │   - validate           │
                 │   - dedupe cache       │
                 │   - batching           │
                 │   - rate limiting      │
                 └─────────┬──────────────┘
                           │ writes a batch
                           ▼
                 ┌────────────────────────┐
   metadata ◄──► │ Backend (Brokers)      │ ◄── consumer
   service       │ - per-queue partitions │
   (queue→broker)│ - persist + replicate  │
                 │ - serve receives       │
                 └─────────┬──────────────┘
                           │
                           ▼
                ┌──────────────────────────┐
                │  Storage Tier            │
                │  - hot: local disk on    │
                │    broker (short-term)   │
                │  - cold: object store    │
                │    (S3 / GCS) for old    │
                │    log segments          │
                └──────────────────────────┘
```

| Layer | Role | Stateless? |
| ----- | ---- | ---------- |
| **Front-end** | Auth, validation, dedup cache, batching, rate limiting | Yes — autoscale freely |
| **Metadata service** | Maps `queue → partitions → broker leaders` | Tiny, consensus-replicated |
| **Backend brokers** | Own one or more partitions; persist to disk; replicate to peers | No — partition-stateful |
| **Object storage** | Long-term retention of sealed log segments | Externalized |

This layout maps onto Kafka almost 1-for-1 (controller = metadata, brokers = backend, log segments = storage).

---

## Phase 4: Front-End Service

The front-end is the **only thing producers see**. It must be cheap to scale
horizontally and absolutely never block on the backend.

### Responsibilities

| Responsibility | Why |
| -------------- | --- |
| **Auth / TLS termination** | Reject unauthenticated traffic at the edge. |
| **Validation** | Message size, queue exists, payload encoding. |
| **De-duplication cache** | Producer retries are common (network blips). Cache `(producer_id, message_id)` for ~5 min in Redis; reject duplicates. |
| **Batching & compression** | Coalesce many sends into a single broker write. Big throughput win, like the agent-side batching in `DesignDistributedMetricsSystemQuick.md`. |
| **Rate limiting** | Per-tenant token bucket — prevents one noisy producer from starving others. |
| **Routing** | Look up the queue's partition map (cached locally) and send the batch to the leader broker. |

### Why this layer is stateless

All of its state is either:
- **Cached** (partition map, refreshed on routing errors).
- **Externalized** (dedup cache in Redis).

No partition assignment, no follower replicas, no log on the front-end. That's
why we can run hundreds of front-end instances behind a load balancer and
trust them to crash freely.

### The dedup cache trade-off

| Approach | Pros | Cons |
| -------- | ---- | ---- |
| **Strict dedup at front-end** | Avoids duplicate writes if producer retries | Cache miss = duplicate slips through |
| **Idempotent producer ID + sequence number** (Kafka style) | Tracked all the way to broker; no dedup needed at front-end | Producer SDK has to support it |
| **At-least-once + idempotent consumers** | Simplest end-to-end; pushes the burden to the consumer | Consumer apps must be idempotent — the modern industry standard |

The video (and most real systems) settle on **at-least-once + idempotent consumers**: it's the simplest contract, and producers / consumers both keep retries safe by carrying a stable message_id.

---

## Phase 5: Metadata Service

A small, strongly-consistent service that knows:

```
queue_name      → list of partitions
partition_id    → { leader broker, follower brokers, key range }
broker          → { hostname, status, capacity }
```

### Why it must be consensus-replicated

The metadata service is the **single source of truth** for routing. If two
front-ends see different partition maps, you get split-brain writes. So:

- 3-or-5-node cluster, **Raft** (or ZooKeeper / etcd, which both implement Paxos-family consensus).
- Reads are linearizable through the leader.
- Writes (e.g. "this broker died, promote a follower") are infrequent — partition map changes are O(seconds) events, not O(milliseconds).

### What clients cache and how they refresh

```
Front-end / Consumer:
  partition_map_cache = {queue → partitions, partition → leader}
  on every send/receive:
    1. look up partition for the message
    2. send to that partition's cached leader
    3. on "NotLeader" / "PartitionMoved" error → refresh from metadata
    4. retry once
```

Same pattern as HBase META, Kafka's controller, CockroachDB's range cache. **Don't query the metadata service per request** — that would melt it.

---

## Phase 6: Backend — Persistence & Partitioning

This is the meat of the design. We're storing a queue as a **partitioned,
replicated, append-only log**.

### One partition = one log

```
Partition 0 (queue=orders, partition=0):
  segment_001.log  segment_002.log  segment_003.log  ...
        │                │                  │
        ▼                ▼                  ▼
   sealed,        sealed,            active,
   immutable      immutable          appending writes
```

Properties:

| Property | Why |
| -------- | --- |
| **Append-only** | Sequential writes are ~100× faster than random — disks love this. |
| **Segmented** | Old segments can be deleted (retention) or shipped to S3 cheaply. |
| **Immutable once sealed** | Trivially cacheable; no read-write contention. |
| **Per-partition offset** | Consumers track "I've processed up to offset N" — no per-message metadata required. |

This is structurally identical to the WAL + segments + compaction story in
`DesignKeyValueStore.md` Part 1 (Bitcask / LSM). The **append-only durable
log** is the fundamental primitive both designs are built on.

### Sharding strategy

```
queue=orders, partitions=8
   ┌─P0─┐ ┌─P1─┐ ┌─P2─┐ ┌─P3─┐ ┌─P4─┐ ┌─P5─┐ ┌─P6─┐ ┌─P7─┐
   broker│ broker│ broker│ broker│ broker│ broker│ broker│ broker│
     A   │   B   │   A   │   C   │   B   │   C   │   A   │   B
```

Two routing options:

| Strategy | When | Trade-off |
| -------- | ---- | --------- |
| **Round-robin (no key)** | "Don't care about ordering" | Best load balance, no per-key ordering. |
| **Hash(partition_key)** | "All messages for user X must be in order" | One user's messages always to one partition → preserved order, but hot keys = hot partitions. |

This is **the same hash-vs-range partitioning trade-off** from DDIA Ch 6 / your `DesignKeyValueStore.md` Part 2 Phase 3.

### Small messages vs. large messages

The video (and all real queues) treat these as different problems:

| | Small (< 1 MB) | Large (> 1 MB) |
| -- | --------------- | --------------- |
| Where stored | Inline in the partition log | Payload stored in object storage (S3); log holds only the URL |
| Why | Sequential disk writes are fast for small batches | Multi-MB messages would balloon the log and fragment retention |

Same idea as the chunking dance in `DesignSimpleObjectStorage.md` — the user-facing API doesn't change, but the storage path differs by size.

### Storage tiering

```
0–6 hours      :  active segments on local SSD on the broker
6 hours – 4 d  :  sealed segments on local HDD, still on the broker
4 d – retention:  sealed segments uploaded to S3, removed from broker disk
```

The broker keeps a manifest mapping `(partition, offset_range) → S3 object`
so consumers reading old offsets are transparently redirected. This is how
Kafka's tiered storage feature works.

---

## Phase 7: Replication & Durability

The single most important paragraph of the entire design. **Acknowledgement
must follow durable replication, or you will lose messages.**

### Replication model — leader/follower per partition

```
Partition 0 replication group:
  ┌─ broker A (LEADER) ─┐         ┌─ broker B (FOLLOWER) ─┐
  │  log: [m1][m2][m3]  │ ──────► │  log: [m1][m2][m3]    │
  └────────┬────────────┘    │    └───────────────────────┘
           │                 │
       producer              └─►  ┌─ broker C (FOLLOWER) ─┐
                                  │  log: [m1][m2][m3]    │
                                  └───────────────────────┘
```

Per-partition leader election (Raft, or ZooKeeper-coordinated as in older Kafka).

### The "ack=" knob — three durability levels

| `acks` | Producer waits for | Durability | Latency |
| ------ | ------------------ | ---------- | ------- |
| `0` | Nothing — fire and forget | Trivial | Lowest |
| `1` | Leader has written to its disk | Survives leader-disk fsync but **not** leader crash before replicating | Low |
| `all` | Leader + all in-sync followers fsync | Survives any single (or N-1) replica failure | Highest |

Production default = **`acks=all`** with `min_in_sync_replicas=2` (a write
needs at least leader + 1 follower durable before ack). This is the same
"quorum write" idea from DDIA Ch 5.

### How replication actually works

```
producer ──► leader:
   1. leader appends to its local log + fsync
   2. leader sends "AppendEntries" to followers (in parallel)
   3. each follower fsync's, replies "OK at offset N"
   4. once min_isr followers have ack'd, leader marks the offset committed
   5. leader sends "ack" to producer
   6. consumers can only read up to the committed offset (HW = "high watermark")
```

Followers that fall behind get kicked out of the in-sync replica (ISR) set.
A leader can serve writes only with quorum; if too many followers fall out
of ISR, **writes pause** rather than risk durability loss.

### Failure modes

| Failure | Behavior |
| ------- | -------- |
| Follower dies | Leader removes it from ISR, keeps writing with reduced durability. New replica added later. |
| Leader dies | Followers detect via heartbeat timeout; metadata service elects a new leader (must be in ISR — never an out-of-sync replica, otherwise you'd lose committed writes). |
| Network partition | Minority side rejects writes (no quorum). Majority side keeps serving. **Strong consistency preserved.** |
| Whole AZ down | If replicas spread across 3 AZs with RF=3, majority survives. Latency increases. |

This is **exactly the failure model** from `DesignKeyValueStore.md` Part 2
Phase 5.

---

## Phase 8: Delivery Semantics & Ordering

The trickiest part to talk about clearly in an interview.

### Three delivery guarantees

| Guarantee | What it means | Cost |
| --------- | ------------- | ---- |
| **At-most-once** | Message delivered 0 or 1 times | No retries; lose on any error. |
| **At-least-once** | Delivered 1+ times | Retries on failure; consumer may see duplicates. **The default.** |
| **Exactly-once** | Delivered exactly 1 time | Distributed transactions or idempotency keys end-to-end; complex and slow. |

> **The honest answer for interviews:** *"True exactly-once across the
> network is impossible without idempotent consumers. We provide
> at-least-once + a stable `message_id`; consumers dedupe on processing.
> That's effectively-once with a fraction of the cost."*

Kafka's "exactly-once semantics" works only when both producer and consumer
stay inside the Kafka transactional API — once you write to an external
side-effect (DB, email, payment) it's at-least-once again.

### Ordering

| Scope | Achievable? | How |
| ----- | ----------- | --- |
| **Per-partition** | Yes | Single leader appends in receive order. |
| **Per-key (with `hash(key)` routing)** | Yes | All messages for that key go to one partition. |
| **Global across the queue** | Only by using a single partition (gives up scaling) | Don't do this. |

Ordering inside Kafka == "FIFO inside one partition." Globally ordered
queues exist (SQS FIFO) but cap throughput at ~3000 msg/sec per queue —
because they enforce serial execution.

### Visibility timeout (the SQS pattern)

```
consumer A: receive(q)
   → broker: hide message m for 30s, return (m, receipt_handle)

consumer A processes m...
   case: success → deleteMessage(receipt_handle); m removed from log
   case: crash   → after 30s timeout, m becomes visible again
                   → consumer B can pick it up (at-least-once)
   case: still working → call extendVisibility(receipt_handle, +60s)
```

The receipt handle is a one-shot token (signed) that proves the consumer
holds the lease — same idea as a **fencing token** from DDIA Ch 8.

### Dead-letter queues (DLQ)

Messages that fail processing N times (e.g. corrupted payload, downstream
bug) are moved to a **dead-letter queue** for manual inspection. Without
this, a single poison message can block a partition forever.

---

## Phase 9: Push vs. Pull

A surprisingly common interview follow-up. Both have legitimate uses.

| | Push (broker → consumer) | Pull (consumer → broker) |
| -- | ------------------------ | ------------------------ |
| Used by | RabbitMQ, SNS-to-HTTP, Webhooks | **Kafka, SQS** |
| Slow consumer handling | Broker has to back-pressure (rate-limit, drop, buffer) | Consumer asks for what it can handle — natural pacing |
| Consumer recovery after crash | Broker must reroute / retry | Consumer just resumes from last offset |
| Ordering guarantees | Harder (broker manages many consumers' state) | Easier (offset = consumer's responsibility) |
| Latency | Lower (no polling overhead) | Slightly higher (poll interval); long-poll fixes this |

**Modern systems lean pull-based** for flow-control and recovery
simplicity, with **long-polling** to recover the latency benefit of push.

---

## Phase 10: Reliability & Observability

| Concern | How we handle it |
| ------- | ---------------- |
| **Producer retries** | Idempotent send with `(producer_id, sequence)`. Broker dedupes. |
| **Broker disk full** | Reject writes (return RetryableError). Don't crash. Older segments tier to S3. |
| **Slow consumer** | Pull model + visibility timeout naturally back-pressures. Per-tenant lag metric exposed. |
| **Poison messages** | Auto-DLQ after N failures. |
| **Hot partition** | Re-shard on the fly (split partition) or pin the offending tenant to dedicated brokers. |
| **Self-monitoring** | Lag (committed offset − consumer offset), broker disk utilization, ISR count, replication lag. **Run on a separate small instance** so monitoring doesn't share fate with the queue itself (same lesson as `DesignDistributedMetricsSystemQuick.md`). |

---

## Common Mistakes

| Mistake | Why it's bad | Fix |
| ------- | ------------ | --- |
| Acknowledging the producer before replication | Single replica fsync = data loss on disk failure. | `acks=all`, `min_isr=2`. |
| Putting the dedup cache in process memory | Front-end restart = duplicates flood downstream. | External Redis with TTL. |
| One global Raft group for the whole cluster | Cluster-wide leader is the bottleneck. | Per-partition leader. |
| Assuming exactly-once is free with Kafka | Side-effecting consumers always re-introduce duplicates. | Idempotent consumer + `message_id`. |
| Push to slow consumers without back-pressure | Memory blows up, broker dies. | Pull (or push + bounded broker buffer + drop policy). |
| No DLQ | Poison message blocks an entire partition forever. | DLQ after N failed deliveries. |
| Storing huge payloads inline in the log | Disk fills, replication slow, retention painful. | Off-load to S3 above 1 MB; log carries only URL. |
| Single global ordering at scale | Caps your throughput at one partition's write rate. | Per-key ordering via partition routing. |

---

## Key Concepts for the Interview

| Topic | What to say |
| ----- | ----------- |
| **Append-only log per partition** | "Same primitive as a WAL — sequential writes, immutable once sealed, easy retention via segment deletion." |
| **Per-partition leader (Raft)** | "Leader takes writes, followers fsync, write committed only after `min_isr` ack — at-least-once durability." |
| **Hash(key) partitioning** | "Per-key ordering, scaled out across partitions; the trade-off is hot keys = hot partitions." |
| **Visibility timeout + receipt handle** | "Lease with fencing — consumer crash recovers via re-delivery; that's where at-least-once comes from." |
| **At-least-once + idempotent consumers** | "Effectively-once at a fraction of the cost of true exactly-once." |
| **Pull > push for flow control** | "Consumer paces itself; broker doesn't need a per-consumer queue." |
| **Dead-letter queues** | "One poison message must not block an entire partition." |
| **Tiered storage** | "Hot on broker SSD, cold to S3; broker manifest redirects old reads transparently." |
| **Stateless front-end + tiny consensus metadata service** | "Pattern from every modern distributed system — Spanner, CockroachDB, Kafka. The data plane is partitioned; the control plane is a small Raft group." |

---

## Wrap-Up

| Aspect | Solution | Why |
| ------ | -------- | --- |
| Producer latency | Front-end batching + acks=all on quorum | Sub-10ms p99 with quorum durability. |
| Throughput | Partitioned log; many leaders | Linear scaling with partition count. |
| Durability | RF=3, `acks=all`, `min_isr=2` across AZs | Survives single-AZ failure. |
| Ordering | Per-partition (or per-key via hash routing) | Global ordering doesn't scale. |
| Delivery semantics | At-least-once + idempotent consumer | Effectively-once without distributed transactions. |
| Slow / dead consumers | Pull + visibility timeout + DLQ | Self-recovering, no poison-message wedging. |
| Retention | Local broker disk → S3 tiering | TB-scale retention without TB-scale broker disks. |
| Failure handling | Per-partition Raft groups | Small blast radius; cluster keeps serving during single-replica failures. |
| Self-monitoring | Lag, ISR, disk; on a separate instance | Same fate-sharing rule as observability stacks. |

---

## How this maps to your other design docs

| Concept | Where it shows up |
| ------- | ----------------- |
| Append-only log + sequential writes | `DesignKeyValueStore.md` Part 1 (WAL + segments) |
| Per-partition Raft group | `DesignKeyValueStore.md` Part 2 Phase 2 |
| Hash vs key-based partitioning | `DesignKeyValueStore.md` Part 2 Phase 3, DDIA Ch 6 recap |
| At-least-once + idempotent consumers | `DesignDistributedMetricsSystemQuick.md` reliability section |
| Tiered storage (hot SSD → cold S3) | `DesignDistributedMetricsSystemQuick.md` Phase 5; `DesignLoggingLibrary.md` retention |
| Front-end as stateless edge layer | `DesignSimpleObjectStorage.md` PUT path |
| Dedup cache for retries | `DesignSimpleObjectStorage.md` (object dedup), `DesignQuotaSystem.md` (idempotency keys) |
| Multi-tenant resource allocation (shared vs dedicated) | `DesignQuestionsBank.md` Q5 (in-memory MQ → cloud MQ) |
| Visibility timeout = fencing token | DDIA Ch 8 recap; `DesignKeyValueStore.md` Part 2 Phase 5 |

---

## One-line summary for the interviewer

> *"A distributed message queue is a partitioned append-only log replicated
> via Raft per partition. Producers go through a stateless front-end that
> handles auth, dedup, batching, and routing; the brokers fsync to local
> disk, replicate to a quorum, and only then ack. Consumers pull, with a
> visibility timeout giving us at-least-once delivery; idempotent receivers
> turn that into effectively-once. Old segments tier to S3 for retention,
> a small Raft-replicated metadata service maps queues to partition
> leaders, and dead-letter queues handle poison messages. Everything else
> — push vs. pull, exactly-once, ordering — is a knob on this same core."*

---

## Suggested deep-dive directions an interviewer might pick

1. **Replication & failure recovery** — walk through ISR, leader election, what happens during a partition.
2. **Storage internals** — segment lifecycle, compaction, S3 tiering, manifest catalog.
3. **Delivery semantics** — at-most/at-least/exactly-once, visibility timeout, DLQ.
4. **Multi-tenancy** — fairness, quotas, hot-tenant isolation.
5. **Ordering** — per-partition vs per-key, what if a key gets too hot.
6. **Push vs. pull** — flow control, long-polling, slow consumers.

If you've covered all six end-to-end, you've handled the standard 60-minute interview.
