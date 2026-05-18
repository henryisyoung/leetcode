# Design an Interactive Data Analysis System — 60-min Interview Version

Ingest billions of events per day; let analysts and product surfaces run **interactive** queries (slice/dice, top-N, time series) with **sub-second** latency on the most recent data and reasonable latency on the full history. The interviewer's hook is: *"Spark batch would be too slow — show me streaming."*

Concrete framing: power Airbnb's host dashboard ("views, bookings, revenue today / 7d / 90d, broken down by listing, region, price tier") plus internal exploratory analytics.

**Time budget**

| Minutes | What you do                              |
| ------- | ---------------------------------------- |
| 0–5     | Clarify + capacity                       |
| 5–10    | Lay out the latency spectrum             |
| 10–25   | Batch (Spark) first, then Lambda → Kappa |
| 25–45   | Flink + Kafka deep dive (the main event) |
| 45–55   | Flink recovery + exactly-once            |
| 55–60   | Trade-offs and wrap-up                   |

---

## 0–5 min: Clarify

### Step 1 — Pin "interactive" before you draw a box

> *"Interactive" can mean three different latencies. Which one?*

| Tier              | p99 target | Example                                 | Architecture implication        |
| ----------------- | ---------- | --------------------------------------- | ------------------------------- |
| **Real-time**     | < 500 ms   | "Live booking counter on host dashboard" | Stream → OLAP serving layer     |
| **Near-real-time**| 1–10 s     | "Searches in the last 5 min"             | Stream aggregation              |
| **Interactive BI**| 1–30 s     | "Bookings by region by week, last 2 yrs" | Columnar warehouse (Snowflake / BigQuery) |

The interviewer's "high interactivity" framing → tier 1 and 2. Tier 3 (Snowflake) is the batch fallback we'll cover too.

### Step 2 — Functional requirements

- Ingest events (clicks, impressions, bookings, server logs) from web + mobile + backend.
- **Aggregations**: counters, gauges, distinct-cardinality, percentiles, top-N.
- **Slicing** by N dimensions (`region × device × listing_type × …`).
- **Time ranges**: live (last minute) to last 2 years.
- Ad-hoc queries from analysts; pre-defined queries from dashboards.

### Step 3 — Non-functional requirements

| Requirement                | Target                                        |
| -------------------------- | --------------------------------------------- |
| Dashboard query p99        | < 500 ms                                      |
| Freshness                  | < 5 s end-to-end for live counters            |
| Ad-hoc BI query (90 d)     | < 30 s                                        |
| Exactly-once semantics     | Required — bookings count must be exact       |
| Late events                | 24 h tolerated; older → quarantine            |
| Backfill / replay          | Re-run the last week without losing data      |

### Step 4 — Capacity math

```
1 M events/sec peak × 500 B avg          ≈ 500 MB/sec ≈ 43 TB/day raw
After 1-min pre-aggregation              ≈ ~5 K records/sec (×200 reduction)
Hot serving layer (Druid/Pinot) 30 d     ≈ ~50 GB compressed per metric × 100 metrics
Warm rollup 90 d                         ≈ ~150 GB per metric
Cold S3 Parquet years                    PB-scale
```

> **Take-away:** raw is huge; pre-aggregated is small. The serving layer is sized by **distinct (metric × dimension combination × bucket)**, not by raw event volume.

---

## 5–10 min: The Latency Spectrum

Lay this out **before** picking an architecture — it gives the interviewer the "Spark vs Flink" answer in advance.

```
< 1 s       Real-time dashboard          Flink → Druid/Pinot/ClickHouse
1–10 s      Near-real-time aggregation   Flink (windowed)
1–30 s      Interactive BI               ClickHouse / Druid / Snowflake on hot+warm
minutes     Daily / hourly batch         Spark on data lake (S3 Parquet + Iceberg)
hours       Backfill / ML feature prep   Spark, scheduled
```

> Say out loud: *"Spark batch is great for cheap, large, complete jobs. But its minimum-latency floor is the job startup time + shuffle — that's minutes, not seconds. For interactive dashboards I'll move to streaming."*

This single framing answers half the question. Now we go deep on **why** and **what replaces it**.

---

## 10–25 min: From Batch (Spark) → Lambda → Kappa

### Why Spark batch alone doesn't cut it

```
raw events ──► S3 (Parquet) ──► Spark scheduled job (every 5 min) ──► dashboard table
```

Pros:
- **Cheap** per byte processed.
- **Easy backfill** — rerun any window.
- **Complete views** — every event seen before the job runs.

Cons (the interviewer is fishing for these):
- **Minimum latency = batch interval + job time**. A 5-min Spark job means the dashboard is at least 5 min stale.
- **Startup overhead** — cluster acquisition, executor spinup, shuffle setup is in the seconds-to-minutes range even before processing.
- **Bursty resource curve** — idle for 4 min, peaks for 1 min, oversized cluster sits unused.
- **No mid-batch incremental view** — analysts can't see "the last 30 seconds" no matter what.

> **The freshness lower bound of any micro-batch system is ~its scheduling interval.** Streaming has no such floor — it processes records as they arrive.

### Lambda Architecture (the classical compromise)

```
        ┌─── Batch layer  ──► Spark (re-process the full day) ──► Hive/Iceberg ──┐
events ─┤                                                                       │
        └─── Speed layer  ──► Storm/Flink (last few hours)   ──► fast cache    ─┴─► Serving layer
                                                                                    (Druid / Pinot)
                                                                                    merges both
```

- Batch layer: source of truth, slow, periodic.
- Speed layer: approximate / incomplete, fast.
- Serving layer: merges, prefers batch when available.

**Why it fell out of favor**:
- **Two pipelines** to maintain — same logic in Spark *and* in Flink, drifts inevitably.
- **Merging** is hard — what does "the batch value just landed; throw away the speed value" actually mean for an interactive query in progress?
- **Two bug surfaces** for the same metric.

### Kappa Architecture (what we ship)

```
events ──► Kafka ──► Flink ──► Druid (hot) / S3 Parquet (cold)
              ▲          │
              │          └──► output topics
              │
       replayable; Spark CAN re-process from Kafka or S3 if needed
```

- **One pipeline**: Flink does both the live stream and (by replay) the historical re-compute.
- **Spark still exists** — for huge historical backfills + ML feature prep — but it reads from the same Kafka topic / S3 archive that Flink does. Same source of truth.
- **Source of truth = Kafka log**, with retention tuned to "long enough to replay everything we'd want to recompute" (often 7–14 days; older lives in S3).

> Tell the interviewer: *"I'd start Kappa. One pipeline, replayable. Spark is still in the picture for nightly heavy lifting, but live + interactive flows through one stream."*

### Where Druid / Pinot / ClickHouse fit (the serving layer)

A stream computes the right per-bucket values; you still need a place to **query** them fast.

| Store      | Strength                                                        | Limitation                                  |
| ---------- | --------------------------------------------------------------- | ------------------------------------------- |
| Druid      | Streaming ingest native; pre-aggregated rollups; time-first queries | Not great for high-cardinality joins        |
| Pinot      | Similar; widely used at LinkedIn / Uber for user-facing analytics | Same family                                 |
| ClickHouse | Best raw-event query speed; supports late updates               | Streaming ingest is bolted on, not native   |

All three offer **sub-second time-bucketed group-bys** on billions of rows. Pick one; mention the rest as alternatives.

---

## 25–45 min: Flink + Kafka Deep Dive (the main event)

### Why Flink for streaming aggregation

| Feature                | Why it matters here                                          |
| ---------------------- | ------------------------------------------------------------ |
| **Native event-time + watermarks** | Correct buckets even with late/out-of-order events  |
| **Keyed state**        | Per-`(metric, dim_key)` rolling aggregates in-process         |
| **Exactly-once via 2PC** | With Kafka source + sink, end-to-end exactly-once          |
| **Checkpointing**      | Survive worker crashes without double-counting               |
| **Windowing primitives** | Tumbling / sliding / session, all event-time based         |

Spark Structured Streaming can do this too — Flink is the canonical answer because it was streaming-first; Spark added it.

### The pipeline shape

```
producers ─► Kafka topic (raw events, partitioned by `entity_id`)
                │
                │  consumer-group: "flink-aggregator-v1"
                ▼
            Flink job:
              source ─► assignTimestampsAndWatermarks(BoundedOutOfOrderness(5s))
                     ─► keyBy(metric, dimension_tuple)
                     ─► window(TumblingEventTimeWindows.of(1 min))
                     ─► allowedLateness(24 h)
                     ─► aggregate(SumFunction)         // incremental: O(1) per event
                ▼
            Kafka sink (rollup topic, partitioned by metric)
                │
                ▼
            Druid Kafka indexer → queryable in seconds
```

### Flink + Kafka integration — the four things to get right

#### 1. Partitioning alignment

Kafka partition key = Flink keyBy field. If `entity_id` is the partition key in Kafka, `keyBy(entity_id)` in Flink keeps each entity's state on the same subtask. No cross-network shuffle for aggregation.

> A common mistake: produce to Kafka with no key (round-robin), then `keyBy` in Flink → every event shuffles across all subtasks. Brutal at 1 M events/sec.

#### 2. Source connector with consumer-group offsets

```
KafkaSource<Event> source = KafkaSource.<Event>builder()
    .setBootstrapServers(brokers)
    .setTopics("events")
    .setGroupId("flink-aggregator-v1")
    .setStartingOffsets(OffsetsInitializer.committedOffsets())
    .setBounded(OffsetsInitializer.latest())   // omit for unbounded
    .setValueOnlyDeserializer(...)
    .build();
```

Offsets are **committed in Flink checkpoints**, not via Kafka's auto-commit — Flink owns the offset → state alignment. Auto-commit in Kafka would advance offsets while Flink state hasn't been checkpointed, breaking exactly-once on restart.

#### 3. Sink with Kafka transactions (exactly-once)

```
KafkaSink<Rollup> sink = KafkaSink.<Rollup>builder()
    .setBootstrapServers(brokers)
    .setRecordSerializer(...)
    .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
    .setTransactionalIdPrefix("flink-aggregator-v1-")
    .build();
```

Behind the scenes: Flink uses **Kafka transactions** + **two-phase commit (2PC)** with checkpoints:

```
1. Open Kafka transaction on each checkpoint barrier passing through sink.
2. Write messages — they're "uncommitted" in Kafka, invisible to consumers with read_committed.
3. When the checkpoint completes globally (all operators acked), Flink commits the Kafka transaction.
4. Downstream consumers (Druid indexer) configured `isolation.level=read_committed`
   only see the committed batch — atomic, no partial writes.
```

> Two-phase commit + Kafka transactions + read_committed consumers = end-to-end exactly-once. Mention all three.

#### 4. Watermarks across partitions

Each Kafka partition emits its own watermark; Flink takes the **minimum** across partitions as the operator watermark. If one partition is idle (no events), it stalls every watermark behind it, which freezes downstream window emission.

Defenses:
- `withIdleness(Duration.ofSeconds(30))` on the watermark strategy — Flink will exclude idle partitions from the min.
- Producer-side **heartbeat events** on every partition keep clocks moving.

---

## 45–55 min: Flink Failure Recovery (the second main event)

### Checkpoint mechanism — Chandy-Lamport

Every N seconds (configurable, typical 30 s – 5 min), the **JobManager** injects a **checkpoint barrier** into each source's stream:

```
source ─[event][event][barrier][event]─► operator A ─[event][barrier][event]─► operator B ─► sink
                                                ▲                                ▲
                                                │ on barrier:                   │
                                                │ 1. snapshot local state       │
                                                │ 2. ack to JobManager          │
                                                │ 3. forward barrier downstream │
```

When **all operators ack the same checkpoint id**, the checkpoint is complete. JobManager writes a **checkpoint metadata file** pointing at:

- Per-operator state snapshots (in the state backend).
- Per-source offset positions (Kafka offsets).

### State backends

| Backend             | Where state lives           | When to use                                   |
| ------------------- | --------------------------- | --------------------------------------------- |
| Heap (in-memory)    | JVM heap, snapshot to S3    | Small state (< few GB per task), low latency  |
| RocksDB             | Local disk, snapshot to S3  | Large state (TB), the default for production  |

Snapshots are written **incrementally** for RocksDB — only changed SST files since last checkpoint go to S3. Critical for keeping checkpoint duration bounded as state grows.

### Recovery flow

```
TaskManager crashes mid-processing.

JobManager:
  1. Detects loss (heartbeat timeout).
  2. Halts the whole job. (Not just the dead task — to keep state consistent.)
  3. Allocates a fresh TaskManager.
  4. Restores from the LAST COMPLETED checkpoint:
       - Each operator loads its state from S3.
       - Kafka source seeks to the committed offsets in that checkpoint.
  5. Restarts the job. Processing resumes from the checkpoint boundary.
```

> Records between the last checkpoint and the crash get **reprocessed**. Combined with Kafka transactions + read_committed consumers, downstream sees no duplicates.

### Checkpoint interval — the trade-off

| Short interval (10 s)         | Long interval (5 min)         |
| ----------------------------- | ----------------------------- |
| Less reprocessing on recovery | More reprocessing on recovery |
| Higher overhead during normal | Lower overhead                |
| Smaller per-checkpoint state  | Larger state snapshots        |

Pick based on:
- **State size** — large state means longer checkpoint duration → must space them out.
- **Recovery SLO** — if "must catch up within 1 min" is the SLO, the interval has to fit.

Typical: **30 s – 60 s** for medium-state aggregations, **5 min** for huge state.

### Savepoints vs checkpoints

| Concept       | When triggered    | Purpose                                         | Retention            |
| ------------- | ----------------- | ----------------------------------------------- | -------------------- |
| **Checkpoint** | Automatic, periodic | Failure recovery within the same job version  | Last N kept; auto-pruned |
| **Savepoint**  | Manual            | Job upgrades, rescaling, state migration        | Operator-retained    |

Use a savepoint when you deploy a new version of the Flink job. The new job version starts from the savepoint and resumes with state intact. **Schema-evolution** of state is supported (with care): same operator UID, compatible state class.

### Backpressure handling

When a downstream operator slows down, Flink propagates **backpressure** upstream — earlier operators stop accepting more records. This is the right behavior (vs. dropping data) and the source eventually stops pulling from Kafka. Symptom: Kafka consumer lag grows.

Detect with:
- Flink Web UI's per-operator backpressure indicator.
- Kafka consumer lag metrics.
- Operator's `inPoolUsage` and `outPoolUsage` > 90%.

Causes & fixes:
- **Hot key** (one entity dominates) → salt the key, two-stage aggregation.
- **Slow sink** (Druid indexer GC) → scale the sink; tune Druid ingest.
- **Skewed parallelism** → increase parallelism; check key distribution.

### Late events vs allowed lateness

A "late" event arrives after the window's watermark has passed:

```
.window(TumblingEventTimeWindows.of(Time.minutes(1)))
.allowedLateness(Time.hours(24))   // window stays open; re-emit when late events land
.sideOutputLateData(lateTag)        // events past 24h go to a side stream for ops review
```

Each late arrival triggers a **re-emit** of the affected bucket. **The sink must support upsert by `(metric, dim_key, bucket_ts)`** — Druid does this natively (`mergeable` rollup tasks); ClickHouse via `ReplacingMergeTree`.

> If your sink is append-only (vanilla Parquet append), late events permanently undercount. Always pick a sink with upsert semantics for streaming aggregations.

### End-to-end correctness checklist

| Layer    | Setting                                                             |
| -------- | ------------------------------------------------------------------- |
| Producer | `acks=all`, idempotent producer, partition key = entity id          |
| Kafka    | `min.insync.replicas=2`, replication.factor=3                       |
| Flink source | `OffsetsInitializer.committedOffsets()`, no Kafka auto-commit   |
| Flink job    | Checkpointing + EXACTLY_ONCE + RocksDB + incremental snapshots  |
| Flink sink   | Kafka transactions + EXACTLY_ONCE, `transactionalIdPrefix` stable across restarts |
| Druid    | `isolation.level=read_committed`; rollup tasks merge late re-emits  |

---

## 55–60 min: Trade-offs / Common Mistakes

| Mistake                                                                | Effect                                            | Fix                                                  |
| ---------------------------------------------------------------------- | ------------------------------------------------- | ---------------------------------------------------- |
| Use Spark micro-batch for sub-second dashboards                        | Floor is ~minutes; never fits SLO                 | Move to Flink streaming                              |
| Lambda architecture for new builds                                     | Two pipelines, two bug surfaces                   | Kappa: stream is source of truth                     |
| Process-time aggregation (instead of event-time)                       | Looks fine at first, silently wrong over time     | Event-time + watermarks + allowed lateness           |
| Kafka auto-commit alongside Flink checkpoints                          | Offsets drift from state → duplicates on recovery | Disable auto-commit; Flink commits offsets in checkpoint |
| `keyBy` differently from Kafka partition key                           | Massive cross-network shuffle                     | Align Flink key with Kafka partition key             |
| EXACTLY_ONCE sink but downstream consumer reads uncommitted            | Sees partial / rolled-back data                   | Downstream `isolation.level=read_committed`          |
| `transactionalIdPrefix` changes between deploys                        | Kafka can't recover open transactions; data loss  | Stable prefix per Flink job                          |
| No idleness on watermark when some partitions are quiet                | Watermark stalls, windows never close             | `withIdleness(Duration.ofSeconds(30))`               |
| Heap state backend at TB scale                                         | OOM, slow checkpoints                             | RocksDB + incremental S3 snapshots                   |
| Long checkpoint interval + small recovery SLO                          | "Catch up after crash" misses SLO                 | Tune interval to recovery SLO + state size           |
| Sink is append-only Parquet                                            | Late events undercount permanently                | Druid / Pinot / ClickHouse with upsert by bucket key |
| Hot-key skew                                                           | One subtask is the bottleneck                     | Salt the key, two-stage aggregation                  |
| No backpressure metrics                                                | "It just feels slow" with no debug signal         | Track per-operator pool usage + Kafka consumer lag   |

### Key Concepts for the Interview

| Topic                                | What to say                                                                            |
| ------------------------------------ | -------------------------------------------------------------------------------------- |
| Latency tiers                        | Spark batch ≥ minutes; Flink streaming ≤ seconds. Pick by SLO.                          |
| Kappa over Lambda                    | One pipeline; replayable from Kafka or S3 archive; Spark is a heavy-lifting consumer.   |
| Event time + watermarks              | Correctness primitive. Allowed lateness keeps windows open for re-emits.                |
| Chandy-Lamport checkpoints           | Barriers flow through the DAG; per-operator state snapshots + Kafka offsets atomically. |
| Two-phase commit with Kafka          | Source committed offsets + sink Kafka transactions + downstream read_committed = E2E exactly-once. |
| RocksDB + incremental snapshots      | State scales to TB; checkpoint duration stays bounded.                                  |
| Savepoint for upgrades               | Versioned, manual; rescaling and schema evolution.                                      |
| Backpressure flows upstream          | Slow sink → Kafka lag grows. Don't drop; let the queue absorb.                          |
| Partition-aligned keyBy              | Kafka partition key matches `keyBy` → no shuffle.                                       |
| Watermark idleness                   | `withIdleness` is the fix for quiet partitions blocking the operator watermark.         |
| Sink must support upsert by bucket   | Otherwise late events break correctness; pick Druid/Pinot/ClickHouse.                   |

### Wrap-Up

| Aspect                          | Solution                                                              |
| ------------------------------- | --------------------------------------------------------------------- |
| Interactive sub-second queries  | Druid / Pinot / ClickHouse serving layer                              |
| Sub-second freshness            | Flink streaming aggregation, not Spark micro-batch                    |
| Single pipeline                 | Kappa architecture; Kafka as source of truth                          |
| Correctness on late events      | Event-time windows + allowed lateness + upsert-capable sinks          |
| Exactly-once end-to-end         | Flink 2PC + Kafka transactions + read_committed consumers             |
| Survive Flink failures          | Periodic checkpoints + RocksDB + incremental S3 snapshots             |
| Survive Kafka partition issues  | `acks=all`, `min.insync.replicas=2`, replication.factor=3             |
| Backfill + heavy historical     | Spark on the same Kafka/S3 source — kept for what it's good at        |
| Job upgrades / rescaling        | Savepoints, stable operator UIDs, compatible state classes            |
| Hot-key skew                    | Salt key, two-stage aggregation; monitor per-subtask throughput       |
