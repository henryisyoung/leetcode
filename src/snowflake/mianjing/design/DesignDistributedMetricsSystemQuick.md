# Design a Distributed Metrics & Logging System — 60-min Interview Version

> Companion to `DesignDistributedMetricsSystem.md`. Same architecture, half the words, paced for a real 60-minute interview.

**Time budget**

| Minutes | What you do                              |
| ------- | ---------------------------------------- |
| 0–5     | Clarify requirements + capacity math     |
| 5–10    | API the application calls                |
| 10–25   | High-level architecture                  |
| 25–45   | Pick 2 deep dives (interviewer chooses)  |
| 45–55   | Scale + failure handling                 |
| 55–60   | Trade-offs and wrap-up                   |

---

## 0–5 min: Clarify

### Step 1 — Pin the data shape (this is the #1 score-getter)

The interviewer says "design a metrics system." Before drawing a single box, classify what's actually flowing:

| Data shape          | Example                                  | Storage that fits      |
| ------------------- | ---------------------------------------- | ---------------------- |
| Raw logs            | `log.Error("conn refused")`              | S3 / Elasticsearch     |
| Structured events   | `{user_id, route, latency_ms, status}`   | Elasticsearch / S3     |
| Metrics (counters)  | `http_requests_total{route, status}`     | Druid / Prometheus     |
| Traces              | `trace_id`, spans                        | Jaeger / Tempo         |

> **Say out loud:** *"Different shapes need different sinks. I'd never put 5 B-point counters in Elasticsearch, and I'd never put 10 KB stack traces in Druid."*

### Step 2 — Functional requirements

- **Ingest** ~1 M events/sec from ~10 K servers, bursty.
- **Debug** — find a specific `request_id` in seconds.
- **Aggregate** — dashboards, SLOs over hours/days.
- **Alert** in near-real-time on metrics and on log patterns.

### Step 3 — Non-functional requirements

| Requirement              | Target                       |
| ------------------------ | ---------------------------- |
| `log()` latency          | < 1 ms (p99) — never blocks app |
| Loss tolerance           | < 0.1% in normal ops         |
| Log query (last 7 days)  | < 5 s                        |
| Metric query             | < 500 ms                     |
| Retention                | 7d hot · 90d warm · years cold |

### Step 4 — Capacity math (one block, then move on)

```
10 K servers × 100 logs/sec       ≈ 1 M log QPS
1 M × 1 KB                        ≈ 1 GB/sec ≈ 85 TB/day
5 M metric series × 1 point / 10s ≈ 500 K metric points/sec
```

> **Take-away:** the system is dominated by **cost**, not throughput. Every later decision (sampling, tiering, cardinality budgets) is a cost knob.

---

## 5–10 min: API the Application Calls

The hot-path SDK has one job: **never slow the application down.**

### Logs (free-form)

```python
log.info("payment captured", request_id=req_id, user_id=u, amount_cents=1099)
```

- Returns immediately — writes to a bounded in-process ring buffer.
- **Drops on overflow** rather than blocking. Surfaces a `dropped_total` counter so operators notice.

### Metrics (typed)

```python
http_requests_total = Counter("http_requests_total", labels=["route", "status"])
http_requests_total.labels(route="/checkout", status="200").inc()
```

- Counters / histograms are **commutative** → the agent aggregates locally and emits **one record/min/series**, not one per increment.
- Gauges are sampled at scrape time.

### Two rules to state clearly

1. **App never talks to Kafka.** It talks to a per-host agent over a Unix domain socket. The agent owns the network.
2. **Bounded buffer + drop on overflow.** Any unbounded queue is a memory leak waiting to happen.

---

## 10–25 min: High-Level Architecture

### Five layers, draw this diagram

```
App SDK ──► Local Agent ──► Kafka ──► Flink ──┬─► Druid          (metrics)
                                              ├─► Elasticsearch  (logs)
                                              └─► S3 Parquet     (cold archive)
```

| Layer            | Role                                                                  |
| ---------------- | --------------------------------------------------------------------- |
| **SDK**          | In-process. `log()`, `counter()`. Never blocks.                       |
| **Local Agent**  | Per-host daemon. Aggregates metrics, batches, compresses, disk-buffers, ships. |
| **Kafka**        | Durable buffer + replay log. The "system of record."                  |
| **Flink**        | Enrich, validate, route, pre-aggregate. Stateful stream processor.    |
| **Sinks**        | Druid (metrics), Elasticsearch (logs), S3 Parquet (cold).             |

### The Emit Flow (walk through this)

1. App calls `log.info(...)` → returns in microseconds.
2. SDK appends to in-process ring buffer.
3. Background thread flushes to local agent over UDS.
4. Agent writes to local on-disk WAL (so an agent restart loses nothing).
5. Agent batches, compresses, ships to Kafka with `acks=all`.
6. Kafka replicates → agent advances WAL offset.

> **Steps 1–3 happen on every call. 4–6 happen in the background. The application never waits.**

### Why Kafka in the middle?

- **Decouples producers from consumers.** Sinks can be slow / down without back-pressuring the app.
- **Replayable.** Add a new sink later → it reads from the beginning. Reprocess after a bug → rewind the offset.
- **One write, many readers.** The same topic feeds Druid, ES, and S3 with no extra write amplification at the edge.

---

## 25–45 min: Deep Dives (pick 2, interviewer's choice)

### Deep Dive A: Pre-aggregation + Cardinality (the metrics half)

#### Pre-aggregate at the agent

Sending one Kafka message per `counter.inc()` at 1 M increments/sec = 1 M Kafka msgs/sec. That's absurd.

**Solution:** the agent keeps `Map<series, count>` in memory and flushes one record per series per minute.

```
1,000,000 increments/sec  →  ~5,000 records/sec  (1000× reduction)
```

This works because counters and histograms are **commutative**: order doesn't matter, only the sum does.

Logs and structured events **cannot** be pre-aggregated — every record is unique.

#### Cardinality is the silent killer

Every distinct combo of label values = a new time series.

```
http_requests_total{route, status, user_id="alice"}    ← 1 series
http_requests_total{route, status, user_id="bob"}      ← another series
... 10 M users → 10 M series → Druid OOMs
```

Three guard rails:

1. **Bounded tag keys.** Only allow `region`, `status_code`, `service`, `route`. Reject other tag keys at ingest.
2. **High-cardinality IDs go in logs, not metrics.** Need to find a slow `request_id`? Search ES, don't add a metric tag.
3. **Per-tenant cardinality budget.** Reject + alert when one team blows past their quota.

> **Real story to tell:** *"A team once tagged a metric with `request_id` and brought down a Druid cluster overnight. Schema review for new metric tags isn't bureaucracy — it's capacity planning."*

### Deep Dive B: Enrichment + Stream Processing

#### What enrichment is

Raw event from Kafka:

```json
{ "tenant_id": "t_42", "ts": 1714770000, "metric": "api.latency_ms", "value": 137 }
```

Flink joins it with a dimension table (broadcast from Cassandra into every Flink subtask):

```json
{ "tenant_id": "t_42", "ts": ..., "metric": ..., "value": 137,
  "plan_tier": "enterprise", "region": "us-east", "sla_class": "gold" }
```

#### Why enrich in the stream, not at query time?

- **Sinks like Druid and ES can't join.** They're single-fact-table indexes.
- **Enrich once, query N times** vs. enrich per query. Massive cost amplification at query time.
- **Self-describing records.** Analysts don't need to know about a dimension table that may have changed schema.

#### The trade-off you must name

Dimension data changes over time. A user's plan today is not their plan a year ago.

| Choice          | Pro                                  | Con                                      |
| --------------- | ------------------------------------ | ---------------------------------------- |
| Snapshot at write time | Historically accurate          | Storage-heavy (extra columns per event)  |
| Late binding (lookup at query time) | Cheap storage     | Historical queries reflect *today's* dimensions |

> Most metrics systems pick snapshot-at-write because telemetry is append-only and storage is cheap.

### Deep Dive C: Sampling (if asked about traces)

| Strategy        | Decided where         | Pro                       | Con                              |
| --------------- | --------------------- | ------------------------- | -------------------------------- |
| **Head-based**  | At the SDK            | Cheap, no buffering       | Drops the trace before knowing if it had an error |
| **Tail-based**  | At the collector after seeing the full trace | Keeps all interesting traces | Buffers full traces for seconds — expensive |

> The right answer is **hybrid**: head-sample 1% by default, tail-sample to keep 100% of error / slow traces.

---

## 45–55 min: Scale & Failure

### Back-pressure

When Kafka or a sink slows down:

- Agent **spools to local disk** first (the WAL).
- When disk fills, **shed oldest** records (newest = most valuable for incidents).
- Emit `buffer_high_water_mark` so on-call gets paged **before** data loss starts.

### Hot / Warm / Cold tiering

| Tier | Age       | Storage                              | Query interface              |
| ---- | --------- | ------------------------------------ | ---------------------------- |
| Hot  | < 7 days  | Elasticsearch / Druid hot indexes    | Kibana, Grafana — interactive |
| Warm | 7–90 days | Downsampled metrics, compacted Parquet | Dashboards, scheduled jobs |
| Cold | 90 d+     | S3 Glacier                           | Spark / Athena — incidents only |

> Implementation: **rollover indices** in ES, **roll-up tables** in Druid (raw 10s → 1m → 5m → 1h as data ages).

### Reliability

- **At-least-once is the default.** Make consumers **idempotent** — key writes by `(host, ts, sequence)` so replays don't double-count.
- **Exactly-once** is achievable inside a Kafka → Flink → Kafka chain (transactions), but rarely worth the cost for telemetry.
- **Producer durability:** `acks=all` + `min.insync.replicas=2`.

### Sharding

| Component         | Partition key            | Why                                          |
| ----------------- | ------------------------ | -------------------------------------------- |
| Kafka topic       | `hash(service_id) % N`   | Co-locate one service's events; avoid hot-spotting |
| ES index          | One index per service per day | Independent retention + query isolation |
| Druid segments    | Time + service           | Time-pruning makes range queries fast        |

---

## 55–60 min: Trade-offs & Wrap-up

### Top 5 mistakes to NOT make in your design

1. **Synchronous HTTP from `log()`.** One slow log freezes the request. Always async + bounded buffer.
2. **`user_id` as a metric tag.** Cardinality bomb. High-cardinality IDs belong in logs.
3. **Everything in Elasticsearch.** Works at small scale; bankrupts you at TB/day. Tier cold to S3 Parquet.
4. **No back-pressure signal.** SDK drops silently → you find out from a customer. Always emit a drop counter.
5. **Monitoring the pipeline with itself.** Circular dependency — when it breaks, you're blind. Run a small separate instance for self-monitoring.

### Trade-offs to name out loud (interviewer hooks)

| Trade-off                        | What you'd say                                                                 |
| -------------------------------- | ------------------------------------------------------------------------------ |
| **Lambda vs Kappa**              | "Modern systems lean Kappa — Kafka retention + Flink replay covers reprocessing without a second pipeline." |
| **At-least-once vs exactly-once** | "At-least-once + idempotent writes = effectively-once for telemetry, with a fraction of the operational cost." |
| **Snapshot vs late-bind enrichment** | "Snapshot at write time for historical accuracy; pay the storage cost." |
| **Head vs tail sampling**        | "Hybrid: head-sample 1% default, tail-sample 100% of errors and slow traces." |

### One-line summary

> *"Decouple the app from sinks with Kafka. Pre-aggregate metrics at the edge to cut volume by 1000×. Enrich once in Flink, then fan out to specialized sinks per query pattern. Cost is the dominant constraint — control it with cardinality budgets, sampling, and tiered retention."*

---

## Cheat-sheet: what scores vs. what loses points

| Scores points                                                  | Loses points                                          |
| -------------------------------------------------------------- | ----------------------------------------------------- |
| Pinning the data shape before picking storage                  | "I'd put it all in Elasticsearch"                     |
| Capacity math (1 M QPS, 85 TB/day) in the first 5 min          | Hand-waving "it's a lot of data"                      |
| Naming **back-pressure** explicitly                            | Assuming Kafka never slows down                       |
| Calling out **cardinality** as the hidden cost driver          | Letting `user_id` end up as a metric tag              |
| **Hot/warm/cold tiering** as cost story                        | Same retention everywhere                             |
| Naming the **enrichment trade-off** (snapshot vs late-bind)    | "We just join at query time"                          |
| Idempotent consumers + at-least-once                           | "I'll do exactly-once via 2PC"                        |
| Self-monitoring on a **separate** instance                     | "We monitor the metrics platform with itself"         |
