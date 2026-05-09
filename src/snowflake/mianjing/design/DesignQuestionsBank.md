# Snowflake System Design — Question Bank (from 面经)

> Cleaned-up, deduped extracts from 1point3acres mianjing posts. Each entry
> keeps only the actionable design statement, and adds a short "what they're
> testing" + "where to look" note so the bank is usable for prep.

---

## Event Subscription System

### Q1. Fire-and-forget event delivery + webhook subscriptions

**Statement.**
Some clients send events in fire-and-forget manner at **1 M events/sec**. Other
clients can register **subscription rules** for events they're interested in,
register a **webhook**, and get notified.

**What they're testing.**
- High-throughput ingest with no client-side back-pressure.
- Rule matching at scale (filtering 1 M ev/s through N rules → matches).
- Reliable webhook delivery with retries / DLQ.
- Multi-tenant isolation: one slow webhook can't block others.

**Key components to mention.**
- Ingest API → Kafka (durable buffer, decouple producers from consumers).
- Rule index (per tenant: tag-based / predicate tree / Bloom-prefilter).
- Matcher = stream processor (Flink/Spark Streaming) — for each event, find
  matching subscription set, fan out one message per subscriber to a
  per-subscriber delivery queue.
- Webhook delivery service: pulls from per-subscriber queue, POSTs with
  exponential backoff + jitter, idempotency-key, DLQ after N retries.
- Per-subscriber concurrency cap so one slow endpoint can't starve others
  ("isolation per webhook" is the key phrase).
- At-least-once delivery + idempotency contract on the receiver side.

**Cousin design** — this is "AWS EventBridge / SNS-to-HTTPS / Webhook Relay."

---

## Monitoring and Alerting

### Q2. Cloud monitoring service with AI-driven automated remediation

**Statement.**
Design a service that monitors users' applications running on the cloud:
collect telemetry, call an AI/ML monitoring service to evaluate it, and
**take actions based on the result** (e.g. shutdown, network isolation when
anomalies are detected).

**What they're testing.**
- Three-stage pipeline: collect → score → act.
- Action layer is the differentiator vs. plain monitoring — needs an
  audit log, dry-run mode, blast-radius limits, human-in-the-loop for
  destructive actions.
- How AI inference fits in (batch-scored vs. streaming).

**Key components.**
- Telemetry collection: per-host agent → Kafka (same shape as
  `DesignDistributedMetricsSystem`).
- Feature pipeline: Flink computes windowed features → pushes batches to a
  model-serving endpoint (gRPC) for anomaly score.
- Decision engine: rule + score → action. Must support "policy modes":
  observe-only, alert-only, auto-remediate.
- Action executor: idempotent operations against the user's cloud
  (shutdown VM, isolate VPC, scale group) with **per-tenant action quotas**
  and a kill-switch.
- Audit log of every action with reason + score + operator override.
- Self-protection: never auto-remediate during a known platform incident
  (correlate against the platform's own status feed).

**See also.** `DesignDistributedMetricsSystem.md` (collect/store/alert),
`DesignDistributedMetricsSystemQuick.md` Deep Dive D (Alerting) for the
"what to do once you've decided to fire" half.

---

### Q3. Sensor data ingestion & analysis (IoT temperature)

**Statement.**
Many sensors in many locations collect **temperature** data. Design a system
that **collects and analyzes** this data.

**What they're testing.**
- IoT-shape ingest: huge fan-in, small messages, intermittent connectivity.
- Time-series storage choices.
- Aggregation patterns (per-location averages, anomaly detection).

**Key components.**
- Edge gateway / MQTT broker accepts sensor pushes (or polls them via
  CoAP). Sensors batch + compress before sending — bandwidth-constrained.
- Ingest service authenticates per-device certs, validates schema,
  publishes to Kafka.
- Stream processor (Flink) computes per-location rolling averages,
  detects anomalies (> 3σ, threshold breaches).
- Storage:
  - **Hot:** time-series DB (InfluxDB, TimescaleDB, Druid) for last N days.
  - **Warm/Cold:** Parquet on S3, partitioned by `date / region / sensor_id`.
- Query API supports `latest`, `range`, `aggregate(by=region, fn=avg)`.
- Down-sampling: raw 1 s → 1 min → 1 h → 1 day as data ages.
- Failure handling for offline sensors: device-side ring buffer, replays on
  reconnect with original timestamps.

**Hot questions an interviewer asks.**
- Cardinality: how many sensors? Per-sensor series cardinality?
- Backfill: a sensor reconnects after 3 days offline — how does the
  pipeline handle out-of-order data?
- Edge compute: should anomaly detection happen on the device, the
  gateway, or the central system? (Trade-off: latency vs. visibility.)

---

### Q4. Logging mechanism for a multi-tenant cloud service

**Statement.**
Develop a logging mechanism for a cloud service:
- Multiple customers; each customer has a list of users.
- Track **user activities** (e.g. `SELECT on table`, `INSERT on table`).
- Capture **success/failure status** of each operation.
- Capture **performance numbers** (latency, request count).
- Track **which tables** are accessed and **which user** ran the query.

**What they're testing.**
- Schema design for structured audit logs (vs. free-form text).
- Multi-tenant isolation in the log storage and query path.
- Audit retention requirements (often 1+ year for compliance).
- Difference between **operational logs** (debugging) and **audit logs**
  (compliance) — they have different durability/retention needs.

**Key components.**
- Per-tenant structured event:
  ```json
  {
    "tenant_id": "...", "user_id": "...", "ts": ...,
    "op": "SELECT", "tables": ["sales.orders"],
    "status": "success", "latency_ms": 73, "request_id": "..."
  }
  ```
- Synchronous write to durable WAL **before** ack on audit-critical events;
  async batched ship for performance counters.
- Two storage tiers:
  - **Search** (Elasticsearch / OpenSearch) for "what did user X do today?"
  - **Analytical** (Parquet on S3, queried by Snowflake/Athena) for trend
    queries and compliance exports.
- Strict per-tenant access control at the query layer (mandatory
  `tenant_id` filter injection).
- Append-only — no UPDATE/DELETE on audit tables; "delete" is a tombstone
  event for compliance.

**See also.** `DesignLoggingLibrary.md` (the SDK side) and
`DesignDistributedMetricsSystemQuick.md` Deep Dive E (the query side).

---

### Q5. In-memory MQ → cloud-managed MQ service

**Statement.**
Design a message-queue service in two passes:
1. **In-memory MQ:** what's the class structure and API?
2. **Cloud MQ-as-a-service:** how does it work? How do you allocate storage
   and resources across many tenants creating their own queues?

**What they're testing.**
- API design: clear primitives (`createQueue`, `send`, `receive`, `ack`,
  `delete`).
- Visibility timeout, dead-letter queues, FIFO vs. standard.
- Multi-tenancy: dedicated vs. shared resources (cost vs. isolation).
- Storage tiering: in-memory hot + on-disk durable.

**Key API.**
```
createQueue(name, opts) -> queue_id
send(queue_id, message)
receive(queue_id, max_msgs, visibility_timeout) -> [Message]
delete(queue_id, receipt_handle)        // ack
deleteQueue(queue_id)
```

**Key design decisions to walk through.**
- **Storage layout:** WAL per queue (sequential writes), in-memory index
  of unacked messages, periodic checkpoint. Same architecture as
  `DesignKeyValueStore.md` Part 1.
- **Visibility timeout:** when a consumer receives, the message is hidden
  for `vt` seconds; if not acked in time, it becomes visible again
  (at-least-once).
- **Multi-tenant resource allocation:** three modes the interviewer wants
  you to compare:
  - **Shared pool** — cheap, but noisy-neighbor risk; needs per-tenant
    rate limits + quota.
  - **Dedicated nodes** — premium tier; predictable latency.
  - **Hybrid** — shared by default, dedicated brokers for paying tiers.
- **Sharding:** hash by `(tenant_id, queue_name)` across a fleet of
  brokers; metadata service maps queue → broker.
- **Replication:** 3-way for durability; Raft per shard.
- **Compare to known systems:** SQS (managed, at-least-once, no ordering),
  Kafka (durable log, partition order), RabbitMQ (push delivery, complex
  routing).

---

### Q6. Snowflake server-rental allocator (instance-type matching)

**Statement.**
Snowflake has many rentable servers; each server has `node_id`, `status`,
and a list of compatible **instance types**. Instance types differ from EC2:
there are ~100 types (`s1, s2, s3, m1, m2, m3, L1, L2, L3, XL1, ...`), and
each physical server can act as some subset of them.

Example:
- Server A can be `{m1, m2, L1, L2}`.
- Server B can be `{s1, m1, L1}`.
- Request "two `(m1, L1)` servers" → return A and B.
- Request "two `(m1, m2)` servers" → cannot satisfy with A, B (B can't be `m2`).

API:
```
getServer(int reqNumOfServer, String[] instance_types)
returnServer(int[] serverNodeIds)
```

The hard part: **at 100 K servers, how do you respond fast?**

**What they're testing.**
- Set-cover / subset-matching at scale — this is more algorithm than
  system design.
- Indexing strategy for a multi-attribute matching problem.
- Concurrency: many concurrent `getServer` calls reserving the same pool.

**Indexing approaches to compare.**

| Approach | Lookup | Pros | Cons |
| -------- | ------ | ---- | ---- |
| **Per-type bitmap** — for each instance type, a bitmap of `server_id`s that support it. | AND the bitmaps for the requested types → set of candidate servers. | O(N/64) per AND; trivially parallelizable; fits in RAM (100 types × 100 K bits = 1.25 MB). | Bitmap maintenance on rent/return. |
| **Inverted index** `instance_type → Set<server_id>` | Intersect sets for requested types. | Same idea, different impl. | Set intersection cost. |
| **Server profile bitmask** — represent each server's capability as a 100-bit mask; request also a 100-bit mask. | `server.mask & req.mask == req.mask` → match. | One CPU instruction per check; trivially shardable. | Linear scan of 100 K masks per request — but that's ~100 µs in cache. |

**For 100 K servers, the bitmask scan is the simplest correct answer:**
- 100 K × 16 B = 1.6 MB resident set.
- A single AVX2 scan of that fits in microseconds.
- Reserve via CAS on a per-server status field; on contention, retry with
  the next match.

**Multi-server requests.** When the request asks for N servers each with a
different type set, it's **bipartite matching** (servers ↔ requested slots).
For small N, just greedy-with-backtracking on the candidate set is fine.
For large N, model as a flow problem.

**Concurrency.**
- Optimistic CAS on `server.status` (`AVAILABLE → RESERVED`).
- Reservation has a TTL — if the client doesn't confirm, it auto-releases.
- Avoid pessimistic locks on the whole pool; partition by type-prefix to
  reduce contention.

**Distributed scale-up.**
- Shard the index by something stable (e.g. rack / region).
- A coordinator queries each shard for candidates, then commits the
  reservation via 2PC or via a consensus-replicated allocation log.

---

## News Feed

### Q7. Social feed (朋友圈 / timeline)

**Statement.**
Design a social feed (Moments / friends timeline) — **API to DB**.

**What they're testing.**
- The classic **fan-out-on-write vs. fan-out-on-read** trade-off.
- Hot users (celebrities) breaking naive fan-out.
- Caching strategy at multiple layers.

**Key components.**

```
Client ──► Post API ──► Post DB (durable write)
                          │
                          ▼
                   Fan-out worker ──► per-user feed cache
                                       (e.g. Redis sorted set,
                                        capped at last 1 K posts)

Client ──► Feed API ──► Feed cache (hot) ─miss─► DB merge of
                                                  followee posts
```

- **Post API.** Authenticates, validates, persists to a strongly-consistent
  store (Postgres / Spanner). Returns immediately.
- **Fan-out worker.** For normal users (followers < ~10 K), pushes the new
  post into each follower's feed cache (Redis sorted set keyed by user_id,
  scored by ts, capped at 1 K entries).
- **For "celebrity" users (followers > 10 K),** skip fan-out. Instead, the
  feed API merges their recent posts into each viewer's feed at read time.
  This is the **hybrid push/pull** strategy.
- **Read path.** Feed API:
  1. Read viewer's feed cache (sorted set top-N).
  2. Merge in recent posts from the celebrities they follow.
  3. Hydrate post bodies from the post cache (look-aside).
  4. Apply ranking / dedup / privacy filters.
  5. Return paginated result with cursor.

**Storage choices.**
- **Posts table** — sharded by `post_id` (or `user_id` + `ts`), durable
  primary store.
- **Following / followers** — graph table; for celebrity detection,
  maintain a `follower_count` column and tier accordingly.
- **Feed cache** — Redis sorted set per user; capped; rebuilt from DB on
  miss.

**Hard questions.**
- Backfill on follow: when user A follows user B, do we backfill B's posts
  into A's feed? (Usually no — feed is forward-looking; A sees B's *new*
  posts.)
- Unfollow: leave stale posts in cache and dedup at read, or evict?
- Privacy: posts may have visibility = `friends-only`, `public`, etc. Apply
  at read time, never at fan-out time.
- Ranking: chronological vs. ML-ranked. Ranking adds a re-score layer
  between the merge and the response.

**Cousin systems.** Twitter timeline (the canonical paper), Instagram feed,
WeChat Moments.

---

## Cross-cutting cheat-sheet

These patterns repeat across nearly every question above:

| Pattern | Where it shows up |
| ------- | ----------------- |
| **Per-host agent + Kafka** in front of any high-volume ingest | Q1, Q2, Q3, Q4 |
| **Tiered storage** (hot search index + cold columnar) | Q3, Q4 |
| **At-least-once + idempotent receivers** | Q1, Q5 |
| **Per-tenant quotas + isolation** | Q1, Q2, Q4, Q5 |
| **Hybrid push/pull** (write-side fan-out for normal, read-side merge for hot) | Q7 |
| **Bitmap / inverted index** for fast multi-attribute matching | Q1 (rule matching), Q6 (server matching) |
| **Audit log of side-effecting actions** with dry-run mode | Q2, Q5 |

When in doubt, anchor on:
1. **Data shape** — events / metrics / logs / messages all need different sinks.
2. **Hot path latency** — never block the caller; use async + bounded buffer.
3. **Multi-tenancy** — quotas, isolation, mandatory tenant filter at query time.
4. **Failure model** — at-least-once is the default; make consumers idempotent.
