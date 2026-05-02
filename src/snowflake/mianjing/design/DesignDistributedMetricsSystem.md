# Design a Distributed Metrics & Logging System

> Source: <https://www.youtube.com/watch?v=_KoiMoZZ3C8> (Jordan has no life — Systems Design Interview Question 14, Deep Dive)

Design a system that captures, ships, stores, and queries the telemetry produced by thousands of application servers. Operators use it for debugging incidents ("what happened at 03:14?"), computing SLOs, alerting on anomalies, and joining engineering signals with product analytics.

The main challenge is not "how do I store logs." It is **never blocking the application's hot path** when telemetry I/O is slow, **picking the right storage for each data shape** (logs vs metrics vs traces), and **keeping the cost from going vertical** when one team adds `user_id` as a metric tag and creates a billion time series overnight.

---

## Phase 1: What We Need to Build

### Four Kinds of Data (Pin This First)

| Data shape                  | Example                                  | Volume        | Query pattern                         |
| --------------------------- | ---------------------------------------- | ------------- | ------------------------------------- |
| **Raw logs**                | `log.Error("conn refused")`              | Highest       | `grep` for specific request, mostly write-only. |
| **Structured events**       | `{user_id, route, latency_ms, status}`   | High          | Aggregations and joins.               |
| **Metrics**                 | `http_requests_total{route, status}`     | Medium-low    | Pre-aggregated; `sum(rate(...))` over time windows. |
| **Unstructured blobs**      | Third-party payloads forwarded as-is     | Variable      | Replay / compliance only.             |

The first thing to do in an interview: **pin the data type before defending an architecture.** "I'd use Druid" is wrong for raw 10 KB stack traces; "I'd use Elasticsearch" is wrong for 5-billion-point counters.

### Functional Requirements

- **Ingest** from tens of thousands of producer servers, with bursty traffic.
- **Debug** — find the trace for a specific `request_id` in seconds.
- **Analyze** — aggregate over hours / days for dashboards and SLOs.
- **Enrich** — join telemetry with reference data (e.g. attach `tenant_id` plan tier) before it lands in storage.
- **Alert** in near-real-time on metrics (sub-minute) and on log patterns (minute-ish).

### Non-Functional Requirements

| Requirement              | Target                  | Why?                                                                          |
| ------------------------ | ----------------------- | ----------------------------------------------------------------------------- |
| Caller latency           | `log()` < 1 ms (p99)    | The application must never block on telemetry I/O.                            |
| Loss tolerance           | < 0.1% in normal ops    | Best-effort. We trade strict completeness for the application's stability.    |
| Ingest throughput        | 1M events / sec peak    | One Kafka topic, partitioned across ~hundreds of partitions.                  |
| Query latency (logs)     | < 5s for last 7 days    | Interactive debugging.                                                        |
| Query latency (metrics)  | < 500 ms                | Dashboards refresh every 5–30s.                                               |
| Retention                | 7d hot, 90d warm, years cold | Telemetry value decays fast; old data is mostly compliance.              |

### Capacity Math

| Metric                            | Value          |
| --------------------------------- | -------------- |
| Application servers               | 10,000         |
| Logs per server per second        | 100            |
| Total log QPS                     | 1,000,000      |
| Average log size                  | 1 KB           |
| Daily log volume                  | ~85 TB         |
| Metric series per service         | ~500           |
| Total metric series               | ~5 million     |
| Metric scrape interval            | 10s            |
| Metric points per second          | ~500,000       |

The data is **enormous** (TB/day). Cost is the dominant constraint, not throughput. Every architectural choice in Phase 5 is driven by **cardinality, sampling, and tiered retention**.

---

## Phase 2: Data Model — Different Sinks for Different Shapes

The wrong instinct is "let's put it all in Elasticsearch." The right one: **pick the sink to match the query pattern, even if you end up with three storage systems.**

| Data shape                  | Storage                                          | Why                                                                 | Trade-off                                                          |
| --------------------------- | ------------------------------------------------ | ------------------------------------------------------------------- | ------------------------------------------------------------------ |
| Raw / unstructured logs     | Object storage (S3 / HDFS) + Parquet             | Cheap, infinite retention, batch-friendly, replayable.              | Minute-to-hour query latency; not interactive.                     |
| Searchable structured logs  | Elasticsearch / OpenSearch                       | Inverted index → "find all 500s for `request_id=abc`" in ms.        | Expensive RAM/SSD; hard to retain >30 days at scale.               |
| Metrics (time series)       | Druid / InfluxDB / OpenTSDB / Prometheus         | Columnar + time-partitioned + roll-ups → fast `sum(rate(...))`.     | High-cardinality labels (e.g. `user_id` as a tag) blow up indices. |
| Long-term cold telemetry    | Glacier / archived Parquet on S3                 | Pennies per GB-month; satisfies compliance retention.               | Restore latency; query only via Spark / Athena / Presto.           |
| Dimensions for stream join  | KV store (Cassandra / DynamoDB / Redis)          | Low-latency lookups for `user_id → plan_tier`.                      | Stale snapshots; cache invalidation.                               |

The fan-out from one Kafka topic typically looks like:

```text
Kafka topic ─┬─► Flink ─► Metrics DB        (hot, 7 days)
             ├─► Flink ─► Elasticsearch     (warm, 7–30 days)
             └─► Kafka Connect ─► S3 Parquet (cold, years)
```

---

## Phase 3: How Services Emit Data (SDK API)

The hot-path SDK has one job: **never slow the application down.**

### Logs

```python
log.info("payment captured", request_id=req_id, user_id=u, amount_cents=1099)
```

- Returns immediately — writes to an in-memory ring buffer.
- Drops on overflow rather than blocking. Surfaces a `dropped_total` counter so operators notice.
- A background thread flushes the buffer to a local agent over a Unix domain socket.

### Metrics

```python
http_requests_total = Counter("http_requests_total", labels=["route", "status"])
http_requests_total.labels(route="/checkout", status="200").inc()

request_latency = Histogram("request_latency_ms", labels=["route"], buckets=[10, 50, 100, ...])
request_latency.labels(route="/checkout").observe(42)
```

- Counters and histograms are **commutative** — they aggregate locally. The SDK + agent emit *one record per minute per series*, not one per increment.
- Gauges represent a single value and are sampled at scrape time.

### Sampling

```python
trace.set_sampler(probability=0.01)            # head-based: keep 1% always
trace.set_sampler(tail=KeepIfErrorOrSlow())    # tail-based: keep all errors + p99
```

Sampling is a **decision the SDK / agent makes**, but tail-based sampling pushes the decision to the collector — see Phase 5.

### Local Agent

Every host runs a daemon (Fluent Bit, Vector, OpenTelemetry Collector, statsd):

- Receives over UDS / loopback from all SDKs on the host.
- **Pre-aggregates metrics** before sending. Turns 1M counter increments into 1 record/min.
- **Buffers to local disk** when Kafka is slow — survives application crashes.
- **Compresses + batches** before shipping (gzip / zstd).

The SDK never talks to Kafka directly. The agent owns durability.

---

## Phase 4: How It Works

### Architecture (Five Layers)

| Layer                     | Role                                                                          |
| ------------------------- | ----------------------------------------------------------------------------- |
| **SDK**                   | In-process. `log()`, `counter()`, `histogram()`. Never blocks.                |
| **Local Agent**           | Per-host daemon. Aggregates, batches, compresses, ships, disk-buffers.        |
| **Message Broker (Kafka)**| System of record. Durable, ordered (per partition), replayable.               |
| **Stream Processors (Flink)** | Enrich, pre-aggregate, route, schema-validate.                            |
| **Specialized Sinks**     | Elasticsearch (search), Druid (metrics), S3 Parquet (cold).                   |

```text
App SDK ──► Agent ──► Kafka ──► Flink ──┬─► Druid (metrics)
                                        ├─► Elasticsearch (logs)
                                        └─► S3 Parquet (cold)
```

The broker layer is what makes the rest of this design possible. **Every other component can fail and recover without losing data**, because the broker holds the buffer.

### The Emit Flow

1. App calls `log.info(...)` → returns in microseconds.
2. SDK writes the record into an in-process ring buffer.
3. Background thread flushes the buffer to the local agent over UDS.
4. Agent appends to a local on-disk WAL (so an agent restart doesn't lose data).
5. Agent batches + compresses + sends to Kafka with `acks=all, min.insync.replicas=2`.
6. Kafka replicates and acknowledges. Agent advances its WAL offset.

Steps 1–3 happen on every log call. Steps 4–6 happen in the background. The application never waits.

### The Enrichment Flow (Flink)

```text
Kafka topic   →   Flink job   →   Kafka topic (enriched)   →   downstream sinks
                     │
                     ├─ Broadcast state: dimension table loaded from Cassandra
                     │  (e.g. user_id → plan_tier, region)
                     │
                     ├─ Window: 1-minute tumbling
                     │
                     └─ Output: enriched event with `plan_tier`, `region` attached
```

Why enrich at the stream level instead of "join at query time"?

- Sinks like Druid and Elasticsearch don't do good joins.
- Enriching once (at stream time) is much cheaper than enriching N times (at every query).
- The enriched record is **self-describing** — analysts don't need to know about the dimension table.

> **Note:** Enrichment data changes over time. A user's plan today is not their plan a year ago. Either snapshot the dimension into the event at write time (immutable but storage-heavy) or accept that historical queries reflect today's dimensions. **Name this choice in an interview.**

### Lambda vs. Kappa

| Architecture | Approach | Pro | Con |
| ------------ | -------- | --- | --- |
| **Lambda**   | Two pipelines: streaming (fast/approximate) + batch (slow/authoritative). Serving layer merges. | Each pipeline uses the best tool. | Two codebases, two sets of bugs. |
| **Kappa**    | One streaming pipeline. Reprocess by replaying Kafka from an earlier offset. | One codebase. | Requires broker retention + horizontally scalable processor. |

Modern designs lean **Kappa** because Kafka retention is cheap and Flink scales for replay. Lambda still appears when batch jobs need engines (Spark with custom UDFs) the streamer can't replicate.

---

## Phase 5: Handling Heavy Traffic

### 1. Pre-Aggregate Metrics at the Edge

A common interview mistake: pushing every counter increment over the network. At 1M increments/sec, that's 1M Kafka messages/sec.

**Solution:** the agent pre-aggregates per minute. 1M increments → 1 record. Counters and histograms are commutative; this is lossless.

Logs and structured events **cannot** be pre-aggregated — each one is unique.

### 2. Cardinality Control on Metrics

Every distinct combination of label values creates a new time series. `http_requests_total{user_id="..."}` is a cardinality bomb — millions of users → millions of series → indexes won't fit in RAM → queries collapse.

**Rules:**

- Restrict tag keys to a **bounded set**: `region`, `status_code`, `service`, `route`. Reject everything else at ingest.
- High-cardinality identifiers (`user_id`, `request_id`) belong in **logs/events**, not metrics.
- Enforce a **per-tenant cardinality budget** at ingest. Surface the rejection count back to the offending team.

> **Watch out:** A team accidentally tagged a metric with `request_id` once and took down a whole Druid cluster. **Schema review for new metric tags is not bureaucracy; it's capacity planning.**

### 3. Sampling on Logs and Traces

| Strategy        | When decided           | Pro                                  | Con                                  |
| --------------- | ---------------------- | ------------------------------------ | ------------------------------------ |
| Head-based      | At the SDK             | Cheap, no buffering needed.          | Blind — drops the trace before knowing if it had an error. |
| Tail-based      | At the collector, after seeing the full trace | Keeps all interesting traces. | Requires buffering full traces for seconds; expensive. |

The right answer is usually a **hybrid**: head-sample at 1% by default, tail-sample to keep 100% of error / slow traces.

### 4. Back-Pressure

When Kafka or downstream sinks slow down, agents must:

- Spool to local disk first.
- Shed **oldest** records before they OOM (newest records are most valuable for incidents).
- Surface a `buffer_high_water_mark` metric — alert before data loss happens, not after.

### 5. Hot / Warm / Cold Tiering

Telemetry value decays fast. A 5-minute-old error trace is gold; a 5-month-old debug log is mostly compliance.

| Tier | Age       | Storage                               | Query interface                       |
| ---- | --------- | ------------------------------------- | ------------------------------------- |
| Hot  | < 7 days  | Elasticsearch indexes, in-memory cache| Kibana, Grafana — interactive.        |
| Warm | 7–90 days | Downsampled metrics, compacted Parquet| Dashboards, scheduled queries.        |
| Cold | 90d+      | Glacier / archived S3                 | Spark / Presto / Athena — incidents only. |

Implement with **rollover indices** in Elasticsearch and **roll-up tables** in the time-series DB (raw 10s → 1m → 5m → 1h as data ages).

### 6. Multi-Tenancy and Isolation

In a shared platform, one bad tenant must not starve everyone else:

- **Per-tenant Kafka quotas** (bytes/sec per producer client ID).
- **Per-tenant indices + retention** in Elasticsearch.
- **Per-tenant cardinality budgets** in the metrics DB.
- **Cost attribution** — emit a `bytes_ingested_per_service` metric for chargeback.

### 7. Sharding & Partitioning

| What                | Partition key            | Why                                                |
| ------------------- | ------------------------ | -------------------------------------------------- |
| Kafka topic         | `hash(service_id) % N`   | Co-locate one service's events; avoid hot-spotting.|
| Elasticsearch index | One index per service per day | Independent retention and query isolation.    |
| Druid segments      | Time + service           | Time-pruning makes range queries fast.             |

### 8. Reliability Guarantees

- **At-least-once is the default.** Consumers must be **idempotent** — key writes by `(host, timestamp, sequence)` so replays don't double-count.
- **Exactly-once** is achievable inside a single Kafka → Flink → Kafka chain via Kafka transactions, but it's rarely worth the cost for telemetry.
- **Producer durability:** `acks=all` + `min.insync.replicas=2`.
- **Ordering** is per-partition. Pick partition keys that match the unit of order you actually need (`host_id` or `trace_id`).

---

## Common Mistakes

- **Synchronous HTTP from `log()`.** Hot path takes 50–500 ms; one slow log call freezes UI / requests. Always async.
- **Unbounded in-memory queue.** Memory leak under any backend stall. Bounded ring buffer + drop policy.
- **Per-line POSTs to backend.** TCP/TLS handshake dominates; backend QPS unmanageable. Batch + compress.
- **Putting `user_id` as a metric tag.** Cardinality bomb — millions of series. High-cardinality IDs belong in logs, not metrics.
- **Putting everything in Elasticsearch.** Works at small scale; bankrupts you at TB/day. Tier raw to S3 Parquet; index only what's searched.
- **Head-based sampling only.** Drops error traces before knowing they were errors. Add tail-based sampling for interesting traces.
- **No back-pressure signal.** SDK silently drops logs forever. Counter + emit drop count.
- **Ship on the app's main thread.** UI jank on mobile; request latency on server. Background thread / sidecar.
- **Forgetting agent disk buffering.** Kafka has a 30s blip → all in-flight logs lost. Local WAL on the agent.
- **No end-to-end probe.** A broken pipeline looks "quiet" from the outside. Inject a synthetic event every second; alert if it doesn't show up in each sink.
- **Monitoring the metrics platform with itself.** Circular dependency — when it breaks, you're blind. Run a separate small instance (or a SaaS) for self-monitoring.

---

## Key Concepts for the Interview

| Topic                        | What to Say                                                                                  |
| ---------------------------- | -------------------------------------------------------------------------------------------- |
| Classify the data first      | Logs, structured events, metrics, blobs each pick different sinks. This drives the architecture. |
| Decouple producer from sink  | Kafka in the middle. Buffer for bursts. Replayable for new consumers.                        |
| Never block the caller       | Bounded ring buffer + drop on overflow. The app's hot path is sacred.                        |
| Pre-aggregate metrics        | Counters and histograms are commutative. 1M increments → 1 record/min in the agent.          |
| Cardinality is the silent killer | Reject high-cardinality tags at ingest. `user_id` belongs in logs, not metrics.            |
| Tail-based sampling          | Keep all traces with errors / high latency; drop the boring ones.                            |
| Hot / Warm / Cold tiers      | Search hot in ES (7d), aggregate warm in Druid (90d), archive cold in S3 Parquet (years).    |
| Lambda vs Kappa              | Modern designs lean Kappa. Kafka retention + Flink replay is enough for most cases.          |
| Idempotent consumers         | At-least-once + idempotent writes = effectively-once for telemetry.                          |
| Self-monitoring              | The pipeline must observe itself, but on a separate instance. No circular dependencies.      |

---

## Wrap-Up

| Aspect                         | Solution                                              | Why?                                                     |
| ------------------------------ | ----------------------------------------------------- | -------------------------------------------------------- |
| Application latency            | Async ring buffer + bounded drop                      | The app's hot path stays under 1 ms.                     |
| Producer / consumer decoupling | Kafka as system of record                             | Buffer bursts; allow replay; fan-out to many sinks.      |
| Metric volume                  | Pre-aggregate at the agent                            | 1M increments → 1 record/min.                            |
| Storage choice                 | Different sinks for logs vs metrics vs cold           | Pick the storage to match the query, not the box count.  |
| Cost containment               | Cardinality budgets + sampling + tiered retention     | Without these, cost goes vertical at 100× scale.         |
| Reliability                    | At-least-once + idempotent consumers                  | Exactly-once is rarely worth the complexity.             |
| Multi-tenancy                  | Per-tenant Kafka quotas + index isolation             | One noisy service doesn't drown out the platform.        |
| Operability                    | E2E synthetic probes + producer-side drop counters    | First signals of pipeline degradation.                   |
| Self-monitoring                | Run on a separate small instance                      | Don't blind the on-call when the pipeline breaks.        |
