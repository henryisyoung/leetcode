# Design a Notification System — 60-min Interview Version

Deliver notifications to users across multiple channels (push, email, SMS, in-app) — millions per minute, respecting per-user preferences, quiet hours, rate limits, and provider reliability. Examples: "your reservation is confirmed", "host accepted your request", "price drop on this listing".

**Time budget**

| Minutes | What you do                              |
| ------- | ---------------------------------------- |
| 0–5     | Clarify requirements + capacity math     |
| 5–10    | API the producers call                   |
| 10–25   | High-level architecture + the send flow  |
| 25–45   | Pick 2 deep dives                        |
| 45–55   | Scale + failure handling                 |
| 55–60   | Trade-offs and wrap-up                   |

---

## 0–5 min: Clarify

### Step 1 — Name the four hard problems

Before any boxes:

1. **Fan-out**: one event ("price drop") → N users × M channels.
2. **Per-user preferences**: opt-out by channel, quiet hours, locale.
3. **Provider reliability**: APNs / FCM / Twilio / SendGrid all fail differently.
4. **De-duplication**: producers retry — recipients should not see the same notification twice.

> Say out loud: *"This is at-least-once delivery to external systems we don't control. Idempotency + provider-side dedup keys are how we avoid duplicate notifications."*

### Step 2 — Functional requirements

- **Transactional notifications** ("booking confirmed") — high priority, low latency.
- **Marketing notifications** ("new listings near you") — high volume, low priority, batchable.
- **In-app inbox** — durable, paginated, with read receipts.
- **User preferences** — channel opt-in/out, quiet hours per timezone, frequency caps.
- **Templates + localization** — same notification rendered in user's locale.
- **Tracking** — sent, delivered, opened, clicked.

### Step 3 — Non-functional requirements

| Requirement              | Target                                    |
| ------------------------ | ----------------------------------------- |
| Transactional latency    | < 5 s p99 from event → delivery attempt   |
| Throughput               | 1 M notifications/min peak                |
| Delivery guarantee       | At-least-once attempt; idempotent         |
| Preferences strong-consistent | A user who clicks "unsubscribe" must not get next email |
| Provider outage handling | Graceful: queue, retry, fail to in-app    |
| Compliance               | CAN-SPAM, GDPR delete-on-request          |

### Step 4 — Capacity math

```
1 M users → ~10 notifs/user/day              ≈ 100 M/day ≈ 1 K/sec avg
Peak (marketing campaign at 09:00)           ≈ 100 K/sec for 10 min
Avg notification metadata                     ~1 KB
Per-channel render output                     ~5 KB email body, ~200 B push payload
Storage: 100 M/day × 1 KB × 90d retention    ≈ 9 TB hot, archive to S3
Provider API limits: APNs ~9K req/sec/conn, SendGrid ~10K/sec, Twilio ~100/sec/number
```

> **Take-away:** transactional QPS is modest; **marketing bursts** dominate; **per-provider rate limits** are the real constraint. Architecture is a fan-out → router → per-channel queue with backpressure.

---

## 5–10 min: API the Producers Call

### Single producer interface (channel-agnostic)

Producers don't know which channels exist. They send a logical "notification request":

```http
POST /v1/notifications
Headers: Idempotency-Key: booking_confirm_r12345
{
  "user_id": "u_42",
  "type":    "booking_confirmed",
  "data":    { "reservation_id": "r12345", "checkin": "2026-06-10" },
  "priority": "transactional"   // or "marketing"
}
→ 202 Accepted { "notification_id": "n_…" }
```

The Notification Service decides:
- Which channels to use (based on user prefs + notification type policy).
- Which template to render (`booking_confirmed.email.tmpl`, `.push.tmpl`, `.sms.tmpl`).
- When to send (now if transactional; respect quiet hours if marketing).

> **Key abstraction:** producers know **what** to notify and **why**, never **how** or **where**. This is what lets you add WhatsApp delivery later without changing any producer.

### Batch / campaign

```http
POST /v1/campaigns
{
  "audience": "users matching segment_id=s_99",
  "template": "weekly_listings_digest",
  "schedule": "2026-05-05T09:00 user_local"
}
```

### Preferences

```http
GET  /v1/users/{id}/preferences
PUT  /v1/users/{id}/preferences
{ "channels": { "email": true, "push": true, "sms": false },
  "categories": { "marketing": false, "transactional": true },
  "quiet_hours": { "start": "22:00", "end": "07:00", "tz": "America/Los_Angeles" } }
```

### Webhook / unsubscribe

```http
POST /v1/webhooks/sendgrid/events      // delivery + bounce + spam events
GET  /unsubscribe?token=<signed>       // one-click unsub link
```

---

## 10–25 min: High-Level Architecture + Send Flow

### Five layers, draw this

```
producer ─► Notification API ─► Kafka(notif_requests) ─► Router
                                                            │
                              ┌─────────────────────────────┼─────────────────────────────┐
                              ▼                             ▼                             ▼
                       Push Worker                     Email Worker                   SMS Worker
                       │ APNs / FCM                    │ SendGrid                     │ Twilio
                       ▼                                ▼                              ▼
                   Provider                         Provider                       Provider
                   (rate-limited, retry)            (template rendered, signed)    (number pool, rate-limited)
                              │
                              ▼
                       Status Tracker  ◄── webhooks from providers (delivered/bounced)
                              │
                              ▼
                       In-app Inbox + Analytics
```

| Component             | Role                                                                  |
| --------------------- | --------------------------------------------------------------------- |
| Notification API      | Idempotency dedup; preference lookup; emit to Kafka                   |
| Router                | Per-notification, per-user fan-out to one or more channel topics       |
| Per-channel workers   | Render template + call provider with circuit breaker + retry          |
| Status Tracker        | Webhook receiver; correlates with sent notifications; powers retries  |
| In-app Inbox          | Durable per-user notification list (Cassandra/DynamoDB)               |
| Preference Service    | User prefs, quiet hours, frequency caps; cached in Redis              |
| Template Service      | Versioned templates, localization, AB variants                        |

### The Send Flow

```
1. Producer POSTs /v1/notifications with Idempotency-Key
2. Notification API:
     - INSERT notifications(id, user_id, type, data, ...)
       ON CONFLICT(idempotency_key) DO NOTHING
     - Emit `notif_request{notification_id}` to Kafka
     - Return 202 immediately (~10 ms)
3. Router consumes:
     - Load user preferences (Redis-cached)
     - Apply channel policy: which channels does THIS type use × user prefs?
     - Apply quiet hours: marketing → defer to next allowed window
     - Apply frequency caps: too many today → drop or coalesce
     - For each chosen channel: emit `channel_send{notification_id, channel}` to that channel's Kafka topic
4. Channel worker (e.g. push):
     - Load template + user locale → render
     - Call provider (APNs) with X-APNs-Id = stable hash of (notification_id, channel)
     - On success → write status; on 4xx → permanent fail; on 5xx/timeout → retry with backoff
5. Status Tracker receives provider webhook (delivered/bounced/spam):
     - Update notification status row
     - On hard bounce → mark email as bad; suppress future sends
```

> Step 2 returns in **~10 ms**. The whole fan-out happens off the critical path of the producer's request.

### Why a Router service, not direct channel dispatch?

- **One place** to apply prefs, quiet hours, caps, A/B routing.
- **One place** to log "we decided to send X via channel Y because Z" — golden for debugging "why didn't I get an email?".
- Producers + workers stay simple and unaware of policy.

---

## 25–45 min: Deep Dives (pick 2)

### Deep Dive A: Per-Provider Rate Limits + Circuit Breakers

Providers have strict, asymmetric limits:

| Provider       | Limit                              | Failure mode                              |
| -------------- | ---------------------------------- | ----------------------------------------- |
| APNs (Apple)   | ~9K req/sec per HTTP/2 connection  | Stream reset on rate limit                |
| FCM (Google)   | ~600K req/min per project          | 429 with `Retry-After`                    |
| Twilio SMS     | ~100 req/sec per phone number      | Hard error; need pool of numbers          |
| SendGrid       | ~10K req/sec per API key           | Slow degradation                          |

**Token bucket per provider**, refreshed on the `Retry-After` header. When the bucket empties, the channel worker stops pulling from Kafka → backpressure flows back through the queue. No customer-facing impact; the queue holds it.

#### Circuit breaker

```
state:    CLOSED → OPEN → HALF_OPEN → CLOSED
trip:     5xx rate > 30% over 1 min
open:     reject sends fast; queue continues to fill
recover:  after 30 s, allow N probe requests
```

When OPEN: 
- **Transactional** notifications: try the next-best channel (push → email fallback).
- **Marketing**: defer until provider recovers.

#### Connection pooling

APNs uses HTTP/2 multiplexing; one connection handles many in-flight streams. Don't open one connection per worker — share a connection pool sized per provider's per-connection limit.

### Deep Dive B: User Preferences, Quiet Hours, Frequency Caps

#### Preferences must be strong-consistent on the send path

A user who taps "unsubscribe" must not receive the next email. The Preferences Service is backed by a SQL store, with a Redis cache invalidated on write.

```
User updates pref:
  1. UPDATE prefs ... ;  COMMIT
  2. DELETE redis:prefs:{user_id}
  3. PUBLISH `pref_changed{user_id}` so router caches in flight invalidate
```

> If the producer retried right after an unsub, the second attempt would re-render and re-send — except the router's pref check now blocks it. Idempotency dedup on the producer side prevents the *same* notification id from being re-checked; preference check prevents *any future* notification.

#### Quiet hours

Stored per user as `{start_local, end_local, tz}`. The router converts to UTC at decision time:

```python
if priority == marketing:
    if now_in_user_tz in quiet_hours(user):
        delay_until = next_allowed_local_time(user)
        schedule_for(notification_id, delay_until)
        return
```

Scheduling is via a delayed Kafka topic (or a small per-bucket scheduler — see `DesignJobScheduler.md`).

> Transactional notifications **bypass quiet hours**. "Booking confirmed" can wake you up; "new listings" cannot.

#### Frequency caps

Per-user, per-category counters in Redis with TTL:

```
INCR ratelimit:u_42:marketing:today
EXPIRE ratelimit:u_42:marketing:today  86400
if value > N: drop or coalesce into a digest
```

**Coalescing** is the under-rated feature: instead of 5 push notifications about 5 separate price drops, fold them into one ("3 listings you saved had price drops").

### Deep Dive C: De-duplication, Idempotency, and Exactly-Once Illusion

Three places where duplicates can happen, three different keys:

| Where                  | Cause                                        | Dedup key                                                |
| ---------------------- | -------------------------------------------- | -------------------------------------------------------- |
| Producer retry         | Network glitch, double POST                  | `Idempotency-Key` on the create-notification API         |
| Kafka redelivery       | Worker crash before commit                   | `notification_id` unique in `notification_status` table  |
| Provider retry         | Worker calls APNs twice (e.g. timeout retry) | `X-APNs-Id` / `X-Idempotency-Key` to the provider        |

End-to-end: a single notification gets a stable id from end to end. Same key flows from client → API → channel worker → provider. Any node on the path can crash + retry, and the dedup propagates.

> Tell the interviewer: *"At-least-once + idempotency is exactly-once from the user's perspective, and that's the only perspective that matters."*

### Deep Dive D: Marketing Campaign Fan-out (the burst problem)

"Send digest to 100 M users at 09:00 user-local."

#### Naive: one notification per user at 09:00

Spikes the system at 09:00 PST, then 09:00 MST, then 09:00 EST, etc. Channel workers run flat-out for hours. Worst-case all timezones aligned (UTC users) → 100M / 60 = 1.6 M/sec for a minute, way past provider limits.

#### Better: time-bucketed expansion

```
1. Marketing system uploads audience as { user_id, target_local_time }
2. Campaign scheduler bucket-sorts by 5-min UTC slots
3. Each slot's worker:
   - SELECT users WHERE bucket = $slot
   - For each user: emit standard `notif_request` to Kafka (just like a transactional one)
4. Kafka backpressure naturally smooths per-provider rate
```

The campaign is just a producer of standard notifications, fanned out over time. **Don't build a separate path for marketing** — that's two systems to maintain.

#### Audience generation

Audience selection is its own pipeline (analytics/data warehouse). The campaign system only consumes "here are user_ids to send to" — keeps the notification system unaware of marketing logic.

### Deep Dive E: In-App Inbox

Distinct from push: a durable list of every notification, paginated, with read receipts. Same data model as group-chat user-inbox:

```
notification_inbox
├── user_id (PK, partition key)
├── notification_id (clustering key, sorted desc by time)
├── type, data (rendered JSON for in-app display)
├── delivered_at, read_at
```

Every channel worker also writes to the in-app inbox. **The inbox is the failover** for users who opted out of email AND turned off push: they still see notifications in the app.

---

## 45–55 min: Scale + Failure Handling

### Sharding

- `notifications` and `notification_inbox` sharded by `user_id` (co-locates a user's data).
- Kafka topics partitioned by `user_id` so per-user prefs/quiet-hours decisions land on the same consumer (rolling cache locality).
- Per-channel topics partitioned by `(provider, region)` to align with the provider's geographic POPs.

### Failure modes

| Failure                          | Behavior                                                            |
| -------------------------------- | ------------------------------------------------------------------- |
| APNs down                        | Push circuit OPEN → transactional falls back to email; marketing defers |
| SendGrid 5xx storm               | Email circuit OPEN → in-app inbox always still works                |
| Preferences DB read fails        | Stale Redis cache; fall back to "no marketing, transactional only" (safe default) |
| Kafka partition unavailable      | Producer API returns 503; clients retry with idempotency key        |
| Webhook flood from provider      | Webhook receiver buffers in its own Kafka topic; processed async    |
| Bad template (renders to empty)  | Channel worker fails-closed; alert; quarantine + skip               |
| User unsubscribed mid-campaign   | Router's preference check on consumption blocks send                |

### Suppression list

Hard bounces, spam reports, unsubscribes all feed a **global suppression list** keyed by `(channel, user_id_or_email)`. Channel workers check this in-process (cached) before each send.

```
Suppression check is BEFORE provider call.
Once suppressed, never send again without explicit re-opt-in.
```

This is also a **legal requirement** (CAN-SPAM) — a missing suppression check is not just bad UX.

### Self-monitoring

Notifications fan out from every other system. If notifications break, you lose visibility into all of them. So:
- Self-healthcheck notification ("did this make it from API → APNs in < 5 s?") fires every 60 s; absence pages on-call.
- Notification system's own alerts go through a **separate** out-of-band pager integration — never `notification_system.notify("notification system is down")`.

---

## 55–60 min: Trade-offs / Common Mistakes

| Mistake                                                                 | Effect                                       | Fix                                                      |
| ----------------------------------------------------------------------- | -------------------------------------------- | -------------------------------------------------------- |
| Producers pick channels themselves                                      | Adding WhatsApp requires changing all producers | Channel-agnostic API; Router decides                  |
| Provider call inside producer request                                   | Provider outage = producer outage            | Always go through Kafka                                  |
| One token bucket per worker (per-process rate limit)                    | Fleet collectively breaks provider limits     | Shared rate limiter (Redis sliding window) per provider |
| No `Idempotency-Key`                                                    | Network retry → duplicate notification        | Header from producer → DB unique constraint              |
| No provider-side dedup key                                              | Worker retry → user gets it twice             | `X-APNs-Id` / `X-Idempotency-Key` per provider           |
| Transactional and marketing share a queue                               | Marketing burst delays "booking confirmed"    | Priority lanes: separate topics + workers                |
| Quiet hours ignored                                                     | 3 AM marketing push for night shift           | Convert with user tz at routing time                     |
| Preferences cache > 5 min TTL with no invalidation                      | User unsubs but still gets next email         | Invalidate cache on pref write; subscribe to pref-changed events |
| No suppression list                                                     | CAN-SPAM violation; user complaints           | Hard bounces / unsubs → suppression DB checked pre-send  |
| Campaign system has its own send path                                   | Two systems to maintain; bugs in one only     | Campaign expands to standard `notif_request` events      |
| Synchronous webhook processing                                          | Provider webhook flood DoS's status tracker   | Webhook → Kafka → async processing                       |
| In-app inbox written by producer, not channel worker                    | Inbox shows things that were never sent       | Inbox write happens in the same path as the channel send |

### Key Concepts for the Interview

| Topic                              | What to say                                                                            |
| ---------------------------------- | -------------------------------------------------------------------------------------- |
| Channel-agnostic producer API      | Producers say *what + why*; system decides *how + where*. Future-proofs new channels.   |
| Idempotency end-to-end             | Stable id from producer → API → worker → provider. Same key everywhere.                 |
| Provider rate limits = real cap    | Shared token bucket per provider; backpressure through Kafka, not customer-facing.       |
| Priority lanes                     | Transactional ≠ marketing. Separate topics, separate workers, separate caps.            |
| Quiet hours + frequency caps       | Marketing only. Transactional bypasses. Coalesce when capped.                          |
| Circuit breakers + fallback        | Push down → try email. Email down → in-app inbox. Always degrade, never drop silently.   |
| Suppression list                   | Required for compliance; checked pre-send; sourced from bounces + unsubs + spam reports. |
| Webhooks async                     | Receive → Kafka → process. Sync processing = DoS surface.                                |
| Self-monitoring off-system         | The notification system can't be its own alerter when it's down.                         |

### Wrap-Up

| Aspect                          | Solution                                              |
| ------------------------------- | ----------------------------------------------------- |
| Channel-agnostic ingestion      | Single `POST /notifications`; Router fans out         |
| Per-channel rate limits         | Shared token bucket per provider; Kafka backpressure  |
| Reliable delivery               | At-least-once + idempotency keys at every hop         |
| User preferences strong on send | SQL source of truth + Redis cache + invalidation event|
| Quiet hours + caps              | Routing-time check, defer or coalesce                 |
| Marketing burst handling        | Bucket-sort campaigns into 5-min slots                |
| Provider failures               | Circuit breaker + channel fallback + always in-app inbox |
| Compliance (suppression)        | Global per-channel deny list, checked pre-send        |
| In-app inbox                    | Per-user durable list, written by channel worker      |
| Self-monitoring                 | Out-of-band pager; can't depend on itself             |
