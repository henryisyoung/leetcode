# Design Query System: Time + Geo Filtered User Activity

Internal users need to look up an end-user's searches and bookings filtered by:

```text
(user_id, start_ts, end_ts, geo_bbox) -> search + booking events
```

The same system also supports aggregations by region and time bucket, such as count and sum amount.

This prompt is intentionally thin. The senior signal is asking the right query-pattern questions before choosing storage layout.

---

## 0-5 min: Clarify

### Functional Requirements

- Query raw events for one user:
  - searches
  - bookings
  - filtered by time window
  - filtered by geographic bounding box
- Return event details:
  - event id
  - event type
  - timestamp
  - user id
  - lat/lng
  - geohash
  - booking amount when applicable
- Support aggregations:
  - count by region and time bucket
  - sum booking amount by region and time bucket

### Clarifying Questions

- Are most queries scoped to a single `user_id`, or do users often query a whole region?
- Is this an interactive support/debugging tool, an analytics dashboard, or both?
- How fresh must the data be?
- How large is the typical time range: one day, one week, one year?
- Is `geo_bbox` required, optional, or can it be country-sized?
- Do we need exact bounding-box correctness or approximate geohash prefix matching?

### Non-Functional Requirements

| Requirement | Target |
| --- | --- |
| Data size | Tens of TB historical |
| Typical latency | < 1s for single user + week + country-sized region |
| Analytics sweep latency | Multi-second acceptable |
| Freshness | Up to ~5 minutes stale is fine |
| Workload | Read-heavy |
| Consistency | Eventual consistency on ingest |

---

## High-Level Architecture

```text
Client / Internal Tool
  |
  v
Query API
  |
  +--> Interactive Store
  |       Elasticsearch / ClickHouse
  |       optimized for user + time + geo lookups
  |
  +--> Roll-Up Store
  |       pre-aggregated count/sum by region + hour
  |
  +--> Warehouse
          BigQuery / Snowflake for large offline analytics


Event Producers
  |
  v
Kafka
  |
  +--> Stream ETL / Flink
  |       enrich event, compute geohash, validate schema
  |
  +--> Interactive Index Writer
  |
  +--> Roll-Up Aggregator
  |
  +--> Warehouse Loader
```

Use separate stores because one storage layout will not be optimal for both interactive lookup and large analytical sweeps.

---

## Event Model

Normalize searches and bookings into one activity event table.

```text
UserActivityEvent
├── event_id
├── event_type          // SEARCH / BOOKING
├── user_id
├── event_ts
├── event_date
├── lat
├── lng
├── geohash_5
├── geohash_6
├── geohash_7
├── country
├── region_id
├── listing_id          // nullable for searches
├── query_text          // nullable for bookings
├── booking_amount_cents // nullable for searches
├── currency
├── ingest_ts
```

Store both:

- raw `lat/lng` for exact bounding-box filtering,
- geohash prefixes for fast coarse pruning.

---

## Ingest Path

```text
Search Service / Booking Service
  |
  v
Kafka topic: user_activity_events
  |
  v
ETL consumer
  |
  +--> validate schema
  +--> dedupe by event_id
  +--> compute event_date
  +--> compute geohash prefixes
  +--> write to interactive index
  +--> write to roll-up table
  +--> write to warehouse
```

Freshness target is 5 minutes, so a small streaming ETL delay is acceptable.

Use idempotent writes keyed by `event_id` because producers and consumers may retry.

---

## Geo Indexing

At ingest time:

```text
lat/lng -> geohash
```

Example prefixes:

```text
geohash_5: metro / city-ish
geohash_6: neighborhood-ish
geohash_7: smaller local area
```

Query flow for `geo_bbox`:

1. Convert the bounding box to covering geohash prefixes.
2. Query by geohash prefix to prune candidates.
3. Apply exact `lat/lng` bounding-box check to remove false positives.

Why not only geohash:

- Geohash cells do not perfectly match rectangles.
- Prefix coverage can include nearby areas outside the bbox.
- Exact lat/lng filter is still needed for correctness.

### Geohash Trade-Off

| Option | Pros | Cons |
| --- | --- | --- |
| Geohash | Simple string prefix, easy indexing, good enough for bbox pruning | Cell boundaries create false positives |
| Quadtree | Natural spatial hierarchy | More custom logic |
| R-tree | Strong spatial queries | Harder to distribute in common KV/index stores |

Pick geohash for simple storage and prefix-based pruning.

---

## Time Partitioning

Partition by event date:

```text
daily partition: event_date = YYYY-MM-DD
```

For higher write volume, use hourly partitions:

```text
event_hour = YYYY-MM-DD-HH
```

Typical query:

```text
single user + 7-day window
```

The query first prunes to 7 daily partitions, then uses indexes within those partitions.

Daily partitioning usually wins the latency budget because it avoids scanning irrelevant history.

---

## Interactive Store Layout

### Option A: User-First Index

Best if most queries are:

```text
user_id + time window + optional geo
```

Index:

```text
partition: event_date
primary sort / index: user_id, event_ts
secondary: geohash prefix
```

Query:

```sql
SELECT *
FROM user_activity_events
WHERE event_date BETWEEN :start_date AND :end_date
  AND user_id = :user_id
  AND event_ts >= :start_ts
  AND event_ts < :end_ts
  AND geohash_5 IN (:covering_prefixes)
  AND lat BETWEEN :min_lat AND :max_lat
  AND lng BETWEEN :min_lng AND :max_lng
ORDER BY event_ts DESC
LIMIT :limit;
```

Pros:

- Fast for the required single-user query.
- Reads very few rows after user pruning.

Cons:

- Region-wide scans are expensive.

### Option B: Region-First Index

Best if most queries are:

```text
region + time window
```

Index:

```text
partition: event_date
primary sort / index: geohash_prefix, event_ts
secondary: user_id
```

Pros:

- Fast regional queries and dashboards.

Cons:

- Single-user lookup may scan more rows in a large region.

### Recommended: Hybrid Dual Index

Because the prompt includes both user lookup and regional aggregation:

```text
UserActivityByUser
├── partition by event_date
├── sort/index by user_id, event_ts

UserActivityByGeo
├── partition by event_date
├── sort/index by geohash_5/6/7, event_ts
```

Write each event to both indexes.

Trade-off:

- Higher write/storage cost.
- Much better read latency for both access patterns.

This is acceptable because the system is read-heavy and batch-tolerant.

---

## Query Flow

### Raw Event Lookup

```text
Input: user_id, start_ts, end_ts, geo_bbox
```

Steps:

1. Validate and cap the time range.
2. Convert `geo_bbox` to geohash prefixes.
3. Choose index:
   - user-scoped query -> `UserActivityByUser`
   - region-only query -> `UserActivityByGeo`
4. Prune by time partition.
5. Filter by user id.
6. Filter by geohash prefix.
7. Apply exact lat/lng bbox filter.
8. Sort and paginate.

### Pseudocode

```text
partitions = daysBetween(start_ts, end_ts)
prefixes = geohashesCovering(geo_bbox)

rows = interactiveStore.query(
    partitions = partitions,
    user_id = user_id,
    geohash_prefixes = prefixes,
    start_ts = start_ts,
    end_ts = end_ts
)

return rows
    .filter(row inside exact bbox)
    .sortBy(event_ts desc)
    .limit(page_size)
```

---

## Aggregation Roll-Up

Do not aggregate regional dashboards from raw events every time.

Maintain:

```text
ActivityStatsHourly
├── region_id or geohash_5
├── hour_bucket
├── event_type
├── event_count
├── booking_amount_sum_cents
├── updated_at
PRIMARY KEY (region_id, hour_bucket, event_type)
```

Query:

```sql
SELECT
    region_id,
    hour_bucket,
    event_type,
    SUM(event_count) AS event_count,
    SUM(booking_amount_sum_cents) AS booking_amount_sum_cents
FROM ActivityStatsHourly
WHERE region_id IN (:regions)
  AND hour_bucket >= :start_hour
  AND hour_bucket < :end_hour
GROUP BY region_id, hour_bucket, event_type;
```

This serves dashboards and common analytics queries without scanning raw events.

---

## Storage Choice

### ClickHouse

Good fit for:

- columnar scans,
- time partitions,
- aggregations,
- sorting by `(user_id, event_ts)` or `(geohash, event_ts)`.

Example table:

```sql
CREATE TABLE UserActivityByUser (
    event_date Date,
    event_ts DateTime,
    user_id String,
    event_id String,
    event_type String,
    geohash_5 String,
    geohash_6 String,
    geohash_7 String,
    lat Float64,
    lng Float64,
    booking_amount_cents Int64
)
ENGINE = MergeTree
PARTITION BY event_date
ORDER BY (user_id, event_ts);
```

### Elasticsearch

Good fit for:

- text search,
- inverted indexes,
- flexible filtering,
- support/debug tools.

Less ideal for huge aggregations compared with a columnar store.

### Warehouse

BigQuery / Snowflake is for:

- offline analysis,
- backfills,
- model training,
- long range analytics sweeps.

Not the primary store for sub-second interactive queries.

---

## Caching

### Raw Event Query Cache

Cache is less useful for raw user lookups because queries are often user-specific.

Use short cache only for repeated support-tool refreshes:

```text
key = (user_id, start_ts, end_ts, bbox_hash, page)
TTL = 30-60 seconds
```

### Aggregation Cache

Useful for dashboards:

```text
key = (region_id, time_bucket_range, event_type)
TTL = 1-5 minutes
```

Better: serve common dashboards from `ActivityStatsHourly`.

---

## Latency Budget

Typical query: single user, week window, country-sized region.

```text
API/auth                 20 ms
partition selection       5 ms
geohash cover             5 ms
interactive store query 200-500 ms
exact bbox filter        20 ms
serialization            50 ms
network                  50 ms
------------------------------
p95 target             < 1 s
```

The big win is partition pruning by time and user-first index pruning.

---

## Failure Modes

| Failure | Handling |
| --- | --- |
| Kafka delay | Show "data delayed up to N minutes" |
| Duplicate events | Idempotent writes by `event_id` |
| Late-arriving events | Upsert into correct historical partition |
| Geo false positives | Exact lat/lng bbox filter |
| Large time range | Switch to async export / warehouse query |
| Hot geohash | Split prefix or shard by `(geohash, hash(user_id))` |
| Interactive store degraded | Fall back to warehouse for async result |

---

## Interview Script

Open with clarifying query patterns:

```text
Before choosing the storage layout, I want to know whether the dominant query is user-scoped or region-scoped. If it is user-scoped, I partition by time and index by user_id. If region-scoped queries are also common, I maintain a second geohash-oriented index.
```

Then present the design:

```text
Events go through Kafka and ETL. At ingest, I compute geohash prefixes and write to an interactive store for sub-second lookup plus a warehouse for offline analytics. The query prunes by time partition first, then user or geohash, then applies exact lat/lng filtering. Aggregations are served from hourly roll-ups instead of raw events.
```

Deep dives to volunteer:

- user-first vs region-first index trade-off,
- geohash prefix plus exact bbox filter,
- daily partition pruning,
- aggregation roll-up table,
- 5-minute freshness and idempotent ingest.

