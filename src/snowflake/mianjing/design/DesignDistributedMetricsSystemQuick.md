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
App SDK ──► Local Agent ──► Kafka ──► Flink ──┬─► Druid          (metrics) ──┐
                                              ├─► Elasticsearch  (logs)      ├─► Alert Manager ──► PagerDuty / Slack
                                              └─► S3 Parquet     (cold)      │
                                              └─► Alert Job (streaming) ─────┘
```

| Layer            | Role                                                                  |
| ---------------- | --------------------------------------------------------------------- |
| **SDK**          | In-process. `log()`, `counter()`. Never blocks.                       |
| **Local Agent**  | Per-host daemon. Aggregates metrics, batches, compresses, disk-buffers, ships. |
| **Kafka**        | Durable buffer + replay log. The "system of record."                  |
| **Flink**        | Enrich, validate, route, pre-aggregate, **stream-evaluate alert rules**. |
| **Sinks**        | Druid (metrics), Elasticsearch (logs), S3 Parquet (cold).             |
| **Alert Manager** | Dedupe, group, silence, route fire/resolve events to PagerDuty/Slack. |

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

### Deep Dive D: Alerting

Alerting is the **closing-the-loop** part of the system: the whole pipeline only matters if humans get paged when something is broken. Two distinct alert paths because metrics and logs have different shapes.

#### Architecture

```
                    ┌─ Alert Rule Store (DB) ─┐
                    │  rule = {expr, for,     │
                    │          labels, route} │
                    └────────────┬────────────┘
                                 │ rules loaded by
                                 ▼
       Kafka (events) ──► Flink Alert Job ──┐
                                             │
       Druid (metrics) ─► Rule Evaluator   ──┤   triggered alerts
                          (cron, 30 s tick)  │   (firing / resolved)
                                             ▼
                                    ┌────────────────────┐
                                    │ Alert Manager      │
                                    │  - dedupe          │
                                    │  - group           │
                                    │  - silence         │
                                    │  - route           │
                                    └─────┬──────────────┘
                                          │
                          ┌───────────────┼───────────────┐
                          ▼               ▼               ▼
                       PagerDuty        Slack         Email/Webhook
```

#### Two alert paths

| Path | Source | Latency | Example |
| ---- | ------ | ------- | ------- |
| **Metric alerts** (threshold-based) | Druid / Prometheus query, evaluated periodically | 30–60 s end-to-end | "p99 latency > 500 ms for 5 min" |
| **Log/event alerts** (pattern-based) | Flink streaming job over Kafka, evaluated continuously | 1–5 s end-to-end | "100+ `OutOfMemory` errors from one service in 1 min" |

#### Path 1 — Metric alerts (the periodic evaluator)

```
1. Operator defines a rule:
     expr  = "rate(http_requests_total{status=~'5..'}[5m]) > 10"
     for   = 2m              ← must be true for 2 min before firing
     labels = {severity: page, team: payments}

2. Alert Evaluator (a stateless service) runs every 30 s:
     for each rule:
        result = query(druid, expr, time=now)
        if result is true and pending_since is None:
           pending_since = now
        elif result is true and now - pending_since >= for:
           fire(rule)
        elif result is false:
           if was_firing(rule): resolve(rule)
           pending_since = None

3. Push fire/resolve events to Alert Manager.
```

Why a `for` duration?
- **Suppresses flapping.** A 10-second blip in p99 shouldn't page anyone.
- **Lets transient issues self-heal** (a redeploy, a GC pause).

Why evaluate every 30 s and not continuously?
- Druid queries are not free; 100 K rules × continuous = unsustainable.
- 30–60 s is acceptable detection latency for *threshold* alerts.

#### Path 2 — Log/event alerts (the streaming evaluator)

For "tell me within seconds when error volume spikes," a periodic query is too slow. Use **Flink**, which is already in the pipeline:

```
Kafka topic ──► Flink Alert Job ──► Alert Manager
                  │
                  ├─ stateful: count by (service, error_class) over 1-min window
                  ├─ when count crosses threshold → emit fire event
                  └─ when window slides + count back below → emit resolve
```

This is why Flink earns its keep — it can run **thousands of windowed aggregations** continuously across the same event stream that's already feeding Druid/ES, with sub-second latency. The same job can express:

- "more than N errors per minute from one service"
- "any FATAL log line from a tier-0 service" (immediate fire)
- "a specific request_id appears with `status=500`" (rare, but supported)

#### Alert Manager — the part everyone forgets

Producing fire/resolve events is the easy half. The Alert Manager handles the **operational reality**:

| Concern | What it does |
| ------- | ------------ |
| **Deduplication** | Same alert from two evaluator replicas → one notification. Key by `(rule_id, label_set)`. |
| **Grouping** | 50 hosts firing the same alert at once → one grouped notification, not 50 pages. |
| **Inhibition** | If a "datacenter down" alert is firing, suppress all the dependent "service unreachable" alerts. |
| **Silencing** | Operator clicks "snooze for 1 h during deploy" → manager drops matching alerts. |
| **Routing** | `team=payments` → PagerDuty rotation A; `team=platform` → rotation B; `severity=warn` → Slack only. |
| **Escalation** | No ack within 10 min → escalate to secondary on-call. |
| **Retry & idempotency** | PagerDuty/Slack APIs fail; retry with idempotency key so the on-call doesn't get paged 5 times. |

This is why we use **Alertmanager** (Prometheus's, or the same pattern in PagerDuty/Opsgenie) — building this from scratch is a multi-quarter project.

#### State, HA, and persistence

- **Rule store** is a regular database (Postgres or etcd). Rules are infrequently changed and need versioning + audit history.
- **Evaluator state** (`pending_since`, last-fire time) is small and rebuilt from the rule store + recent metric data on restart. Run the evaluator with **leader election** (one active, one hot standby) to avoid double-firing.
- **Flink alert job state** is checkpointed to S3 like any Flink job — survives restarts, can be replayed.
- **Alert Manager state** (active alerts, silences, group memberships) is replicated across 3 nodes via gossip (Alertmanager's design) or stored in a small Raft KV.

#### Reliability patterns to call out

- **Test alerts in CI.** A unit test for every rule that asserts the expression actually fires on a synthetic input. Otherwise rules silently rot.
- **Self-monitoring is separate.** The alerting system must page you when *itself* is unhealthy — and that watchdog cannot share infrastructure with what it's monitoring (see top-5 mistakes).
- **Heartbeat alerts.** A "DeadMansSwitch" rule that *should always be firing*; if it stops firing, your alerting pipeline is broken. PagerDuty/Opsgenie have a built-in primitive for this.
- **Alert as code.** Rules live in Git, reviewed via PR, deployed by CI to the rule store. Avoids "someone tweaked a threshold in the UI at 3 AM."

#### Common alerting mistakes

| Mistake | Effect | Fix |
| ------- | ------ | --- |
| No `for` duration | Alert flaps every 10 s; on-call ignores them. | `for: 2m` minimum on threshold alerts. |
| Alerting on every error log line | Pager floods during a single bug. | Window + threshold + grouping. |
| One global alert manager with no silencing | Deploys page everyone. | Silences integrated with deploy tool. |
| No dead-man's-switch | Pipeline silently dies; you find out hours later. | Heartbeat rule that *must* fire continuously. |
| Self-alerting on the same Druid/Kafka the metrics use | When the system breaks, the alerts about it also break. | Separate, minimal monitoring instance. |
| Symptom-based alerts only | Page on every leaf cause; on-call drowns. | Alert on **user-facing symptoms** (SLO burn rate), drill down via dashboards. |

#### One-liner for the interviewer

> *"Metric alerts run on a 30-second periodic evaluator against Druid; log/event alerts run as a Flink streaming job for sub-second detection. Both feed an Alert Manager that handles dedup, grouping, silencing, and routing — because producing the fire event is the easy half; the hard half is not paging the on-call 50 times for the same incident."*

### Deep Dive E: Log Query (the read path)

The write path gets all the attention; the **read path** is what users actually touch. Two query shapes dominate, and they need different storage to hit the SLO of "find a `request_id` in seconds."

#### Two distinct query shapes

| Shape | Example | What it needs |
| ----- | ------- | ------------- |
| **Needle search** | "Show me logs where `request_id = abc123` in the last 24 h" | Inverted index on selective fields |
| **Aggregation / scan** | "Count ERROR per service per hour for the last 30 days" | Columnar storage + time pruning |

Same data, two indexes — that's why the architecture has both **Elasticsearch** (hot) and **S3 Parquet** (warm/cold).

#### Storage layout for queries

```
Time →  ─────────────────────────────────────────────────►
        ┌─────────┬──────────────┬───────────────────────┐
        │ < 7 d   │  7 d – 90 d  │   90 d – 1 y          │
        ├─────────┼──────────────┼───────────────────────┤
        │ ES hot  │ ES warm      │  S3 Parquet (Athena)  │
        │ (SSD)   │ (HDD, smaller│   columnar, partitioned
        │         │  shards)     │   by date+service     │
        └─────────┴──────────────┴───────────────────────┘

ES indices:    logs-payments-2026-05-04
               logs-payments-2026-05-03
               logs-checkout-2026-05-04
               ...                          ← one index per service per day
```

**Why daily indices per service:**
- Time pruning is free — query last 24 h ignores 89 other indices.
- Service pruning is free — `service=payments` hits only payments indices.
- **Retention is just `DELETE INDEX`** — no expensive row-level deletes.
- Independent shard counts per service — high-volume services get more shards.

#### What gets indexed (and what doesn't)

Inverted indexes are expensive — every indexed field roughly doubles write cost. Be selective:

| Field type | Indexed? | Why |
| ---------- | -------- | --- |
| `service`, `host`, `level`, `env`, `region` | **Yes** (keyword) | Used in nearly every filter. |
| `request_id`, `trace_id`, `user_id` | **Yes** (keyword) | Needle searches; high cardinality but each query is selective. |
| `ts` | **Yes** (date) | Range pruning. |
| `msg` body | **Tokenized** (full-text) | Optional — costly. Many teams skip and use grep on raw text instead. |
| `attrs.*` (free-form) | **No** by default | Stored as JSON; queryable via `match`, but slow. Promote to first-class fields if heavily used. |

> **Schema-on-write for hot paths, schema-on-read for the long tail.** The fields you alert/dashboard on become real ES fields; everything else stays inside `attrs` as a blob.

#### The query path, end-to-end

```
1. User issues query in Kibana / API:
     service=payments AND level=ERROR AND request_id=abc123
     time: last 24 h

2. Query Coordinator (ES coordinator node):
     a. Resolve indices touched:    logs-payments-2026-05-04, ...05-03
     b. Resolve shards:             3 shards × 2 days = 6 shards
     c. Fan-out query to each shard in parallel.

3. Each shard (a Lucene index):
     a. Use `request_id` inverted index → small posting list
     b. Intersect with `level=ERROR` posting list
     c. Apply time-range filter on the docs
     d. Return top-N matches with _source

4. Coordinator merges per-shard results,
   sorts by ts, returns page of N=100 results to the user.
```

A selective query like `request_id=abc123` is a **single inverted-index lookup** per shard — typically returns a posting list with 1–5 doc IDs, then a couple of disk reads for the JSON source. That's why it can be sub-second even over hundreds of GB.

#### What about queries older than 90 days?

ES is too expensive for cold data. Switch backends:

```
User: "Find all 5xx in checkout for March 2026" (8 months ago)
       │
       ▼
   Query API routes by time range:
       │
       ├─ < 90 d  → Elasticsearch
       │
       └─ ≥ 90 d  → Athena / Trino over S3 Parquet
                    SELECT ts, msg
                    FROM   s3.logs.checkout
                    WHERE  date BETWEEN '2026-03-01' AND '2026-03-31'
                      AND  status >= 500
                    -- Athena prunes via Hive partitions:
                    --   date=2026-03-15/service=checkout/part-0001.parquet
```

**Parquet wins for cold queries** because:
- **Columnar** — reading `status` doesn't read `msg` bytes.
- **Predicate pushdown** — Parquet's per-column min/max stats skip whole files.
- **Partitioning** by `date` + `service` lets Athena read 0.1% of objects.
- **No always-on cluster.** Athena/Trino are pay-per-query.

The trade-off vs. ES: needle queries on cold data are **slow** (10s of seconds, not sub-second). Acceptable because: (a) cold queries are rare, (b) they're usually scans, not needles, (c) cost is 100× lower.

#### The `request_id` shortcut: write-time inverted indexes for IDs

For the highest-value lookup ("find this trace"), some systems maintain a **lightweight separate index** independent of ES:

```
Kafka ──► Flink ──► writes  (request_id, ts, log_offset)  → small KV store (Bigtable / DynamoDB)

Query path:
   1. KV lookup on request_id → get list of (date, file, offset)
   2. Read those rows directly from S3 Parquet / Kafka
```

This is **a million times cheaper** than full-text-indexing a year of logs in ES. CockroachDB / Datadog use a variant of this for trace lookup.

#### Concurrency, caching, and rate limits at the query tier

| Concern | Mitigation |
| ------- | ---------- |
| One bad query blows up a shard | **Per-tenant query budget** — CPU-time + bytes-scanned quotas. Reject when exceeded. |
| Same dashboard refreshes every 30 s | **Result cache** keyed by `(query, time-window)` with short TTL. Grafana does this client-side too. |
| User asks for 1 B rows | **Pagination + cursor** — never return unbounded result sets. |
| Slow query blocks fast queries | **Separate query queues** — interactive vs. batch on different ES coordinator pools. |
| Unauth'd cross-tenant query | **Row-level filter injected by gateway** — `tenant_id = $caller_tenant` is always added. |

#### Query latency budget (the SLO breakdown)

```
Target: P99 < 5 s for last-7-day log query

  Coordinator parsing + planning      :    50 ms
  Fan-out + shard execution (parallel):   500 ms ← inverted-index lookup
  Source-doc fetch (top 100)          :   200 ms ← random reads on segments
  Merge + sort + paginate             :   100 ms
  Network round trips                 :   100 ms
  ──────────────────────────────────────────────
  Headroom for outliers               : 4,050 ms
```

If you blow the budget, the suspects are usually:
- **Too many shards per query** (over-sharded daily indices). Cap shard count per query.
- **Source bloat** — `_source` of each doc is huge. Project only fields you need.
- **Cold shards on slow disks** — ES warm tier on HDD; first read pays the seek tax.

#### Common log-query mistakes

| Mistake | Effect | Fix |
| ------- | ------ | --- |
| One giant ES index for all services | Every query fans out to every shard. | One index per service per day. |
| Index `msg` as full-text by default | Doubles storage; few queries actually use it. | Tokenize only when teams ask for it. |
| Keep all data in ES forever | Storage cost explodes; cluster instability. | Tier to Parquet at 7–90 d. |
| Same ES cluster for queries and ingest | A bad query starves writes. | Separate coordinator nodes / clusters. |
| No rate limits on free-text search | One regex query takes the cluster down. | Per-tenant CPU/byte budgets. |
| Queries treated as best-effort | Devs lose trust ("it's slow today"). | Publish a query-latency SLO and alert on it. |

#### One-liner for the interviewer

> *"Logs are queried two ways — needle searches on `request_id` / `trace_id`, and aggregations over time. We index high-value fields in Elasticsearch with one daily index per service for time + service pruning, then tier to Parquet on S3 for queries older than 90 days where Athena's columnar scan is 100× cheaper. The whole read path runs on a separate coordinator pool with per-tenant budgets so one bad query can't take the cluster down."*

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
| **Periodic vs streaming alerts** | "Threshold alerts on Druid every 30 s; log-pattern alerts via Flink streaming for sub-second detection. Both feed one Alert Manager so dedup/silence/routing is centralized." |
| **ES vs Parquet for log query**  | "ES for needle searches < 90 d (inverted-index sub-second lookups); Parquet on S3 + Athena for cold scans where columnar pruning is 100× cheaper." |

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
| Alert Manager handles **dedup / grouping / silencing**         | "Just send each fired alert straight to PagerDuty"    |
| **Dead-man's-switch** heartbeat alert                          | "We'll know if alerting breaks because…" (you won't)  |
| **Per-service daily indices** for log query                    | "One huge index for everything"                       |
| **Per-tenant query budgets** (CPU/bytes-scanned)               | "One regex query takes down the cluster"              |
