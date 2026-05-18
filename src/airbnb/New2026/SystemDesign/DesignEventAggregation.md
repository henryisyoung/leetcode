# Design an Event Aggregation System — 60-min Interview Version

Ingest billions of events per day from clients and services, roll them up into useful counters (per-minute / hourly / daily, per-listing / per-user / per-region), serve them in sub-second dashboards, and handle late-arriving and out-of-order events gracefully.

Concrete Airbnb framing: count **listing impressions, clicks, bookings, search queries** in real time and over historical windows.

**Time budget**

| Minutes | What you do                              |
| ------- | ---------------------------------------- |
| 0–5     | Clarify requirements + capacity math     |
| 5–10    | API the client / dashboard calls         |
| 10–25   | High-level architecture                  |
| 25–45   | Pick 2 deep dives                        |
| 45–55   | Scale + failure handling                 |
| 55–60   | Trade-offs and wrap-up                   |

---

## 0–5 min: Clarify

### Step 1 — Pin the four questions (this is the #1 score-getter)

Before drawing a single box:

1. **What's an event?** Click, impression, server log, business event?
2. **What's the aggregation grain?** Minute / hour / day / arbitrary range?
3. **What dimensions?** Per-listing, per-user, per-region, per-device? (Cardinality!)
4. **Exact or approximate?** Counts (exact) vs unique visitors (HLL-approximate)?

> Say out loud: *"I'll assume exact counts for simple metrics and HLL for distinct-cardinality metrics. We'll size cardinality conservatively because that's what kills time-series stores."*

### Step 2 — Functional requirements

- **Ingest** structured events (typed, schema-versioned).
- **Aggregate** by `(metric, dim_1..dim_k, time_bucket)`.
- **Query**: time-series for dashboards, "top N" for leaderboards.
- **Late events**: events arriving up to ~24 h after their event time must still update the right buckets.
- **Backfill / replay** after a bug.

Out of scope: free-form log search (separate system — see metrics design).

### Step 3 — Non-functional requirements

| Requirement              | Target                              |
| ------------------------ | ----------------------------------- |
| Ingest throughput        | 1 M events/sec peak                 |
| End-to-end freshness     | < 60 s for 1-min bucket             |
| Query latency            | < 500 ms p99 for last 7 days        |
| Loss tolerance           | < 0.1% (replayable from Kafka)      |
| Late-event window        | 24 h                                |
| Retention                | 7d minute · 90d hour · years day    |

### Step 4 — Capacity math

```
1 M events/sec × 500 B avg                ≈ 500 MB/sec ≈ 43 TB/day raw
After pre-agg at 1-min × ~10 K series     ≈ 10 K records/sec (×100 reduction)
After day-rollup: tiny — KBs/day
Cardinality budget per dimension          ≤ 100 K distinct values (hard cap)
Top-N queries over 7 days                 1000s of points per series, sub-ms
```

> **Take-away:** raw ingest is huge but pre-aggregation collapses it 100–1000×. The cost driver is **cardinality**, not throughput. Every architectural choice is a cost knob.

---

## 5–10 min: API

### Producers (ingest)

The application SDK never talks to the aggregation pipeline directly. It calls a typed counter:

```python
metric_listing_impressions.labels(region="us-east", listing_id=L).inc()
```

The SDK (or per-host agent) batches and ships to Kafka.

For "raw events" (every click / impression), use:

```http
POST /v1/events
{
  "metric":  "listing.impression",
  "ts_ms":   1714770000123,
  "user_id": "u_42",       // dimension OR high-card → routed to logs not metrics
  "listing_id": "L_999",
  "region":  "us-east"
}
```

`ts_ms` is the **event time**, not the wall clock. Crucial for late-event handling.

### Consumers (dashboards, alerts)

```http
GET /v1/metrics/query
?metric=listing.impression
&group_by=region
&from=2026-05-04T00:00&to=2026-05-04T23:59
&interval=1h
&filter=region:us-east
→ { "series": [{ "labels": {region:"us-east"}, "points": [[ts, val], ...] }] }
```

```http
GET /v1/metrics/topn?metric=listing.bookings&dimension=listing_id&window=24h&n=10
```

### Schema registry

```http
POST /v1/schemas
{ "metric": "listing.booking", "dimensions": ["region", "listing_id"], "kind": "counter" }
```

A registry of allowed `(metric, dimension)` pairs **rejects unknown high-cardinality dimensions at ingest** — the only way to prevent metric-store OOM.

---

## 10–25 min: High-Level Architecture

### Five layers, draw this

```
App SDK ──► Local Agent ──► Kafka ──► Flink Aggregator ──► Storage Tier
                                          │
                                          ├─► 1-min buckets → Druid / ClickHouse (hot)
                                          ├─► 1-hour rollup → Druid (warm)
                                          ├─► daily rollup  → S3 Parquet (cold)
                                          └─► HLL sketches  → HLL store (Redis / Druid)
                                                       │
                                                       ▼
                                                 Query API ──► Dashboards / Alerts
```

| Layer            | Role                                                              |
| ---------------- | ----------------------------------------------------------------- |
| **SDK**          | Typed counters / histograms in process. Never blocks the app.      |
| **Local agent**  | Batches, compresses, ships to Kafka. WAL on local disk.           |
| **Kafka**        | Durable buffer; replay log; the "system of record" for raw events.|
| **Flink**        | Windowed aggregation by event time; emits per-bucket records.     |
| **Druid / ClickHouse** | Time-series store with fast range scans + group-bys.        |
| **Cold storage** | Old buckets to S3 Parquet for cheap long-term retention.          |
| **Query API**    | Single façade over hot+warm+cold, picks the right tier per query. |

### The Ingest Flow

```
1. App: counter.inc()                       — returns in μs
2. SDK: append to in-process ring buffer
3. Agent: flush every 1 s, batched + gzipped, to local on-disk WAL
4. Agent → Kafka with acks=all
5. Kafka replicates → agent advances WAL offset
6. Flink consumes from Kafka, partitions by metric+key
7. Flink windowed aggregate by event_time, emits to Druid every minute
8. Druid persists → queryable in seconds
```

> **Steps 1–3 happen on every call. 4–7 happen in the background. The application never waits.**

### Why pre-aggregate at the agent?

Sending one Kafka message per `counter.inc()` at 1 M increments/sec = 1 M Kafka msgs/sec. Absurd.

The agent keeps `Map<series, count>` in memory and flushes **one record per series per minute**:

```
1,000,000 increments/sec  →  ~5,000 records/sec  (1000× reduction)
```

This works because counters / histograms are **commutative**: order doesn't matter, only the sum does.

Raw clickstream that needs per-event detail **cannot** be pre-aggregated — every record is unique. Those skip the agent counter and ship as individual records into Kafka.

---

## 25–45 min: Deep Dives (pick 2)

### Deep Dive A: Event Time vs Processing Time + Late Events

#### Why event time matters

A user click happens at `t=10:00:00`. Phone is offline. Event arrives at server at `t=10:30:00`. Which bucket does it belong to?

| Time model      | Bucket assigned | Consequence                                                 |
| --------------- | --------------- | ----------------------------------------------------------- |
| Processing time | 10:30 bucket    | "10:00 bucket" undercounts forever; "10:30" mysteriously high |
| Event time      | 10:00 bucket    | Correct attribution; but bucket must stay open              |

> Always aggregate by **event time**. Processing-time aggregations look right at first and silently produce wrong reports later.

#### Watermarks + allowed lateness

Flink's watermark mechanism is the canonical answer:

```
windowed
  .keyBy(metric, dims, bucket=eventTime/60s)
  .window(TumblingEventTimeWindows.of(Time.minutes(1)))
  .allowedLateness(Time.hours(24))
  .aggregate(SumFunction)
  .sideOutputLateData(lateTag)
```

- **Watermark** = "event time has advanced past T; close windows ≤ T-grace."
- **Allowed lateness** keeps a window open for an extra 24 h to absorb late events. Each late event triggers a **re-emit** with the updated count.
- **Side output** for events arriving past 24 h → routed to a "very late" sink for manual reconciliation.

#### Downstream must handle updates

Druid / ClickHouse must accept overwrites for the same `(metric, dims, bucket)` key. Both do; that's why we pick them.

If you're using a time-series DB that's append-only (e.g. naive InfluxDB writes), late events become permanent under-counts. Mention this distinction.

### Deep Dive B: Cardinality — The Silent Killer

Every distinct combo of dimension values = a new time series.

```
listing.impression{region="us-east", listing_id="L_999"}        ← 1 series
listing.impression{region="us-east", listing_id="L_998"}        ← another series
...10 M listings × 50 regions = 500 M series → Druid melts
```

#### Three guard rails

1. **Bounded dimension keys.** Schema registry rejects unknown `dim` keys at ingest. "I want to add `user_id` as a metric tag" → no, that's a log query, not a metric.
2. **High-cardinality IDs go in logs, not metrics.** Need to find a specific `request_id`? Search Elasticsearch, not Druid.
3. **Per-tenant cardinality budget.** A counter on `(metric, dim_combinations)` per tenant; reject + alert when they blow past their quota.

> Real story to tell: *"A team tagged a metric with `request_id` and brought down our time-series store overnight. Schema review for new tags isn't bureaucracy — it's capacity planning."*

#### What if you actually need millions of buckets?

Switch storage shape: use **wide-column time-series rollups** (one row per `(metric, primary_dim, hour)`, columns are sub-dim aggregates) or **pre-bucketed top-K** with Count-Min-Sketch fallback for the long tail.

### Deep Dive C: Approximate Counting (HLL) for Distinct Cardinalities

Exact `COUNT(DISTINCT user_id)` over 100 M users requires keeping every id. Impossibly expensive at high QPS.

**HyperLogLog (HLL)**: a small sketch (~12 KB) estimates cardinality with ~1% error.

```
For each (metric, dims, bucket):
  agent maintains an HLL sketch in memory
  every minute, flush the *sketch bytes* (not the values) to Kafka
  Druid merges sketches across buckets at query time
```

HLL sketches are **mergeable**: union of two sketches is a single sketch with the same error bound. That's what enables roll-ups across minute → hour → day.

| Use case                          | Exact?       | Storage                        |
| --------------------------------- | ------------ | ------------------------------ |
| Total bookings                    | Exact (sum)  | Druid counter                  |
| Distinct users who saw listing X  | Approximate  | Druid HLL column or Redis HLL  |
| Top-10 most-viewed listings/hour  | Approximate  | Count-Min Sketch + heap, periodic eviction |

Mention all three sketch types by name (HLL, CMS, top-K) even if you only go deep on HLL — shows breadth.

### Deep Dive D: Tiered Storage + Query Routing

| Tier | Tech                  | Granularity            | Retention |
| ---- | --------------------- | ---------------------- | --------- |
| Hot  | Druid                 | 1-min buckets          | 7 days    |
| Warm | Druid (rollup table)  | 1-hour buckets         | 90 days   |
| Cold | S3 Parquet (Hive-partitioned by day) | 1-day buckets | years     |

Rollup jobs run hourly: read last hour of 1-min buckets → emit one 1-hour row → keep both for the 7-day window. After 7 d the minute data is dropped.

#### The Query API picks the tier

```python
def query(metric, from_, to_, interval):
    if (now - to_) <= 7d and interval in {1m, 5m, 15m}:
        return druid.query(hot_table)
    elif (now - to_) <= 90d:
        return druid.query(warm_table)
    else:
        return s3_query(cold_partitions)
```

The dashboard does **not** know about tiers. The Query API is the single façade.

### Deep Dive E: Replay / Backfill

Bug shipped at noon; metrics double-counted for 2 hours. Now what?

Because Kafka holds the raw event log, you can:

1. Stop the affected Flink job.
2. Delete the bad output buckets from Druid (`(metric, dims, bucket)` rows).
3. Rewind Kafka offset to noon.
4. Resume Flink → re-emits clean buckets.

This is why Kafka in the middle of the pipeline is non-negotiable. Without it, "fix the metrics" means "you can't."

> Replayability is the cheapest feature in this architecture. It costs you a few Kafka brokers and saves you every time something goes wrong.

---

## 45–55 min: Scale + Failure Handling

### Sharding

- **Kafka topics** partitioned by `(metric, primary_dim)` — keeps the same series on the same partition, lets Flink aggregate in-place.
- **Flink keyed state** sharded by the same key — natural alignment.
- **Druid** sharded by time + secondary dim.

### Backpressure / loss

| Failure                          | Behavior                                                          |
| -------------------------------- | ----------------------------------------------------------------- |
| Kafka unavailable                | Agent buffers to local WAL; on Kafka recovery, drains             |
| Flink job dies                   | Checkpoints in S3 → restart from last checkpoint; no double-count |
| Druid ingestion lag              | Bucket appears "incomplete" with a freshness annotation; alert    |
| Local agent OOM                  | Drop on bounded ring buffer overflow; emit `events_dropped_total` |
| Schema-violating event           | Routed to a quarantine topic + alert; not silently dropped        |
| Clock skew on producer           | Server stamps `ingest_ts` too; reject events with `event_ts` > now+5min |

### Hot-key skew

One listing's events dominate the partition → Flink subtask becomes a bottleneck. **Salting**: append a small random suffix to the key (`L_999:0..3`), aggregate in parallel, merge in a second stage.

### Self-monitoring

The aggregation system **must not** be its own metric source. Run a separate, minimal Prometheus instance for "is the metrics pipeline healthy?" alerts. Otherwise the metric pipeline going down silences its own alerting.

---

## 55–60 min: Trade-offs / Common Mistakes

| Mistake                                                | Effect                                              | Fix                                                  |
| ------------------------------------------------------ | --------------------------------------------------- | ---------------------------------------------------- |
| Aggregate by processing time                           | Buckets look right at first, drift silently         | Event-time + watermarks + allowed lateness           |
| No schema registry                                     | Anyone adds a tag → unbounded cardinality           | Bounded tag keys; reject unknown at ingest           |
| High-cardinality IDs as metric labels                  | Time-series DB OOMs                                 | Send to logs; metrics is for *dimensions*, not IDs   |
| `COUNT(DISTINCT)` queried directly on raw events       | Impossibly expensive                                | HLL sketches; merge across buckets                   |
| No Kafka — direct SDK → DB                             | Can't replay; can't add new sink without re-ingest  | Kafka in the middle is non-negotiable                |
| One global tier (Druid for everything, forever)        | Storage cost explodes                               | Tiered hot/warm/cold + query router                  |
| Late events permanently undercount                     | Yesterday's report changes if asked twice           | Allowed lateness with re-emit; downstream upsert     |
| Self-alerting on the same stack                        | Pipeline down → no alert it's down                  | Separate minimal monitoring instance + dead-man's-switch |
| Hot-key skew on popular series                         | One Flink subtask is the bottleneck                 | Salt key + two-stage aggregation                     |
| Unbounded ring buffer on agent                         | OOM under burst                                     | Bounded buffer + drop with counter                   |

### Key Concepts for the Interview

| Topic                                | What to say                                                                            |
| ------------------------------------ | -------------------------------------------------------------------------------------- |
| Event time, not processing time      | Watermarks + allowed lateness. Anything else lies.                                     |
| Cardinality is the cost driver       | Bounded dim keys; reject high-card IDs; per-tenant budgets                             |
| Pre-aggregate at the edge            | Counters / histograms are commutative; 1 record per series per minute, not per event   |
| HLL for distinct                     | Sketches are mergeable; "1% error, 12 KB" sells itself                                 |
| Kafka in the middle for replay       | Bug shipped? Rewind, re-emit. Without Kafka, you can't fix metrics.                    |
| Tiered storage + query router        | Hot Druid → warm Druid rollup → cold S3 Parquet. Dashboard never knows.                |
| Hot-key salting                      | When one series dominates, salt the key, merge in stage 2                              |
| Self-monitoring on separate infra    | The metrics pipeline can't be its own alerter                                          |
| Don't aggregate logs in a metric DB  | Logs are searchable text → Elasticsearch. Metrics are numeric → Druid.                 |

### Wrap-Up

| Aspect                          | Solution                                              |
| ------------------------------- | ----------------------------------------------------- |
| 1 M events/sec ingest           | SDK → agent → Kafka with WAL                          |
| Pre-aggregation (×1000)         | Agent keeps counters in memory; flushes 1/min/series  |
| Late events                     | Event-time windows + 24 h allowed lateness            |
| Distinct counts                 | HLL sketches; mergeable across buckets                |
| Tiered storage                  | Druid 7d minute / 90d hour / S3 day forever           |
| Query                           | Single API picks tier; sub-second for last 7 days     |
| Replay after bug                | Kafka retention + Flink offset rewind                 |
| Cardinality                     | Schema registry + per-tenant budgets + reject at ingest |
| Hot-key skew                    | Salt + two-stage Flink aggregation                    |
| Self-monitoring                 | Separate minimal monitoring instance + dead-man's-switch |
