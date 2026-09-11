# Design Home Page Search + Availability + Ranking

Backend for the Airbnb home page: free-text search, filters, date-range availability, and personalized ranking with ML integration.

This is the senior/staff version of the booking-system prompt. The core signal is not just "use Elasticsearch", but explaining the four-stage funnel and where ML features are joined under sub-second latency.

---

## 0-5 min: Clarify

### Functional Requirements

- Search by free text, location, date range, guests, price band, property type, and amenities.
- Filter out listings unavailable for the requested date range.
- Rank results using user features, listing features, and context features.
- Support pagination / infinite scroll.
- Return listing cards: title, photos, price, rating, location, availability, badges.

### Non-Functional Requirements

| Requirement | Target |
| --- | --- |
| Scale | Tens of millions of listings, hundreds of millions of users |
| Traffic | Read-heavy, thousands of QPS at peak |
| Latency | Sub-second p95 end-to-end |
| Listing edit freshness | Seconds of staleness acceptable |
| Ranking signal freshness | Minutes of staleness acceptable |
| Booking correctness | Search can be stale; final booking path must re-check availability |

Out of scope: the transactional booking flow, payment, host calendar editing UI, and experimentation platform internals.

---

## 5-10 min: API

```http
GET /v1/home/search?query=beach&location=sf&checkin=2026-07-01&checkout=2026-07-05&guests=2&min_price=100&max_price=300&amenities=wifi,pool&cursor=...

Response:
{
  "request_id": "req_123",
  "results": [
    {
      "listing_id": "l_1",
      "title": "Ocean view apartment",
      "price_cents_per_night": 22000,
      "rating": 4.91,
      "thumbnail_url": "...",
      "badges": ["Guest favorite"]
    }
  ],
  "next_cursor": "..."
}
```

The cursor should encode the query hash, ranking version, page offset, and enough state to avoid reshuffling results across pages.

---

## 10-20 min: High-Level Architecture

```text
Client
  |
  v
API Gateway
  |
  v
Search Orchestrator
  |
  +--> Query Understanding
  |       normalize location, parse filters, expand synonyms
  |
  +--> L0 Retrieval
  |       Elasticsearch / custom geosharded index
  |       filters: location, guests, price, amenities, availability
  |
  +--> Feature Join
  |       online user features + offline listing features + request context
  |
  +--> L1 Light Rank
  |       two-tower / embedding similarity over ~1000 candidates
  |
  +--> L2 Deep Rank
  |       GBDT / MLP over top ~100 candidates
  |
  +--> Result Hydration
          listing cards, price display, photos, badges
```

Search is a read-optimized eventually consistent path. The booking service remains the source of truth for final availability.

---

## Data Model

### Listing Source of Truth

```text
listings
├── listing_id
├── host_id
├── title, description
├── lat, lng, geohash
├── max_guests
├── property_type
├── amenities
├── base_price
├── status              // ACTIVE / PAUSED / DELETED
├── updated_at

listing_availability
├── listing_id
├── date
├── status              // OPEN / HELD / BOOKED / BLOCKED

listing_features_offline
├── listing_id
├── price_embedding
├── quality_score
├── conversion_rate_7d
├── host_response_rate
├── review_score
├── refreshed_at
```

### Search Index Document

```text
listing_search_doc
├── listing_id
├── geohash_prefixes
├── title_tokens, description_tokens
├── max_guests
├── price_bucket
├── amenity_tokens
├── property_type
├── availability_bitmap_by_month
├── static_listing_features
├── index_version
```

The index is denormalized so L0 retrieval does not call the listing database.

---

## L0 Retrieval: Index + Filters

### Sharding

Shard listings by `geohash_prefix`.

```text
city query -> geohash prefixes -> small fan-out to relevant shards
```

For dense cities, split a geohash prefix into smaller sub-prefixes. For sparse rural areas, query a wider prefix. The orchestrator chooses the fan-out based on map bounds and result count.

### Filters

Push cheap filters into the index:

- location / geohash
- guests
- price bucket
- amenities
- property type
- active listing status
- date-range availability

The target is to retrieve around `1000` candidates, not all matching listings.

---

## Availability Filtering

Maintain a compact `availability_bitmap` per listing.

```text
listing_id = l_123
2026-07 bitmap:
day 1 2 3 4 5 ...
    1 1 1 0 1 ...
```

At query time:

```text
requested_range_mask = bits for checkin <= date < checkout
available if (listing_bitmap & requested_range_mask) == requested_range_mask
```

For short date ranges, bitmap intersection is fast in memory. For large date ranges or high-QPS paths, also index daily booleans / date buckets so Elasticsearch can prune earlier.

Important caveat: search availability can be stale. Before checkout, the booking service must re-read source-of-truth availability and atomically reserve the dates.

---

## Ranking Funnel

### L0: Retrieval

Input: parsed query + filters.

Output: ~1000 candidates.

Goal: high recall, low latency.

Common implementation:

- Elasticsearch BM25 for text.
- Geospatial filtering by geohash.
- Availability and structured filters.
- Optional ANN vector retrieval for semantic query/listing matching.

### L1: Light Ranking

Input: ~1000 candidates.

Use a two-tower model:

```text
user tower:    user features + recent search context -> user embedding
listing tower: listing features -> listing embedding
score = dot(user_embedding, listing_embedding)
```

Why two-tower:

- Very fast scoring.
- Listing embeddings can be precomputed offline.
- User embedding can be computed online from recent context.

Limitation:

- It does not model rich cross-features well, such as "user prefers cheap listings only for long stays".

Output: top ~100 candidates.

### L2: Deep Ranking

Input: top ~100 candidates.

Use GBDT / MLP with richer cross-features:

- user recent searches
- clicked / wishlisted listing categories
- trip length
- price elasticity
- host quality
- review score
- distance to searched location
- cancellation risk
- listing freshness
- conversion probability

Output: final ordered results.

The deep model is slower, so only run it on the smaller candidate set.

---

## Feature Store

### Online User Features

Stored in Redis / DynamoDB / Cassandra with low-latency reads.

Examples:

- last N searches
- clicked listings in last 24 hours
- recent wishlist saves
- preferred price range
- preferred neighborhoods
- device / locale / trip context

Freshness target: seconds to minutes.

### Offline Listing Features

Produced by Spark / Flink / batch pipelines and refreshed daily or hourly.

Examples:

- conversion rate
- click-through rate
- booking rate
- host response rate
- quality score
- price competitiveness
- listing embedding

Freshness target: minutes to hours, depending on feature.

### Reconciliation

At request time:

```text
request context + online user features + offline listing features -> ranking feature vector
```

If a feature is missing:

- New user: fall back to popularity and location-level priors.
- New listing: use default listing features plus a small exploration boost.
- Feature store timeout: fall back to non-personalized rank, do not fail the search.

---

## Caching

Cache only the reusable retrieval stage, not the personalized ranking stage.

```text
L0 cache key:
(geohash_prefix, date_bucket, guests_bucket, price_bucket, amenities_hash, query_hash)

TTL: ~60 seconds
Value: candidate listing IDs + retrieval metadata
```

Why not cache final ranking:

- Ranking is user-specific.
- User features change quickly.
- Experiments and ranking model versions can differ by user.

Hydrated listing cards can use a short TTL cache because card metadata is shared across users.

---

## Cold Start

### New User

No behavioral history yet.

Use:

- location popularity
- query intent
- global conversion rate
- price / quality balance
- device / locale
- trending listings

After 3+ interactions, switch to personalized ranking.

### New Listing

No click or booking history yet.

Use:

- host quality
- listing completeness
- price competitiveness
- neighborhood prior
- photo quality
- small exploration boost

The boost should be capped so new listings get exposure but do not dominate.

---

## Write Path and Freshness

Listing edits and availability updates flow through events:

```text
Listing Service / Booking Service
  |
  v
Outbox
  |
  v
Kafka
  |
  +--> Search Indexer
  +--> Availability Bitmap Builder
  +--> Feature Pipelines
```

Freshness expectations:

- Listing title / amenities edits: seconds of staleness.
- Availability changes: seconds of staleness in search, strongly checked at booking.
- Ranking features: minutes of staleness acceptable.
- Offline embeddings: daily / hourly refresh acceptable.

---

## Latency Budget

```text
API gateway + auth        20 ms
query parsing             20 ms
L0 retrieval/cache       100 ms
feature joins             80 ms
L1 rank                   30 ms
L2 rank                  100 ms
hydration                 80 ms
network / buffer          70 ms
--------------------------------
p95 target              <500-800 ms
```

Degradation plan:

- If L2 rank times out, return L1 ranking.
- If online feature store times out, use anonymous ranking.
- If L0 cache misses and index is slow, reduce fan-out or return partial city results.
- If hydration is slow, return essential fields first and lazy-load secondary badges.

---

## Deep Dives

### Deep Dive A: Availability Bitmap

For each listing, keep a bitset per month or per rolling year.

```text
OPEN = 1
NOT_OPEN = 0
```

For a 4-night stay:

```text
checkin=Jul 1, checkout=Jul 5
required bits = Jul 1, 2, 3, 4
```

This matches hotel semantics: checkout day does not need to be available for sleeping.

Updates are event-driven:

```text
reservation confirmed -> clear bits
reservation cancelled -> set bits if host did not block them
host blocks dates -> clear bits
host opens dates -> set bits if no reservation exists
```

The bitmap builder must consume events in order per listing or use version numbers to ignore stale updates.

### Deep Dive B: Two-Tower vs Deep Ranker

Two-tower:

- Great for fast candidate scoring.
- Listing embeddings are precomputed.
- User embedding can be computed quickly.
- Weak at cross-features.

Deep ranker:

- Better accuracy.
- Can use cross-features.
- More expensive.
- Only run on top candidates.

Senior answer: use both. Two-tower narrows candidates; deep ranker optimizes final order.

### Deep Dive C: Experimentation

Every response should log:

```text
request_id
user_id hash
query context
candidate listing IDs
L0/L1/L2 scores
features used
model version
experiment bucket
impressions / clicks / bookings
```

This supports offline training, debugging, and A/B test analysis.

---

## Failure Modes

| Failure | Handling |
| --- | --- |
| Search index stale | Accept for search; booking path rechecks |
| Feature store timeout | Fall back to non-personalized ranking |
| L2 model timeout | Return L1-ranked results |
| Availability bitmap lag | Show stale result, reject at checkout if unavailable |
| Hot city shard | Split geohash prefix, cache L0 candidates |
| New user/listing | Popularity priors + exploration boost |

---

## Interview Script

In the first 5 minutes:

```text
I would split this into a four-stage funnel:
1. filter and retrieve from a geosharded search index,
2. apply date-range availability using bitmaps,
3. run a fast L1 two-tower ranker over around 1000 candidates,
4. run a deeper L2 ranker over the top 100.

Search can be eventually consistent, but booking must re-check availability in the booking service.
```

Then deep dive into:

- availability bitmap correctness,
- feature store split between online user features and offline listing features,
- two-tower vs deep ranker trade-off,
- cold start and graceful degradation.

---

## Common Follow-Ups

### Why not cache final results?

Final ranking is personalized and depends on fast-changing user features and experiment buckets. Cache the L0 candidate set instead.

### How do you keep availability correct?

Search availability is best-effort and eventually consistent. The booking service owns correctness and atomically reserves dates before confirmation.

### What happens for a brand-new user?

Use query context, location popularity, global conversion rates, and price-quality balance until enough interactions exist.

### What happens for a brand-new listing?

Use host/listing quality priors and a capped exploration boost so the model can collect impressions and clicks.

