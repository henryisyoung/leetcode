# Design Google Calendar — 60-min Interview Version

A multi-user calendar service that stores events, expands recurring rules, answers free/busy queries across attendees, and fires reminders — across timezones and DST.

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

### Step 1 — Pin the read shape (this is the #1 score-getter)

Calendars look like a write-heavy CRUD app. They are not. They are dominated by **one read pattern**:

> *"Show me everything on the screen for this user, this week, expanded, in their timezone."*

Two query shapes dominate, both reads:

| Read shape          | Example                              | Latency budget |
| ------------------- | ------------------------------------ | -------------- |
| **Range query**     | "Give me my events for next 7 days"  | < 200 ms p99   |
| **Free/busy fan-out** | "When can these 10 people meet?"   | < 500 ms p99   |

Both must expand recurring rules. **Recurrence expansion is the load.**

### Step 2 — Functional requirements

- Create / update / delete a one-off event.
- Create a **recurring** event (RFC 5545 RRULE: weekly, monthly, with exceptions).
- Invite attendees; track RSVPs (accept/decline/tentative).
- Free/busy lookup across N attendees over a time range.
- Reminders (push / email) at T minus N minutes.
- Calendar sharing (read, write, free-busy-only).

### Step 3 — Non-functional requirements

| Requirement            | Target                          |
| ---------------------- | ------------------------------- |
| Read p99 (week view)   | < 200 ms                        |
| Free/busy p99 (10 ppl) | < 500 ms                        |
| Write durability       | Replicated, no lost events      |
| Reminder accuracy      | ± 30 s of requested time        |
| Multi-device sync      | < 5 s lag for the same user     |

### Step 4 — Capacity math (one block, move on)

```
1 B users · 5 events/user/day              ≈ 60 K writes/sec avg
Reads ≈ 50× writes (everyone refreshes)    ≈ 3 M reads/sec
Avg event row                              ≈ 1 KB metadata + ICS body
Storage: 5 events × 365 × 5 yr × 1 KB × 1 B ≈ 9 PB raw → 3 PB compressed
Active recurring events per user           ~10s, expanded → 100s of instances/week
```

> **Take-away:** writes are easy; the system is **dominated by expanded-read fan-out**. Every later choice (materialization, sharded reads, free/busy index) is about read amplification.

---

## 5–10 min: API the Client Calls

### Create / update an event

```http
POST /v1/events
{
  "calendar_id": "cal_alice",
  "title": "Weekly sync",
  "start": "2026-05-04T15:00:00",
  "end":   "2026-05-04T16:00:00",
  "tz":    "America/Los_Angeles",
  "rrule": "FREQ=WEEKLY;BYDAY=MO;UNTIL=20261231T000000Z",
  "attendees": ["bob@x.com", "carol@x.com"],
  "reminders": [{ "method": "push", "minutes_before": 10 }]
}
```

Return the canonical event id + ETag.

```http
PATCH /v1/events/{id}             // edit ONE instance OR the whole series
DELETE /v1/events/{id}?scope=this_and_following
```

### Range query (the hot path)

```http
GET /v1/calendars/{id}/events?start=2026-05-04T00:00&end=2026-05-11T00:00&tz=America/Los_Angeles
```

Server expands all RRULEs that overlap the window and returns **flat instance rows**, not the raw RRULE. Client never has to know about RFC 5545.

### Free/busy

```http
POST /v1/freebusy
{
  "calendars": ["cal_bob", "cal_carol", "cal_dave"],
  "start": "2026-05-04T09:00",
  "end":   "2026-05-04T18:00",
  "tz":    "America/Los_Angeles"
}
→ { "cal_bob": [{start,end}, …], "cal_carol": [...], ... }
```

Returns intervals only — no titles, no attendees. This is what permissions allow for "free-busy-only" sharing.

### Sync (multi-device)

```http
GET /v1/calendars/{id}/sync?since_token=opaque_cursor
```

Returns deltas since the cursor + a new cursor. Web/iOS/Android clients use this — never a full re-fetch.

---

## 10–25 min: Data Model + High-Level Architecture

### Tables (logical)

```
events
├── event_id (PK)
├── calendar_id      // owner calendar; sharded by this
├── series_id        // = event_id for one-off; same value across recurring instances
├── start_utc, end_utc
├── tz               // store the original TZ string, NOT just UTC
├── rrule            // RFC 5545 expression, NULL for one-off
├── rdate / exdate   // explicit dates added/removed from the series
├── title, location, body
├── version, etag
├── visibility       // public / private / free-busy-only
├── updated_at       // for sync deltas

event_overrides
├── series_id (FK)
├── instance_start_utc   // identifies which instance is being overridden
├── new_start_utc, new_end_utc, new_title, ...
├── deleted (bool)
PRIMARY KEY (series_id, instance_start_utc)

attendees
├── event_id (FK), user_email
├── response_status, optional
├── notified_at

freebusy_index             // materialized read cache (see deep dive)
├── calendar_id, day_bucket   // bucket = 1 day
├── intervals: [(start, end), ...]   // pre-expanded busy intervals

reminders_due
├── fire_at_utc (INDEXED)
├── event_id, user_id, method
```

**Critical decision: store `tz` as a string, not just UTC.**
"Every Monday at 9 AM Pacific" must keep firing at 9 AM **even across DST transitions**. If you only store UTC, your "9 AM" becomes 10 AM after spring-forward. Store the local time + tz string and convert at read time using the IANA TZ database.

### Architecture

```
                ┌────────────┐
client ─────────► API Gateway│
                └────┬───────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
   Event Service  FreeBusy Svc  Reminder Svc
        │            │            │
        ▼            ▼            ▼
   ┌──────────────────────────────────┐
   │  Sharded SQL (by calendar_id)    │   ← events, overrides, attendees
   │  + per-shard read replicas       │
   └──────────────────────────────────┘
        │            │
        ▼            ▼
   Recurrence    Materialized
   Expander      FreeBusy Cache
   (in-process)  (Redis or SQL)
                     │
                     ▼
              Reminder Scheduler  ───► Kafka ───► Push/Email Workers
              (time-sharded buckets)
```

| Service          | Role                                                          |
| ---------------- | ------------------------------------------------------------- |
| Event Service    | CRUD; writes events + overrides; emits change events to Kafka |
| FreeBusy Service | Fans out to N calendars; merges busy intervals                |
| Reminder Service | Reads `reminders_due` shards; enqueues fire jobs              |
| Sharded SQL      | Source of truth, sharded by `calendar_id`                     |
| Materialized cache | Pre-expanded busy intervals per day, invalidated on write   |

### The Read Flow (week view)

1. Client GET `/events?start=…&end=…`.
2. API loads all `events` rows where `series_id ∈ user's calendars` and the series **could** overlap the window (cheap bounding check on `dtstart`, `until`).
3. For each series, expand RRULE in process to instances in the window.
4. Apply `event_overrides` (some instances are moved or cancelled).
5. Convert each instance to the requested tz, return.

> **Steps 3–4 are the load.** Cache them.

---

## 25–45 min: Deep Dives (pick 2)

### Deep Dive A: Recurring Events Done Right

#### Why "just store UTC" is wrong

Spring forward: 02:00–03:00 PST disappears. Fall back: 01:00–02:00 PST happens twice. A "9 AM weekly" series in UTC would land at 16:00 UTC half the year and 17:00 UTC the other half — and a UTC-stored RRULE will silently shift. Store **(local_start, tz, rrule)** and **expand at read time** using IANA tzdata.

#### Storing the series + exceptions

```
series_id = E123
master    : "Every Mon 9 AM PT, until 2026-12-31"
overrides :
  - instance_start = 2026-05-11   moved to 10 AM
  - instance_start = 2026-05-18   cancelled (exdate)
```

Three operations users will perform — all three matter for the UX:

| Edit scope         | Storage effect                                                   |
| ------------------ | ---------------------------------------------------------------- |
| **This event only** | Insert into `event_overrides` keyed by `(series_id, instance_start)`. |
| **This and following** | Truncate master with `UNTIL = instance_start - 1`. Insert NEW series starting at `instance_start`. |
| **All events**     | Update the master row. (Existing overrides still apply unless cleared.) |

#### Expansion algorithm

```python
def expand(series, window_start, window_end, tz):
    rule = parse_rrule(series.rrule, dtstart=series.local_start, tz=tz)
    raw = list(rule.between(window_start, window_end, inc=True))   # IANA-aware
    overrides = load_overrides(series.id, window_start, window_end)
    out = []
    for inst_start in raw:
        ov = overrides.get(inst_start)
        if ov and ov.deleted: continue
        out.append(apply_override(make_instance(series, inst_start), ov))
    out.extend(extra_rdates(series, window_start, window_end))
    return out
```

#### Bounded expansion

A `RRULE=FREQ=MINUTELY;COUNT=10000000` series would OOM your service. Hard caps:

- **No more than ~1000 instances per single API response.** Return a pagination cursor.
- **Reject RRULE patterns** with `INTERVAL=1; FREQ=SECONDLY/MINUTELY` unless `COUNT` or `UNTIL` is bounded.
- **Server-side window cap** of e.g. 1 year per read call.

### Deep Dive B: Free/Busy Across N Attendees

#### The naive approach blows up

"When can these 10 people meet next week?" expands to:

```
10 calendars × 7 days × ~20 events/day = 1400 events to expand and merge
```

Done per request, that's expensive at 3 M reads/sec.

#### The fix: per-calendar, per-day busy intervals (materialized)

```
freebusy_index { calendar_id, day_bucket UTC, intervals }

Example row:
  calendar_id = cal_bob
  day_bucket  = 2026-05-04
  intervals   = [(09:00, 10:00), (13:30, 14:00), (15:00, 16:00)]
```

- Write path: when an event is created/updated/deleted, **invalidate** the affected `(calendar_id, day_bucket)` rows. A background expander recomputes them from `events` + `event_overrides`.
- Read path: free/busy reads pull pre-expanded intervals directly. Merging N attendees becomes "sort + sweep" over a few hundred intervals — microseconds.

#### Trade-off

| Strategy                  | Pro                            | Con                            |
| ------------------------- | ------------------------------ | ------------------------------ |
| Recompute on every read   | Always fresh; no extra storage | Slow at fan-out; CPU-bound     |
| Materialize per-day cache | Fast reads; cheap fan-out      | Write amplification; eventual consistency on free/busy |

> Pick materialization. Free/busy is a fundamentally **read-amplified** problem.

#### Privacy

Free/busy must reveal **intervals only** — not titles, locations, or attendees. The materialized table stores only `(start, end)`, so accidental over-fetch can't leak details.

### Deep Dive C: Reminders at Scale

100 M reminders/day, each must fire within ±30 s. Don't iterate the events table for "what's due now" — too much I/O.

#### Time-bucketed scheduler

```
reminders_due is sharded by fire_at_utc / 60 (one shard per minute)
```

Each shard has a leader (etcd lease). The leader polls only its bucket:

```sql
SELECT * FROM reminders_due
WHERE fire_at_utc BETWEEN $now AND $now + 60s
  AND shard = $my_shard
LIMIT 10000;
```

Push each row to Kafka. Push/email workers consume from Kafka.

This is the same pattern as the distributed scheduler — leases, idempotency keys on `(reminder_id)`, exponential backoff with jitter, DLQ after N retries. See `DesignJobScheduler.md`.

### Deep Dive D: Sync Across Devices

#### The sync token

A monotonically-increasing per-user cursor stored as `(shard_id, max_updated_at_seen)`. Client sends `since_token`; server returns:

```json
{
  "changes": [{ "event_id": ..., "version": ..., "deleted": false }, …],
  "next_token": "opaque-cursor",
  "truncated": false   // true → client must do a full re-sync
}
```

#### Long-tail edge cases

- **Client clock skew.** Never trust the client's `now`. Server-stamps everything.
- **Backfill / tombstones.** When an event is deleted, keep a tombstone row for ~30 days so offline clients can learn about the deletion on next sync.
- **Token expiry.** If `since_token` is older than the tombstone retention, force a full sync (`truncated: true`). Don't let the client believe a deleted event still exists.

---

## 45–55 min: Scale + Failure Handling

### Sharding

- **Events / overrides / attendees**: sharded by `calendar_id`. Co-locates all of one user's data; range queries hit one shard.
- **Free/busy index**: sharded by `calendar_id` too, so writes hit the same shard.
- **Reminders**: sharded by `fire_at_utc / 60` so that "what's due in the next minute" is one shard's responsibility, regardless of which user owns it.

### Caching

| Layer                 | What lives there                              | TTL          |
| --------------------- | --------------------------------------------- | ------------ |
| Redis (per user)      | Last 7 days of expanded instances             | 5 min        |
| Redis (per calendar)  | Free/busy intervals (already materialized in SQL too — Redis is the fast path) | invalidate on write |
| CDN                   | Static calendar UI assets                     | days         |

Cache invalidation on write: the Event Service emits a change event to Kafka; a small consumer evicts the affected Redis keys + recomputes the freebusy_index row.

### Failure modes

| Failure                       | What we do                                             |
| ----------------------------- | ------------------------------------------------------ |
| SQL shard down                | Read replicas serve stale data; writes queue in Kafka with replay on recovery |
| RRULE expander OOMs           | Caps + circuit breaker + reject patterns with COUNT > 10K |
| Reminder Kafka backed up      | Apply backpressure to scheduler; firing late is preferred over duplicating |
| Free/busy cache stale         | Reads include `as_of` timestamp; client may force `?fresh=true` (slow path) |
| Timezone DB out of date       | Pin tzdata version; deploy updates ahead of biannual DST transitions |

### Why no event-sourcing here?

For meetings/calendars, the natural model is a **mutable row with version + ETag**, not an append-only log. Event-sourcing buys you nothing for "the current state of this event" and complicates RRULE editing semantically. The Kafka stream of changes is for cache invalidation + sync deltas, not source of truth.

---

## 55–60 min: Trade-offs / Common Mistakes

| Mistake                                       | Effect                                              | Fix                                                |
| --------------------------------------------- | --------------------------------------------------- | -------------------------------------------------- |
| Storing recurring events in UTC only          | DST silently shifts meeting times                   | Store local time + tz string; expand with IANA db  |
| Expanding RRULEs on every read                | CPU bound; fan-out unusable                         | Materialize per-day free/busy; cache instances     |
| Edit-this-and-following silently broken       | Future edits affect past instances or vice versa    | Truncate master with `UNTIL`; insert new series    |
| One huge "deleted" flag on a recurring event  | All instances disappear forever                     | Use exdate / overrides at the instance level       |
| Reminders loop scans the events table         | I/O storm at the top of every minute                | Time-bucketed `reminders_due` table, leader per bucket |
| Free/busy returns titles                      | Privacy leak for "free-busy-only" sharing           | Materialized index stores intervals only           |
| Sync without tombstones                       | Offline clients show events that were deleted       | 30-day tombstone retention                         |
| No bound on RRULE expansion                   | `FREQ=SECONDLY;COUNT=∞` OOMs the service            | Reject unbounded; cap window + instance count      |

### Key Concepts for the Interview

| Topic                              | What to say                                                                       |
| ---------------------------------- | --------------------------------------------------------------------------------- |
| Calendars are read-heavy           | Reads ~50× writes; design for expanded-instance fan-out                           |
| Local time + tz, not UTC           | DST handling is the #1 thing interviewers probe                                   |
| Materialize free/busy              | Per-calendar, per-day intervals; merging N attendees is a sort+sweep              |
| Edit scope (this / following / all)| Different storage semantics for each; mention all three                           |
| Time-bucketed reminders            | Avoid scanning the events table; lease + dedup like a job scheduler               |
| Sync via opaque cursor             | Tombstones, truncation, forced full-resync edge case                              |
| Privacy in shared calendars        | Free-busy-only means intervals only; enforce at storage, not at API layer         |

### Wrap-Up

| Aspect                          | Solution                                          |
| ------------------------------- | ------------------------------------------------- |
| Recurring events + DST          | Store local time + tz; expand at read with IANA   |
| Edit semantics                  | Series master + per-instance overrides            |
| Free/busy at fan-out            | Materialized per-day busy intervals               |
| Hot read path                   | Per-user Redis cache, invalidated on write        |
| Reminders                       | Time-bucketed scheduler with leader per minute    |
| Multi-device sync               | Opaque cursor + tombstones, forced full-resync    |
| Sharding                        | `calendar_id` for events; `fire_at` for reminders |
| Privacy                         | Storage-layer separation of intervals vs metadata |
