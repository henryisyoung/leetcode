# Design Host Listings Page Aggregation Optimization

A host opens the dashboard, chooses a date range, and sees per-listing aggregates:

- nights booked
- average price
- optionally gross revenue, occupancy rate, cancellation count

The host may have 100+ listings, and the page must load in sub-second latency.

The interview signal is diagnosing the slow SQL plan first, then redesigning the read path with pre-aggregation. Do not jump straight to "add cache".

---

## 0-5 min: Clarify

### Functional Requirements

- Host chooses `start_date` and `end_date`.
- Return one row per listing owned by the host.
- For each listing:
  - `nights_booked`
  - `avg_price`
- Support hosts with 100+ listings.
- Include recent changes such as new bookings or cancellations.

### Non-Functional Requirements

| Requirement | Target |
| --- | --- |
| Page latency | Sub-second p95 |
| Scale | Many hosts, some with hundreds/thousands of listings |
| Query pattern | Read-heavy dashboard page |
| Freshness | Near-real-time for today; daily is acceptable for older dates |
| Correctness | Aggregates should converge after late cancellations / edits |

---

## Existing Tables

```text
Listings
├── listing_id
├── host_id
├── title
├── status

Reservations
├── reservation_id
├── listing_id
├── checkin_date
├── checkout_date
├── status              // CONFIRMED / CANCELLED
├── total_price_cents

Pricing
├── listing_id
├── date
├── price_cents
```

Assume reservation dates follow hotel semantics:

```text
checkin <= night < checkout
```

---

## Diagnose the Bad Query

Naive query:

```sql
SELECT
    l.listing_id,
    COUNT(DISTINCT p.date) AS nights_booked,
    AVG(p.price_cents) AS avg_price
FROM Listings l
JOIN Reservations r
  ON r.listing_id = l.listing_id
JOIN Pricing p
  ON p.listing_id = l.listing_id
 AND p.date >= r.checkin_date
 AND p.date <  r.checkout_date
WHERE l.host_id = :host_id
  AND r.status = 'CONFIRMED'
  AND p.date >= :start_date
  AND p.date <  :end_date
GROUP BY l.listing_id;
```

Why this is slow:

- One host maps to 100+ listings.
- Each listing maps to many reservations.
- Each reservation expands into many daily pricing rows.
- The join creates a large intermediate result before aggregation.
- The dashboard repeats this work every page load.

Bad plan shape:

```text
host_id -> listings
        -> reservations for all listings
        -> pricing rows for every reserved night
        -> group by listing_id
```

The expensive part is fan-out from listings to `reservations x nightly pricing rows`.

---

## Optimized Design: Daily Roll-Up

Maintain a pre-aggregated daily table:

```text
ListingStatsDaily
├── host_id
├── listing_id
├── date
├── nights_booked        // 0 or 1 for that listing-date
├── gross_revenue_cents  // booked price for that night
├── price_count          // 1 if booked, else 0
├── updated_at
PRIMARY KEY (listing_id, date)
```

For dashboard queries, aggregate over a much smaller table:

```sql
SELECT
    listing_id,
    SUM(nights_booked) AS nights_booked,
    CASE
        WHEN SUM(price_count) = 0 THEN 0
        ELSE SUM(gross_revenue_cents) / SUM(price_count)
    END AS avg_price_cents
FROM ListingStatsDaily
WHERE host_id = :host_id
  AND date >= :start_date
  AND date <  :end_date
GROUP BY listing_id;
```

This removes joins from the hot path. The page query becomes a range scan + group-by over daily facts.

---

## Index and Partitioning

### Primary Access Pattern

The dashboard query filters by:

```text
host_id + date_range
```

and groups by:

```text
listing_id
```

Recommended index:

```sql
CREATE INDEX idx_listing_stats_host_date_listing
ON ListingStatsDaily(host_id, date, listing_id);
```

Alternative if the app first fetches listings:

```sql
CREATE INDEX idx_listing_stats_listing_date
ON ListingStatsDaily(listing_id, date);
```

Then query:

```sql
SELECT
    listing_id,
    SUM(nights_booked) AS nights_booked,
    SUM(gross_revenue_cents) / NULLIF(SUM(price_count), 0) AS avg_price_cents
FROM ListingStatsDaily
WHERE listing_id IN (:listing_ids)
  AND date >= :start_date
  AND date <  :end_date
GROUP BY listing_id;
```

For very large scale, partition by month:

```text
ListingStatsDaily_2026_01
ListingStatsDaily_2026_02
...
```

A date range then scans only relevant partitions.

---

## Refresh Strategy

### Pick: Batch for Historical + Live Delta for Today

Most dashboard queries look at historical ranges where yesterday-and-earlier can be stable.

Use two layers:

```text
ListingStatsDaily          // batch-built for yesterday and earlier
ListingStatsLiveDelta      // small near-real-time table for today/recent changes
```

Read path:

```sql
-- historical roll-up
SELECT listing_id, SUM(nights_booked), SUM(gross_revenue_cents), SUM(price_count)
FROM ListingStatsDaily
WHERE host_id = :host_id
  AND date >= :start_date
  AND date < :end_date
GROUP BY listing_id

UNION ALL

-- live updates not yet compacted
SELECT listing_id, SUM(nights_booked_delta), SUM(revenue_delta), SUM(price_count_delta)
FROM ListingStatsLiveDelta
WHERE host_id = :host_id
  AND date >= :start_date
  AND date < :end_date
GROUP BY listing_id;
```

The service merges the two aggregate sets by `listing_id`.

Why this is a good interview answer:

- Batch is simple and reliable for historical data.
- Live delta keeps the current dashboard fresh.
- The live table stays small.

### Alternative: Fully Streaming

```text
Reservation events -> Kafka -> stream processor -> ListingStatsDaily upsert
```

Pros:

- Fresher.
- One table for reads.

Cons:

- More operational complexity.
- Must handle duplicate events, out-of-order events, and replay.

Both are defensible. For a dashboard, batch + live delta is usually easier to reason about.

---

## Handling Cancellations and Edits

Reservations create daily positive deltas:

```text
booking confirmed:
  for each night:
    nights_booked_delta = +1
    revenue_delta = nightly_price
    price_count_delta = +1
```

Cancellations create negative deltas:

```text
reservation cancelled:
  for each night:
    nights_booked_delta = -1
    revenue_delta = -nightly_price
    price_count_delta = -1
```

The event should include enough information to reverse the original contribution, especially if pricing changed after booking.

Use idempotency:

```text
event_id
reservation_id
event_type
listing_id
date
delta fields
```

The consumer records processed `event_id`s or uses deterministic upserts keyed by `(reservation_id, date, event_type)`.

---

## Cache

Cache the rendered dashboard or the aggregate response:

```text
cache key = (host_id, start_date, end_date, filters, stats_version)
TTL = 60 seconds
```

Invalidation:

- On booking/cancellation for that host, delete affected `(host_id, date_range)` keys if easy.
- If range invalidation is hard, rely on short TTL plus a per-host version.

Example:

```text
host_stats_version:{host_id} increments on booking events
cache key includes version
```

Cache helps repeated dashboard refreshes, but it is not the primary fix. The primary fix is avoiding the huge join.

---

## Read Path

```text
Client
  |
  v
Dashboard API
  |
  +--> get host listings once
  |
  +--> read aggregate table for all listings in one query
  |
  +--> merge historical + live deltas
  |
  +--> hydrate listing names/photos/status
  |
  v
Response
```

Avoid:

```text
for each listing:
    query reservations
    query pricing
    aggregate
```

That creates 100+ database round trips and repeats the same expensive work.

---

## Optimized SQL Side-by-Side

### Bad

```sql
SELECT l.listing_id, COUNT(*), AVG(p.price_cents)
FROM Listings l
JOIN Reservations r ON r.listing_id = l.listing_id
JOIN Pricing p ON p.listing_id = l.listing_id
WHERE l.host_id = :host_id
  AND p.date >= :start_date
  AND p.date < :end_date
GROUP BY l.listing_id;
```

Problem: joins large raw tables and aggregates after fan-out.

### Good

```sql
SELECT
    listing_id,
    SUM(nights_booked) AS nights_booked,
    SUM(gross_revenue_cents) / NULLIF(SUM(price_count), 0) AS avg_price_cents
FROM ListingStatsDaily
WHERE host_id = :host_id
  AND date >= :start_date
  AND date < :end_date
GROUP BY listing_id;
```

Benefit: scans already-compacted daily facts.

---

## Capacity Math

Suppose:

```text
host has 100 listings
each listing has 100 reservations/year
avg stay = 4 nights
date range = 1 year
```

Naive path:

```text
100 listings x 100 reservations x 4 pricing rows = 40,000 joined rows
```

before grouping, and that ignores indexes, cancellations, and extra joins.

Roll-up path:

```text
100 listings x 365 daily stat rows = 36,500 rows
```

The row count may look similar for a full year, but each row is narrow, already denormalized, and avoids expensive joins. For 30 days:

```text
100 listings x 30 rows = 3,000 rows
```

which is easy to serve sub-second with the right index.

---

## Failure Modes

| Failure | Handling |
| --- | --- |
| Roll-up job delayed | Show cached stats with "last updated" timestamp |
| Duplicate reservation event | Idempotent event processing |
| Cancellation after roll-up | Emit negative delta and compact later |
| Pricing edited after booking | Use booked nightly price, not current listing price |
| Host has thousands of listings | Paginate listings, precompute per-host summaries |
| Cache stale | Short TTL + host stats version invalidation |

---

## Interview Script

Start with diagnosis:

```text
The slow part is the query plan. One host fans out to many listings, then to many reservations, then each reservation expands into nightly pricing rows. The database does the group-by only after creating a large intermediate result.
```

Then propose:

```text
I would move this dashboard to a daily roll-up table keyed by listing_id and date. The page query becomes a range scan over ListingStatsDaily, grouped by listing_id. For freshness, I would batch-build historical rows and maintain a small live delta table for today's booking and cancellation events.
```

Deep dives to volunteer:

- bad SQL vs optimized SQL,
- roll-up schema,
- cancellation as negative deltas,
- index choice,
- short dashboard cache with invalidation.

