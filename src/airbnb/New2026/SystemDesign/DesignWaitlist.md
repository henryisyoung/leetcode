# Design a Waitlist Service — 60-min Interview Version

A user wants to book a listing for specific dates, but it's fully booked. They join the **waitlist**. When the slot opens (cancellation, dates unblocked), everyone on the waitlist gets notified. **First to click wins** — the booking service (which already exists) is the authority on who actually gets the room.

The interview prompt was deliberately simple: API + schema + scalability + tradeoffs, with explicit "drive the conversation" expectations. Notification is plain fan-out — no per-position priority, no "notify only the first N", no fancy delivery scheduling.

**Time budget**

| Minutes | What you do                                              |
| ------- | -------------------------------------------------------- |
| 0–5     | Clarify scope + capacity; **explicitly name what's OUT** |
| 5–10    | API + DB schema                                          |
| 10–25   | Architecture + the slot-open flow                        |
| 25–45   | 2 deep dives (fan-out, race condition)                   |
| 45–55   | Scale + failure handling                                 |
| 55–60   | Trade-offs and wrap-up                                   |

---

## 0–5 min: Clarify (drive the conversation)

This was the lesson from the actual interview: **you have to propose the scope yourself**. Don't wait for the interviewer to feed it to you.

### Open with three clarifying questions, then answer them yourself

> *"A few things I want to pin down before I draw anything. I'll propose my read; tell me if you'd narrow or widen any of them."*

| Question                                                                     | My proposal                                                                                 |
| ---------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| **What triggers a slot opening?**                                            | Booking-service emits `listing.slot_opened{listing_id, date_range}` events. We consume.     |
| **Per-position or broadcast notify?**                                        | **Broadcast.** Notify everyone matching the freed slot. First to click wins via Booking Svc.|
| **Is the waitlist responsible for the booking too?**                         | **No.** We surface the opportunity; Booking Service is the source of truth on who gets it.  |
| **What defines a "match" between a waitlist entry and a freed slot?**        | Date-range overlap on the same listing. Strict: entry's dates must be contained in freed range. (Or relax to "overlap" — I'd ask which.) |
| **Self-serve leave + auto-expire?**                                          | Both. User can leave anytime; entry expires after the requested check-in date.              |

This pre-empts the "what about X?" derail and shows you're driving.

### Step 1 — Functional requirements

- **Join**: user adds (listing, check-in, check-out, guests) to their waitlist.
- **Leave**: user removes their entry; admin removes spam entries.
- **List**: user sees their active waitlist; host sees who's waiting on their listing.
- **Notify on slot open**: when a date range becomes available, push to every entry whose dates fit.
- **Auto-expire**: entries past check-in are stale; auto-prune.

**Explicitly out of scope** (say this out loud — clarity scores):
- The actual booking transaction (Booking Service).
- Search / ranking / pricing.
- Per-position priority or "VIP-first" notifications.

### Step 2 — Non-functional requirements

| Requirement                  | Target                                              |
| ---------------------------- | --------------------------------------------------- |
| Join / leave latency         | < 200 ms p99                                        |
| Notification fan-out         | < 60 s from slot-open event to last delivery        |
| Durability                   | Entries survive process restart                     |
| Read consistency             | Eventual on "host sees waitlist" is fine            |
| Idempotent join              | Re-clicking "join" doesn't create duplicates        |
| Fairness                     | Best-effort broadcast — first-click wins (not FIFO) |

### Step 3 — Capacity math

```
~10 M listings, ~1 B users
Avg listing on waitlist by ~10 users for ~5 popular date windows  → ~500 M active entries
Hot listing (Bali villa, Christmas)                                ~10 K – 100 K entries
Joins / leaves                                                     ~1 K writes/sec avg
Slot-open events                                                    ~10/sec avg, bursty
Notifications / sec on a hot cancellation                          up to 100 K bursts
```

> **Take-away:** the workload is light most of the time. The two stress points are (1) a **hot listing with 100K entries** generating a fan-out burst, and (2) **race resolution** when 100K people click "book now" simultaneously. Everything in the design serves these two.

---

## 5–10 min: API + DB Schema

### API (REST; clients also receive push via existing Notification Service)

```http
POST /v1/waitlists
Headers: Idempotency-Key: <client-uuid>
{
  "listing_id": "L_42",
  "checkin":    "2026-12-24",
  "checkout":   "2026-12-31",
  "guests":     2,
  "notify_channels": ["push", "email"]      // optional, defaults to user prefs
}
→ 201 { "entry_id": "w_…", "position_estimate": 47 }

DELETE /v1/waitlists/{entry_id}
GET    /v1/waitlists/me                     // user's active entries
GET    /v1/listings/{id}/waitlist/summary    // host view: count + dates histogram
```

Two design choices to call out:

1. **`Idempotency-Key`** — client retry must not create two entries. DB unique constraint enforces it.
2. **`position_estimate`, not `position`** — exact position is misleading. Two users may "match" different slot widths, and broadcast notification means position doesn't determine outcome. Calling it "estimate" sets the right expectation.

### DB Schema

```
listings                  -- owned by Listing Service; we only need ids
waitlist_entries
├── entry_id (PK)         -- UUID
├── user_id               -- INDEXED for "my waitlist"
├── listing_id            -- INDEXED (composite with checkin) for slot matching
├── checkin_date          -- inclusive
├── checkout_date         -- exclusive
├── guests
├── joined_at             -- audit / tiebreaker if we ever want FIFO
├── status                -- ACTIVE | NOTIFIED | CONVERTED | LEFT | EXPIRED
├── notified_at           -- last time we pushed for this entry
├── notify_channels       -- JSON
├── idempotency_key       -- UNIQUE
└── version               -- optimistic concurrency on status transitions

INDEXES:
  (user_id, status) WHERE status='ACTIVE'           -- "my waitlists"
  (listing_id, checkin_date, checkout_date) WHERE status='ACTIVE'   -- slot match
  (status, checkin_date) WHERE status='ACTIVE'      -- auto-expire scan
```

#### Why SQL (Postgres / MySQL), not NoSQL?

> *Even though the obvious choice is "use the same store as bookings," I want to say why:*

- **Range query on dates** is first-class in SQL (`WHERE checkin_date <= $x AND checkout_date >= $y`). DynamoDB / Cassandra need either Global Secondary Indexes you'd hand-maintain, or full scans.
- **Idempotency via UNIQUE constraint** is a free, transactional primitive in SQL.
- **Operational familiarity** — same Postgres ops team, backups, replication that booking already uses.
- **Volume is modest** — 500 M rows, well within a sharded Postgres footprint.

Choose Postgres sharded by `listing_id`. (Same shard topology as the booking service → joins are cheap if we ever need them.)

---

## 10–25 min: Architecture + The Slot-Open Flow

### Components

```
   client ──► Waitlist API ──► Waitlist DB (Postgres, sharded by listing_id)
                                      ▲
                                      │ writes via Booking Svc emitter
                                      │
   Booking Svc ──► Kafka(`listing.slot_opened`) ──► Matcher Worker ──► Notification Svc
                                                          │
                                                          └─ scans waitlist_entries
                                                             matching the freed slot
                                                          └─ updates entries → NOTIFIED
                                                          └─ emits broadcast notification

   Expiry Worker ──► daily sweep: ACTIVE entries past checkin → EXPIRED
```

| Component         | Role                                                                  |
| ----------------- | --------------------------------------------------------------------- |
| Waitlist API      | Stateless REST. Idempotent join, leave, list.                         |
| Waitlist DB       | Postgres sharded by `listing_id`. Source of truth for entries.        |
| Matcher Worker    | Consumes `slot_opened` events. Finds matching entries. Triggers notify.|
| Notification Svc  | The existing system. We send it one fan-out request per slot, not per user. |
| Expiry Worker     | Sweeps stale entries off the hot table.                               |

### The Slot-Open Flow (the heart of the design)

```
1. A booking is cancelled (or dates unblocked).
2. Booking Svc emits to Kafka:
     { listing_id: "L_42", freed: {from: "2026-12-24", to: "2026-12-31"} }
3. Matcher Worker (consumer-grouped, partitioned by listing_id) picks it up:
     a. SELECT entry_id, user_id, notify_channels
        FROM waitlist_entries
        WHERE listing_id = 'L_42' AND status = 'ACTIVE'
          AND checkin_date >= '2026-12-24' AND checkout_date <= '2026-12-31'
        LIMIT 10000;        -- safety cap per pass; loop if needed
     b. UPDATE the matched entries: status = 'NOTIFIED', notified_at = NOW()
     c. Emit ONE batched notification to Notification Svc:
          POST /v1/notifications/batch
          { user_ids: [...], template: 'waitlist_slot_open',
            data: { listing_id, freed_range, deep_link } }
4. Notification Service handles the rest:
     - Per-user pref / quiet hours / channel routing.
     - Per-provider rate limits (APNs, FCM, email).
     - Retries, suppressions, in-app inbox.
```

### Why "one batched call to Notification Svc," not N individual calls?

- **Backpressure compatibility**: Notification Svc owns per-provider rate limits. If we send 100K POSTs, we hammer it. If we send one batch of 100K user_ids, it absorbs the burst and shapes it.
- **Tracing**: one notification id covers the broadcast — debugging "why didn't I get the waitlist email" walks one batch, not 100K rows.
- **Backpressure path**: if Notification Svc returns 429 / circuit-breaker open, we requeue the batch in Kafka, not 100K times.

> Recall from `DesignNotificationSystem.md`: producers never pick channels or do per-user fan-out. We hand it one logical event and a user-id set.

### Why a Kafka topic between Booking and Waitlist?

| Direct call (Booking → Waitlist API)       | Kafka event                                          |
| ------------------------------------------ | ---------------------------------------------------- |
| Tight coupling — Booking knows we exist    | Loose coupling — anyone can subscribe (analytics, etc.) |
| Booking-time latency includes our work     | Booking acks the moment the event is in Kafka         |
| If Waitlist is down, bookings fail         | If Waitlist is down, events buffer; we catch up      |
| Hard to backfill                           | Kafka retention + offset rewind                      |

Easy win — say it out loud, take the points.

---

## 25–45 min: Deep Dives (pick 2)

### Deep Dive A: Notification Fan-out for a Hot Listing

**Scenario:** 100K users are on the waitlist for a popular Bali villa over Christmas week. The host cancels a 7-day reservation.

#### Without care, what goes wrong

- Matcher Worker pulls 100K rows in a single query → slow scan, locks the table.
- Updates 100K rows to NOTIFIED → write amplification.
- Sends 100K notifications in a single second → Notification Svc's per-provider rate limits trip.
- Notification Svc may degrade *other* notification traffic (booking confirms, host alerts) while it digests our burst.

#### What we do

1. **Bounded scan with chunking.** `LIMIT 1000` per query, paginate with `WHERE entry_id > last_seen`. Each chunk is one Kafka message into a `slot_notification_batches` topic.
2. **Partition the Kafka topic by `listing_id`**, so per-listing work is serial — no double-fan-out from concurrent processing of the same event.
3. **Use Notification Svc's batch endpoint** with `user_ids` as a list — let the downstream rate-limit shape it.
4. **Mark `NOTIFIED` after the batch is enqueued, not after delivery.** Delivery is best-effort at this layer; the user's notification prefs and provider state are the Notification Svc's problem.

#### Why not "notify only the first 10"?

> *I'd raise this as a tradeoff to the interviewer, not as an obvious choice.*

| Strategy                | Pro                                       | Con                                                  |
| ----------------------- | ----------------------------------------- | ---------------------------------------------------- |
| Broadcast all           | Maximum conversion likelihood; simplest    | Fan-out cost; users may feel like spam               |
| Notify first N (by joined_at) | Fewer notifications; "fair" feeling | Long tail never gets a chance; complex fairness rules |
| Wave-based (10, then 100 if no booking in 5min) | Best UX | Multi-step orchestration; race window if booking is instant |

Per the prompt: **broadcast** is the chosen design. We'd justify it as "matches the booking model where first-click-wins anyway; complexity of wave-based isn't worth the small UX win."

### Deep Dive B: The "Everyone Clicks At Once" Race

100K users get the push at roughly the same moment. The first to click triggers the Booking Service. **What is the waitlist's role in resolving the race?**

> *Answer: none. The waitlist surfaces the opportunity; the booking service's existing double-book defense (SQL row lock per (listing, date), see `DesignBookingSystem.md`) is the gate.*

Concretely:
- User clicks the notification deep-link → arrives at the standard Listing Detail page.
- Detail page calls `POST /v1/reservations` like any other booking.
- Booking Service's atomic `(listing, date) FOR UPDATE` lets one win, rest get a 409.
- The other 99,999 users see a "sorry, gone" page → their waitlist entry **stays ACTIVE** so the next opening notifies them again.

This is the right time to **explicitly defer to a dependency**:

> *"I'm not redesigning the booking transaction here — the Booking Service already has the (listing, date)-level SQL row lock that's the source of truth. The waitlist just surfaces the slot."*

#### One subtle thing: when do we transition `NOTIFIED` → `CONVERTED`?

When the Booking Service emits `reservation_confirmed` with the same `(listing, dates)`, our Matcher consumer sees it and:

```
UPDATE waitlist_entries
   SET status='CONVERTED', converted_at=NOW()
 WHERE listing_id = $L AND user_id = $U AND status = 'NOTIFIED'
```

This closes the loop. Analytics can compute notification → booking conversion rate.

#### What about the entries that DIDN'T convert?

They stay `NOTIFIED` — but the next `slot_opened` event re-evaluates them. We treat `NOTIFIED` as still-active for matching purposes, with a debounce (don't re-notify the same user about the same slot within 30 min).

### Deep Dive C: Auto-expire + Cleanup

A waitlist entry for "check-in 2026-12-24" is useless on 2026-12-25.

```
Expiry Worker (daily, sharded by listing_id):
  UPDATE waitlist_entries
     SET status='EXPIRED', expired_at=NOW()
   WHERE status IN ('ACTIVE','NOTIFIED')
     AND checkin_date < NOW();
```

This keeps the active index small (the `WHERE status='ACTIVE'` indexes don't bloat with stale entries).

Archive expired entries older than 90 days to S3 / cold storage for analytics, deleting from the hot table.

> Mention: same tiered-storage pattern as the job scheduler. Hot SQL stays small.

---

## 45–55 min: Scale + Failure Handling

### Sharding

- **Waitlist DB** sharded by `listing_id` — matches Booking Service shape, slot-match query is single-shard.
- **Kafka `slot_opened` topic** partitioned by `listing_id` — concurrent events for the same listing serialize, no double fan-out.
- **Matcher Worker** consumer-grouped on that topic — one in-flight slot per listing at a time.

### Caching

| Layer | What                                          | TTL    | Why                                          |
| ----- | --------------------------------------------- | ------ | -------------------------------------------- |
| Redis | "user X's active waitlist count"              | 60 s   | Powers user-facing badge; cheap to invalidate on write |
| Redis | "listing L's waitlist size by date histogram" | 5 min  | Host dashboard; tolerates lag                |

> Don't cache "the matching entries for a slot" — that's a one-shot query when slots open, not a hot read.

### Failure modes

| Failure                          | Behavior                                                          |
| -------------------------------- | ----------------------------------------------------------------- |
| Notification Svc down            | Matcher's batch POST fails → requeue in Kafka with exp backoff   |
| Waitlist DB shard down           | Joins / leaves return 503 for that listing; matcher backlog grows; catches up |
| Kafka `slot_opened` partition unavailable | Booking Svc's outbox holds events (it persists locally before publishing) |
| Matcher worker crashes mid-scan  | Kafka consumer offsets ensure replay; `NOTIFIED` flag is idempotent |
| Cancellation flood (1K cancels in 1 min) | Topic partitions absorb; matcher fan-out throttled by Notification Svc backpressure |
| Duplicate `slot_opened` events   | Matcher deduplicates by `(listing_id, freed_from, freed_to)` event key (Redis or DB) |
| Booking-confirmed event lost     | Stale `NOTIFIED` entries — debounce + next slot_open re-evaluates |

### Self-monitoring

- `slot_open_to_notify_ms_p99` — end-to-end freshness SLO.
- `notification_batch_size_p99` — flags hot listings worth product attention.
- `waitlist_active_size_p99_per_listing` — runaway listings to investigate.
- `conversion_rate_by_listing` — does waitlist actually convert? Product metric.

---

## 55–60 min: Trade-offs / Common Mistakes

| Mistake                                                              | Effect                                            | Fix                                                  |
| -------------------------------------------------------------------- | ------------------------------------------------- | ---------------------------------------------------- |
| Waitlist Service makes the booking too                               | Duplicates Booking's locking; two double-book defenses to maintain | Defer to existing Booking Service        |
| Direct API call Booking → Waitlist                                   | Booking outage when Waitlist is down              | Kafka event in the middle                            |
| One notification per matched user                                    | N HTTP calls to Notification Svc; rate-limit storm | Batch endpoint with `user_ids` list                  |
| No idempotency on join                                               | Double-tap → duplicate entry                      | `Idempotency-Key` header → DB unique constraint      |
| FIFO ordering as a hard guarantee                                    | Implies real-time priority queue with locks       | Broadcast + first-click-wins; FIFO is best-effort    |
| "Notify only the top 10" without saying why                          | Long tail never converts; complex fairness        | If asked, defend broadcast on simplicity grounds     |
| No debounce on re-notification                                       | Same user pinged 5x for nearby slot openings      | Per (entry, slot-bucket) cooldown                    |
| Auto-expire is missing                                               | `ACTIVE` index bloats; matcher scans get slow     | Daily Expiry Worker; archive to cold storage         |
| Storing entries by user, not by listing                              | Slot-open match becomes a full-table scan         | Index by `(listing_id, checkin_date)`                |
| Position shown as exact `42`                                         | Misleading — broadcast means position ≠ outcome   | Call it `position_estimate` or omit                  |
| Cache-as-source-of-truth for "active entries"                        | Lost cache → wrong notifications                  | Redis is read-cache only; SQL is the gate            |

### Key Concepts for the Interview

| Topic                                | What to say                                                                                |
| ------------------------------------ | ------------------------------------------------------------------------------------------ |
| Scope discipline                     | Booking transaction is OUT. Notification per-provider mechanics are OUT. We own *matching*. |
| Event-driven coupling                | Kafka between Booking and Waitlist — survives outages, enables analytics consumers.        |
| Broadcast is intentional             | Not because it's easy — because it matches the first-click-wins booking model.              |
| Batch into Notification Svc          | Don't fan out 100K HTTP calls; one batch lets the downstream rate-limiter do its job.       |
| Shard by `listing_id`                | Matches booking; slot-match query is single-shard; partitions Kafka cleanly.                |
| Indexes that matter                  | `(listing_id, checkin_date)` for matching; `(user_id, status)` for "my waitlist".          |
| Auto-expire keeps the index small    | Daily worker prunes; ACTIVE index stays cache-resident.                                     |
| Drive the conversation               | Open with 3–5 clarifying questions you answer yourself. Then check in: "anything else?"     |

### Wrap-Up

| Aspect                          | Solution                                                |
| ------------------------------- | ------------------------------------------------------- |
| Join / leave                    | REST API with idempotency key; SQL UNIQUE constraint   |
| Slot-open detection             | Kafka event from Booking Svc                            |
| Match entries to freed slot     | Single-shard range query by `listing_id` + dates        |
| Notify all matched users        | One batched call to Notification Svc                    |
| Race resolution                 | Defer entirely to Booking Svc's row lock                |
| Re-notify safely                | Debounce per (entry, slot) to avoid spam                |
| Auto-expire                     | Daily Expiry Worker; archive to S3 after 90 d           |
| Sharding                        | By `listing_id`, aligned with Booking and Kafka         |
| Scale guard                     | Bounded scan + chunked Kafka batches; Notification Svc absorbs the burst |
| Observability                   | `slot_open_to_notify_ms`, conversion rate, hot-listing sizes |

---

## Driving the conversation — phrases that work

The interview feedback was: *interviewer wants you to drive*. Here are the actual sentences:

| When                            | Say                                                                                                  |
| ------------------------------- | ---------------------------------------------------------------------------------------------------- |
| At the start                    | *"Let me propose scope; tell me if you'd narrow or widen. Three questions I want to pin: trigger source, broadcast vs per-position, who owns the booking."* |
| After drawing the diagram       | *"What I haven't covered yet: auto-expire, race resolution on click, fan-out to a 100K-entry list. Where would you like to go?"* |
| Before a tradeoff               | *"There are three reasonable options here: A, B, C. I'd pick B because (...). I want to make sure that matches your priorities — would you push for A?"* |
| When asked to scale             | *"Two stress points: hot listing fan-out and concurrent click race. The first is bounded scan + batched notify; the second I defer to Booking's existing row lock."* |
| Near the end                    | *"Have I covered the parts you wanted? If we have time, I can go deeper on (X) or (Y)."*             |
