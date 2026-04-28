# Design a Quota System

> snowflake · anonymous user · 2mon

Design a system to track and limit resource usage across many services. Think about Google's storage: you have 100 GB shared between Drive, Photos, and Gmail. Every time you upload a file, the system must check if you have enough space.

The main challenge is not just saving data (CRUD). It is **strict enforcement** when many things happen at once (high concurrency). We must never let a user go over their limit, but the system must still be fast.

---

## Phase 1: What We Need to Build

### Functional Requirements

- **Use Quota**: Services (like Drive) can ask to use space for a user.
- **Free Up Quota**: Services can give space back when a user deletes a file.
- **Cancel Requests**: Services can cancel a request if an upload fails.
- **Check Usage**: Users can see how much space they have used.

Assume there is one global limit per user. We will not discuss different payment plans or admin tools right now.

### Non-Functional Requirements

| Requirement         | Target          | Why?                                                                                              |
| ------------------- | --------------- | ------------------------------------------------------------------------------------------------- |
| Strict consistency  | No overdraft ever | If a user has 2 GB left, two simultaneous 2 GB uploads must not both happen.                    |
| Low latency (P99)   | < 50ms          | Every upload waits for this check. It must be fast.                                               |
| High availability   | 99.99%          | If this system goes down, no one can upload files anywhere.                                       |
| High throughput     | 10K–100K+ QPS   | We must handle write requests from all services combined.                                         |

The "no overdraft" rule makes this hard. We cannot use "eventual consistency." We need to handle concurrency very carefully.

### Capacity Math

| Metric              | Value         |
| ------------------- | ------------- |
| Daily Active Users  | 10 million    |
| Actions per user/day | 20 (uploads, deletes, edits) |
| Peak QPS            | 23,000+ QPS   |
| Data size           | ~100 bytes per user |
| Total storage       | 1 GB          |

The data is very small (1 GB fits in RAM). This is a **compute-heavy** problem (lots of requests), not a storage-heavy problem. We use **Sharding** to spread out the CPU work, not to store more data.

---

## Phase 2: Database Schema

### Core Tables

```text
UserQuota
├── user_id (PK)
├── quota_limit          // e.g., 100 GB
├── used                 // confirmed usage
├── reserved             // locked but not confirmed yet
├── updated_at

Reservation
├── reservation_id (PK)
├── user_id (FK)
├── service_id           // e.g., drive, photos
├── idempotency_key      // prevents double-charging for retries
├── amount               // how much space is reserved
├── status               // pending | confirmed | released | expired
├── created_at
├── expires_at           // when to auto-delete this reservation
```

### The Strategy: Reserve → Confirm

We separate **reserved** space from **used** space. Uploading a file takes time.

1. **Reserve**: Drive asks to hold 5 GB. We add 5 GB to `reserved`.
2. **Upload**: Drive uploads the file.
3. **Confirm**: Drive tells us the upload finished. We move 5 GB from `reserved` to `used`.

```text
Available quota = quota_limit - used - reserved
```

If you only track `used`, two uploads could see "10 GB available" at the same time and both start. This causes an overdraft. The `reserved` field prevents this.

---

## Phase 3: How Services Talk to Us (API)

### Reserve Quota (Must be Fast and Atomic)

```http
POST /v1/quota/reserve
Headers:
  X-Service-Id: google-drive
  Idempotency-Key: upload_abc123

Request:
{
  "user_id": "user_456",
  "amount_bytes": 5368709120   // 5 GB
}

Response (200 OK):
{
  "reservation_id": "res_789",
  "available_after": 53687091200,
  "expires_at": "2024-01-15T12:30:00Z"
}

Response (409 Conflict):
{
  "error": "INSUFFICIENT_QUOTA",
  "available": 2147483648,  // only 2 GB left
  "requested": 5368709120
}
```

### Confirm Reservation

```http
POST /v1/quota/confirm
{ "reservation_id": "res_789" }
```

### Cancel Reservation (If Upload Fails)

```http
POST /v1/quota/cancel
{ "reservation_id": "res_789" }
```

### Free Up Space (Delete File)

```http
POST /v1/quota/release
{
  "user_id": "user_456",
  "amount_bytes": 5368709120,
  "service_id": "google-drive",
  "reference_id": "file_xyz"
}
```

### Check Usage

```http
GET /v1/quota/usage?user_id=user_456

Response:
{
  "user_id": "user_456",
  "quota_limit": 107374182400,   // 100 GB
  "used": 53687091200,            // 50 GB used
  "reserved": 5368709120,         // 5 GB waiting
  "available": 48318382080        // ~45 GB free
}
```

The `reserve` call must be **synchronous** (the client waits for the answer). The upload cannot start until we say "Yes."

---

## Phase 4: How It Works

### Architecture Overview

#### Who Does What?

| Part                | Role                                                                          |
| ------------------- | ----------------------------------------------------------------------------- |
| Upstream Services   | Drive, Photos, etc. ask for permission before using space.                    |
| Load Balancer       | Distributes traffic. It can send all requests for `user_123` to the same server. |
| Quota Servers       | Simple servers that run the logic. They don't store data.                     |
| PostgreSQL          | Stores the data. Sharded by `user_id`.                                        |
| Expiration Worker   | Background job that cleans up old reservations.                               |

### The Reserve Flow (Most Important)

This step must be **atomic** (all or nothing) to stop overdrafts.

```sql
BEGIN;

-- Check for duplicate requests (Idempotency)
SELECT reservation_id, status, amount
FROM reservations
WHERE service_id = $service_id
  AND idempotency_key = $idempotency_key
FOR UPDATE;
-- If found, return the existing result.

WITH reserved_row AS (
  UPDATE user_quota
  SET reserved = reserved + $amount,
      updated_at = NOW()
  WHERE user_id = $user_id
    AND (quota_limit - used - reserved) >= $amount
  RETURNING user_id
)
INSERT INTO reservations (
  reservation_id, user_id, service_id, idempotency_key, amount, status, created_at, expires_at
)
SELECT $reservation_id, user_id, $service_id, $idempotency_key, $amount, 'pending', NOW(), NOW() + INTERVAL '30 minutes'
FROM reserved_row;

COMMIT;
```

**Step-by-Step:**

1. Drive asks to reserve 5 GB.
2. The database starts a transaction. It checks if we already processed this request ID (idempotency).
3. It tries to `UPDATE` the quota **IF** there is enough space.
4. If the `UPDATE` works, it inserts a new reservation row.
5. If the `UPDATE` fails (not enough space), nothing is inserted. We return an error.
6. The transaction ends.

This guarantees that we never reserve more space than exists.

### The Confirm Flow

```sql
BEGIN;

WITH target AS (
  SELECT reservation_id, user_id, amount, status
  FROM reservations
  WHERE reservation_id = $reservation_id
  FOR UPDATE
)
UPDATE user_quota q
SET used = q.used + t.amount,
    reserved = q.reserved - t.amount,
    updated_at = NOW()
FROM target t
WHERE q.user_id = t.user_id
  AND t.status = 'pending';

UPDATE reservations
SET status = 'confirmed'
WHERE reservation_id = $reservation_id
  AND status = 'pending';
COMMIT;
```

This moves the bytes from `reserved` to `used` and marks the reservation as complete.

### The Cancel Flow (Failed Upload)

```sql
BEGIN;
WITH target AS (
  UPDATE reservations
  SET status = 'released'
  WHERE reservation_id = $reservation_id
    AND status = 'pending'
  RETURNING user_id, amount
)
UPDATE user_quota q
SET reserved = q.reserved - t.amount,
    updated_at = NOW()
FROM target t
WHERE q.user_id = t.user_id;
COMMIT;
```

### Cleaning Up (Expiration Worker)

If Drive crashes during an upload, it never calls Confirm or Cancel. The quota stays "reserved" forever. We need a background worker to fix this:

```sql
-- Worker loop: find old 'pending' reservations and cancel them
WITH expired AS (
  UPDATE reservations
  SET status = 'expired'
  WHERE reservation_id IN (
    SELECT reservation_id
    FROM reservations
    WHERE status = 'pending'
      AND expires_at < NOW()
    ORDER BY expires_at
    LIMIT 1000
    FOR UPDATE SKIP LOCKED
  )
  RETURNING user_id, amount
)
UPDATE user_quota q
SET reserved = q.reserved - e.amount,
    updated_at = NOW()
FROM expired e
WHERE q.user_id = e.user_id;
```

We set a **TTL (Time To Live)**, usually 30 minutes. If a reservation is older than that, we assume the upload failed and give the space back.

---

## Phase 5: Handling Heavy Traffic

### 1. Handling Many Requests (Sharding)

A single database cannot handle tens of thousands of updates per second.

**Solution: Shard by User ID.** We split the database into pieces (shards) based on `user_id`.

- User A goes to Shard 1.
- User B goes to Shard 2.

Because a specific user's data is always on one shard, the SQL transaction works perfectly.

### 2. Why No Cache? (Redis)

You might want to put Redis in front to make it faster. **Don't do it.**

| Approach           | Problem                                                                                           |
| ------------------ | ------------------------------------------------------------------------------------------------- |
| Check Cache First  | Two servers see "10 GB free" in the cache. Both allow an upload. Overdraft happens.               |
| Write-Through      | If the cache updates but the DB fails (or vice versa), the numbers don't match.                   |

For strict limits, the **database MUST be the source of truth**. We only use a cache for `GET /usage` requests (reading), not for reserving space.

### 3. Replication

We need **Strict Consistency**.

- **Writes (Reserve)**: Use **Synchronous** Replication. The primary DB waits for at least one copy to confirm the write. This is safer but slightly slower.
- **Reads (Check Usage)**: Read from any copy (Async). It's okay if the user sees a number that is 1 second old.

### 4. Keeping Reservations Alive

What if a user is uploading a 50 GB movie? It takes longer than 30 minutes.

- **Heartbeats**: The uploading service (Drive) sends a "heartbeat" every 5 minutes to extend the reservation time.
- If the heartbeat stops, the worker deletes the reservation.

### 5. Scaling Levels

| Scale         | Architecture                              |
| ------------- | ----------------------------------------- |
| < 10K QPS     | One PostgreSQL DB.                        |
| 10K–100K QPS  | Sharded PostgreSQL (10–50 shards).        |
| 100K–1M QPS   | Many Shards + Read Replicas.              |
| > 1M QPS      | Local Budgets (See below).                |

### 6. Local Budgets (Extreme Scale)

If we have millions of requests per second, the central DB is too slow. We give each service a "budget."

- "Drive, you get 1 GB for User A."
- "Photos, you get 1 GB for User A."

Drive can spend that 1 GB locally without calling the central DB. It only calls home when it runs out.

**Trade-off**: It's much faster, but less accurate. A user might see 2 GB available in Drive but 0 GB in Photos for a short time.

---

## Common Mistakes

- **Read-then-Write**: Reading available space in one query, then updating in a second query. This creates a "race condition" where two requests slip through. Always use one atomic `UPDATE`.
- **Caching Logic**: Using Redis to check limits. This causes stale data and overdrafts.
- **Forgetting Cleanup**: Not having an expiration worker. Crashed uploads will eat up all the space permanently.
- **Wrong Sharding**: Sharding by `service_id`. This is bad because one user's data is spread out, making it hard to check their total limit. Always shard by `user_id`.

---

## Key Concepts for the Interview

| Topic                | What to Say                                                       |
| -------------------- | ----------------------------------------------------------------- |
| Strict Enforcement   | Use Conditional `UPDATE` inside a transaction.                    |
| Process              | Reserve → Confirm (or Cancel).                                    |
| Cleanup              | Use a background worker to remove old, stuck reservations.        |
| Caching              | Do not cache writes. The DB is the boss.                          |
| Sharding             | Split data by `user_id` to handle high traffic.                   |
| Replication          | Sync writes for safety, Async reads for speed.                    |
| Math                 | Data size is small (1 GB). This is a speed problem, not a size problem. |

---

## Wrap-Up

| Aspect           | Solution                | Why?                                                       |
| ---------------- | ----------------------- | ---------------------------------------------------------- |
| Core Idea        | UserQuota + Reservations | Separates "used" space from "pending" uploads.            |
| Safety           | Atomic SQL Updates       | Prevents race conditions.                                  |
| Stuck Uploads    | TTL (Time To Live)       | Cleans up if a service crashes.                            |
| High Traffic     | Shard by `user_id`       | Spreads the work across many servers.                      |
| Consistency      | Strong for Writes        | Ensures we never give away free space.                     |
| Extreme Scale    | Local Budgets            | Sacrifices strict accuracy for massive speed.              |
