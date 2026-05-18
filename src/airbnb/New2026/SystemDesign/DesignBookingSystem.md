# Design a Booking System (Airbnb / Hotel) — 60-min Interview Version

A reservation service for short-term stays: search by date range, book a listing, never double-book, charge the guest, refund on cancellation, and notify both sides.

**Time budget**

| Minutes | What you do                              |
| ------- | ---------------------------------------- |
| 0–5     | Clarify requirements + capacity math     |
| 5–10    | API the client calls                     |
| 10–25   | Data model + high-level architecture     |
| 25–45   | Pick 2 deep dives                        |
| 45–55   | Scale + failure handling                 |
| 55–60   | Trade-offs and wrap-up                   |

---

## 0–5 min: Clarify

### Step 1 — Pin the hard invariant (this is the #1 score-getter)

> **No two confirmed reservations may overlap on the same listing.**

Everything else (search latency, recommendations, A/B tests) is incidental. The interviewer wants to hear how you prevent a double-book during a race, while also not blocking the booking flow on slow third-party payments.

### Step 2 — Functional requirements

- **Search** listings by `(location, check_in, check_out, guests)`.
- **Detail page** showing live availability + price for selected dates.
- **Book**: hold a listing, charge payment, confirm reservation.
- **Cancel** with policy-based refund (flexible / moderate / strict).
- **Host-side**: block dates, accept/decline requests (instant book vs request-to-book).
- **Notifications** to guest + host on every state transition.

Out of scope (call out): pricing engine, search ranking, messaging.

### Step 3 — Non-functional requirements

| Requirement              | Target                                   |
| ------------------------ | ---------------------------------------- |
| **No double-booking**    | 100%. Hard constraint.                   |
| Search p99               | < 300 ms                                 |
| Booking confirmation p99 | < 3 s (includes payment authorization)   |
| Availability consistency | Strong on the booking path; eventual on search index |
| Payment durability       | At-least-once charge attempt; idempotent |
| Multi-currency           | Yes; FX captured at book time            |

### Step 4 — Capacity math

```
~10 M listings, ~1 B users
Searches  ~10 K QPS peak  (read-heavy; 100:1 read:write)
Bookings  ~100 QPS peak avg, 1 K QPS during a flash sale
Avg listing has ~365 day-rows of availability
Reservations / yr ~500 M → ~10 KB row → 5 TB / yr
```

> **Take-away:** small write volume, huge read volume, **strict correctness on the write path**. The architecture splits cleanly into a search read tier (eventually consistent) and a booking write tier (strongly consistent).

---

## 5–10 min: API the Client Calls

### Search

```http
GET /v1/search?location=oakland&checkin=2026-06-10&checkout=2026-06-13&guests=2
→ { "results": [{ "listing_id", "price", "rating", "thumb_url" }, ...], "cursor": ... }
```

### Quote (hold a price, optional)

```http
POST /v1/quotes
{ "listing_id": ..., "checkin": ..., "checkout": ..., "guests": 2 }
→ { "quote_id": "q_…", "total_cents": 48000, "expires_at": "+10 min" }
```

A `quote_id` lets the client checkout flow render a stable price and tax breakdown without recomputing on every step.

### Reserve (the two-phase booking)

```http
POST /v1/reservations
Headers: Idempotency-Key: <client-uuid>
{
  "quote_id": "q_...",
  "payment_method_id": "pm_...",
  "guest_id": "u_..."
}
→ 201 { "reservation_id": "r_...", "status": "PENDING_PAYMENT" }
```

The server immediately:

1. Atomically reserves the date range (see Deep Dive A) → `HELD` state.
2. Returns 201 with `PENDING_PAYMENT`.
3. Asynchronously charges the payment processor.
4. On success → `CONFIRMED`. On failure → `RELEASED` (dates freed).

### Status / Cancel

```http
GET /v1/reservations/{id}
POST /v1/reservations/{id}/cancel
```

### Host availability

```http
PUT /v1/listings/{id}/availability
{ "blocked": [{"from": "2026-07-01", "to": "2026-07-10"}], "open": [...] }
```

---

## 10–25 min: Data Model + Architecture

### Tables (logical)

```
listings
├── listing_id (PK), host_id, location, capacity, ...
├── pricing_rules     // base price, weekend uplift, length-of-stay discount

listing_availability         // ONE ROW PER DAY per listing
├── listing_id (PK)
├── date       (PK)
├── status     // OPEN / HELD / BOOKED / BLOCKED
├── reservation_id  // NULL unless HELD or BOOKED
├── version    // optimistic-lock counter
PRIMARY KEY (listing_id, date)

reservations
├── reservation_id (PK)
├── listing_id, guest_id, checkin, checkout, guests
├── status     // PENDING_PAYMENT / CONFIRMED / CANCELLED / REFUNDED / FAILED
├── price_cents, currency, fx_rate
├── payment_intent_id   // pointer into payments service
├── idempotency_key (UNIQUE)
├── created_at, confirmed_at, cancelled_at

quotes
├── quote_id, listing_id, checkin, checkout, total_cents, expires_at

payments         // could be a separate service entirely
├── payment_id (PK), reservation_id (FK), status, charge_amount, refunds
```

> The `listing_availability` table being **one row per (listing, date)** is the secret to clean concurrency: a date range lock is a transactional update of N rows, atomic by definition.

### Architecture

```
                 ┌──────────────┐
client ──────────► API Gateway  │
                 └──────┬───────┘
        ┌───────────────┼───────────────┐
        ▼               ▼               ▼
   Search Svc     Booking Svc     Host/Listing Svc
        │               │               │
        ▼               ▼               ▼
   ┌─────────┐    ┌────────────────────────────┐
   │ ES /    │    │ Sharded SQL (by listing_id)│   ← reservations, availability
   │ Solr    │    │ + read replicas            │
   └─────────┘    └──────────────┬─────────────┘
        ▲                        │
        │                        ▼
        │                  Outbox table  ──► Kafka ──┐
        │                                            ├─► Search Indexer (eventual)
        │◄───────────────────────────────────────────┤
        │                                            ├─► Notification Svc
        │                                            ├─► Payment Worker
        │                                            └─► Analytics
```

| Component         | Role                                                              |
| ----------------- | ----------------------------------------------------------------- |
| Search Svc + ES   | Read-only index; refreshed via Kafka events; eventual consistency |
| Booking Svc       | Owns reservations + availability; strong consistency              |
| Sharded SQL       | Source of truth; one shard per listing_id range                   |
| Outbox + Kafka    | Decouples writes from downstream effects                          |
| Payment Worker    | Calls Stripe / Braintree; idempotent retries                      |
| Notification Svc  | See `DesignNotificationSystem.md`                                 |

### The Reserve Flow (the heart of the system)

```
T0  Client POSTs /v1/reservations with idempotency key
T0  Booking Svc:
       BEGIN
         INSERT reservations(...) ON CONFLICT(idempotency_key) DO NOTHING RETURNING *
         -- if no row, return the existing reservation (idempotent replay)

         UPDATE listing_availability
            SET status='HELD', reservation_id=$rid, version=version+1
          WHERE listing_id=$L AND date BETWEEN $ci AND $co-1
            AND status='OPEN'
        -- expect rows_affected == nights; else ROLLBACK and 409
       COMMIT
       Emit `reservation_created` to outbox

T0  Return 201 PENDING_PAYMENT to client

(async, separate Payment Worker)
T1  Read outbox → call Stripe with idempotency_key = reservation_id
T2  On success: UPDATE reservations SET status='CONFIRMED'
                UPDATE listing_availability SET status='BOOKED'
                Emit `reservation_confirmed`
    On failure (declined or timeout): release dates → status='FAILED'
```

Two key things this gives us:
- The **availability lock is held in SQL** (durable, transactional) for the few seconds the payment takes — no Redis lock that can vanish.
- The **payment is decoupled from the user request** — the API returns in ~50 ms; the user sees "your booking is being confirmed" and gets a push notification when Stripe replies.

---

## 25–45 min: Deep Dives (pick 2)

### Deep Dive A: Preventing Double-Booking

Three valid strategies — name all three, pick one, justify.

#### 1. SQL row-level lock (the one I'd ship)

```sql
BEGIN;
SELECT date FROM listing_availability
 WHERE listing_id = $L AND date BETWEEN $ci AND $co-1
   FOR UPDATE;        -- locks the rows; concurrent txns block

-- verify all are OPEN; if not, ABORT and return 409

UPDATE listing_availability
   SET status='HELD', reservation_id=$rid
 WHERE listing_id = $L AND date BETWEEN $ci AND $co-1;
COMMIT;
```

- **Pro:** simplest, durable, no separate lock service. Constraint is the schema.
- **Con:** transaction holds locks for the duration of the txn — must keep it short (`SELECT FOR UPDATE` → `UPDATE` → `COMMIT`, no network calls in between). **Payment happens OUTSIDE this txn.**

#### 2. Optimistic concurrency with version

```sql
UPDATE listing_availability
   SET status='HELD', reservation_id=$rid, version=version+1
 WHERE listing_id=$L AND date BETWEEN $ci AND $co-1
   AND status='OPEN'
   AND version IN ($prev_versions)
```

Rows-affected must equal nights, else retry.

- **Pro:** no row locks; high concurrency on the same listing.
- **Con:** retry storms on contested popular dates. Best when contention is rare.

#### 3. Distributed lock (Redis / ZooKeeper) — DO NOT use as sole defense

A Redis `SETNX listing:L:checkin:checkout` lock with TTL feels fast, but Redis can lose the lock under partition / restart. Use it only as an **early-rejection** in front of the SQL transaction, never as the source of truth.

> **Rule:** The DB unique-index / row lock is the source of truth. Redis can be the fast-path optimizer.

#### Why one-row-per-day is non-negotiable

If you store availability as `(start, end)` ranges, you have to use range exclusion constraints (Postgres `EXCLUDE USING gist`) or hand-rolled range merging. One row per day turns range conflicts into trivial row-level locks, joinable with any SQL, and easy to reason about.

#### Hold expiry

`HELD` rows that don't transition to `BOOKED` within ~5 min must auto-release. Two ways:

- **TTL column** + reaper job: `WHERE status='HELD' AND held_until < NOW()` resets to OPEN.
- **Outbox event** + delayed Kafka topic / scheduled job.

Pick the reaper — same belt-and-suspenders as a job-scheduler lease. See `DesignJobScheduler.md`.

### Deep Dive B: Payment Integration (Transactional Outbox)

#### The wrong way

```python
# inside the booking transaction:
db.update_availability(...)
stripe.charge(...)            # ← network call in a DB transaction
db.commit()
```

Stripe takes ~1 s. You're holding row locks for 1 s. Concurrent bookers on the same listing all queue. And if Stripe times out, you must decide: did the charge succeed? Did the DB commit?

#### The right way: Transactional Outbox

```
BEGIN
  INSERT reservation (status='PENDING_PAYMENT')
  UPDATE availability ('HELD')
  INSERT outbox (topic='charge_reservation', payload={rid, amount, ...})
COMMIT
```

A separate **Outbox Relay** polls the `outbox` table and publishes to Kafka. A Payment Worker consumes Kafka and calls Stripe with `Idempotency-Key = reservation_id` so retries can't double-charge.

```
worker:
  msg = kafka.poll()
  charge = stripe.create_charge(
      amount=msg.amount,
      idempotency_key=msg.reservation_id   ← key insight
  )
  if charge.status == 'succeeded':
      db.confirm(msg.reservation_id)
  elif charge.declined:
      db.release(msg.reservation_id)
  else:
      raise  # let Kafka redeliver
  kafka.commit(msg)
```

#### Why idempotency keys on Stripe matter

The Payment Worker can crash between `stripe.create_charge()` and `kafka.commit()`. On redelivery, the same `Idempotency-Key` tells Stripe "I already authorized this charge; return the existing result." Without it, the same reservation gets charged twice.

This is the same pattern as `(job_id, scheduled_run_at)` for the job scheduler — push dedup into the downstream system's primary key.

#### Refund flow

Cancellation goes through the same outbox: `INSERT outbox(topic='refund', ...)`. The worker calls Stripe's refund API, also idempotent on `reservation_id`. Refund amount is computed from the cancellation policy at refund time.

### Deep Dive C: Search Index (eventual consistency on reads)

The search tier is **not** the source of truth — it can lag by seconds and that's fine.

| Concern                     | Approach                                                                    |
| --------------------------- | --------------------------------------------------------------------------- |
| Indexing                    | Listings + availability windows shoveled into ES via Kafka consumer         |
| Date-range filter           | Index a denormalized `available_dates` field, or pre-compute "available next N days" buckets |
| Geo + filter combo          | ES with geohash + facets; cache hot location queries in Redis               |
| Stale availability          | Final availability check happens at quote/reserve time against SQL          |
| Ranking                     | Out of scope; mention it's a separate ML service                            |

> **Critical pattern:** **search may show a listing that's actually booked**, but the **reserve transaction is the gate**. Showing a stale listing is a UX paper-cut; double-booking is a correctness bug.

### Deep Dive D: Instant Book vs Request-to-Book

| Mode               | Flow                                                            | Reservation states                       |
| ------------------ | --------------------------------------------------------------- | ---------------------------------------- |
| Instant Book       | Guest → HELD → CONFIRMED (payment captures)                     | normal                                   |
| Request to Book    | Guest → PENDING_HOST_APPROVAL (dates HELD, payment authorized but not captured) → host accepts → CONFIRMED + capture; host declines or 24h timeout → released + auth voided | extra state |

The dates are **held during the approval window**. Otherwise a popular listing's calendar is meaningless. Payment is authorized but not captured (Stripe `capture: false`); on accept, we capture; on decline or timeout, we void the auth so the guest's card isn't held forever.

---

## 45–55 min: Scale + Failure Handling

### Sharding

- **Listings + availability + reservations** sharded by `listing_id`. All booking operations on one listing hit one shard → row locks stay local.
- **Search index** sharded by geohash → location queries are partitioned naturally.
- **Payments** are typically a separate service with its own DB, called via API.

### Caching

| Layer       | What                                                          | TTL                    |
| ----------- | ------------------------------------------------------------- | ---------------------- |
| Redis       | Listing detail page (price, photos, host info)                | 5 min, invalidate on update |
| Redis       | "Listing L available on date D?" hot-path (for popular dates) | 30 s, write-through on book |
| CDN         | Listing photos, static page assets                            | days                   |

The hot-path availability cache must **never** be the booking gate — it's an optimization for search/detail pages only.

### Failure modes

| Failure                        | What we do                                                    |
| ------------------------------ | ------------------------------------------------------------- |
| Stripe down                    | Outbox retries with exp backoff; reservation stuck in PENDING_PAYMENT; reaper releases after N hours |
| SQL shard down                 | Bookings on that shard return 503; search keeps working from ES |
| Outbox relay falls behind      | Backpressure on bookings? No — accept the lag; emit metric    |
| Kafka unavailable              | Outbox table is durable; relay catches up when Kafka recovers |
| Host blocks a date that has a confirmed reservation | Reject at the API layer; reservation always wins |
| Double-fault: charge succeeded but DB write failed  | Outbox row marks "needs reconciliation"; daily job compares Stripe ledger vs reservations |

### Reconciliation job (nightly)

```
foreach payment in stripe.payments(yesterday):
  rid = payment.metadata.reservation_id
  if reservations[rid].status != 'CONFIRMED':
      alert("orphan charge: " + payment.id)
```

> No payment system is correct without a reconciliation job. Mention it.

---

## 55–60 min: Trade-offs / Common Mistakes

| Mistake                                                                | Effect                                   | Fix                                                |
| ---------------------------------------------------------------------- | ---------------------------------------- | -------------------------------------------------- |
| Storing availability as `(start_date, end_date)` ranges                | Range-conflict logic complex; deadlocks  | One row per (listing, date)                        |
| Calling Stripe inside the booking transaction                          | Locks held during slow network call      | Transactional outbox; payment is async             |
| Redis lock as sole double-book defense                                 | Lock loss → double booking               | SQL row lock or unique constraint = source of truth |
| Using search index (ES) as availability source                         | Stale data → double-book                 | Search is hints; SQL is the gate                   |
| No idempotency key on Stripe                                           | Retry → duplicate charge                 | `Idempotency-Key = reservation_id`                 |
| Forgetting to release HELD rows on payment failure                     | Listing stays unavailable; angry users   | Reaper + auto-release after timeout                |
| Request-to-book without payment auth                                   | Guest's card may decline after host accepts | Auth at request time; capture on accept         |
| No reconciliation between payment processor and reservations           | Silent ledger drift                      | Nightly reconciliation job + alerts                |
| Synchronous notifications inside the booking txn                       | Email outage breaks bookings             | Outbox → Notification Svc consumes async           |
| Quotes that don't expire                                               | Guest books at yesterday's price weeks later | TTL on quotes; re-quote at reserve time         |

### Key Concepts for the Interview

| Topic                                | What to say                                                                            |
| ------------------------------------ | -------------------------------------------------------------------------------------- |
| One row per (listing, date)          | Turns range conflicts into row-level locks; trivially correct                          |
| Two-phase booking                    | HELD (sync, fast) → CONFIRMED (async, payment); user gets fast 201                     |
| Transactional outbox                 | DB write + outbox row in one txn; relay publishes to Kafka                             |
| Idempotency keys end-to-end          | Client → Booking Svc → Stripe. Same key replays cleanly at every layer                 |
| Source of truth vs cache             | SQL is the booking gate; ES/Redis are read optimizations                               |
| Hold expiry / reaper                 | Same lease pattern as job scheduler — never trust a "lock" without a TTL               |
| Reconciliation                       | Nightly Stripe ledger vs reservations; the only way to catch silent ledger drift       |
| Eventual consistency on search       | Listing in search but actually booked = paper-cut; booking the same listing twice = bug |

### Wrap-Up

| Aspect                          | Solution                                                |
| ------------------------------- | ------------------------------------------------------- |
| No double-booking               | SQL row lock or optimistic version on per-day rows      |
| Slow payment in the hot path    | Two-phase: HELD synchronously, CONFIRMED via outbox     |
| Idempotency end-to-end          | Client idempotency key → Stripe idempotency key         |
| Search vs source of truth       | ES for search, SQL for booking gate                     |
| Hold leaks                      | TTL + reaper job                                        |
| Payment correctness             | Transactional outbox + idempotency + nightly reconcile  |
| Request-to-book                 | Auth at request, capture on accept, void on decline     |
| Scale                           | Shard by listing_id; geo-shard search index             |
