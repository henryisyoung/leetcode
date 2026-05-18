# Design a Group Chat System — 60-min Interview Version

A messaging service that delivers messages to N members of a group in near-real-time, preserves order, supports read receipts and unread counts, and survives the existence of a 100K-member channel.

**Time budget**

| Minutes | What you do                              |
| ------- | ---------------------------------------- |
| 0–5     | Clarify requirements + capacity math     |
| 5–10    | API + connection model                   |
| 10–25   | Data model + high-level architecture     |
| 25–45   | Pick 2 deep dives                        |
| 45–55   | Scale + failure handling                 |
| 55–60   | Trade-offs and wrap-up                   |

---

## 0–5 min: Clarify

### Step 1 — Pin the topology and the worst case

> *"How big can a group get?"*

This single answer changes the architecture more than any other.

| Group size           | Architecture implication                                           |
| -------------------- | ------------------------------------------------------------------ |
| 1–1 DM               | Trivial fan-out                                                    |
| 10–100 (typical)     | Fan-out on write to each member's inbox                            |
| 1K–10K (Slack)       | Hybrid: fan-out + per-channel feed                                 |
| 100K+ (broadcast)    | Fan-out on **read**, not write                                     |

Say out loud: *"I'd target 1-1 and small groups with fan-out-on-write, and switch to fan-out-on-read for channels above ~1K members."* The interviewer will love that you named the bend.

### Step 2 — Functional requirements

- 1-1 DM and group chat (multi-member).
- Online presence (online / away / offline).
- Typing indicator.
- Read receipts ("seen by Alice 09:31").
- Per-channel unread count.
- Message history with infinite scroll.
- Push notification when offline.

Out of scope: voice / video, file uploads (handled by a separate object store + signed URL).

### Step 3 — Non-functional requirements

| Requirement              | Target                                          |
| ------------------------ | ----------------------------------------------- |
| Send latency (p99)       | < 200 ms from sender's tap to receiver's screen |
| Delivery guarantee       | At-least-once + dedup by client message id      |
| Message ordering         | Strict per-channel; not global                  |
| Durability               | Message persisted before ack                    |
| Availability             | 99.99% — outage = WhatsApp on the news          |

### Step 4 — Capacity math

```
1 B users, 50 messages/user/day      ≈ 50 B msg/day  ≈ 600 K msg/sec avg
Peak (3×)                            ≈ 2 M msg/sec
Avg message + metadata               ≈ 500 B
Storage: 50 B × 500 B = 25 TB/day, ~9 PB/yr
Fan-out: avg 5 members → 10 M deliveries/sec
WebSocket concurrent connections    ≈ 100 M (10% of users online)
```

> **Take-away:** the system has three independent scaling axes — **send throughput**, **fan-out amplification** (×N members), and **WebSocket connection count**. Each gets its own tier.

---

## 5–10 min: API + Connection Model

### Two transports, one service

| Transport      | Used for                                       |
| -------------- | ---------------------------------------------- |
| HTTPS REST     | Send message, fetch history, manage channels   |
| WebSocket      | Receive real-time pushes (delivery, typing, presence) |

Mobile clients keep an idle WebSocket to a **Gateway**. Sending a message goes over REST so a flaky uplink doesn't lose it on a half-open socket.

### Send

```http
POST /v1/channels/{id}/messages
Headers: Client-Message-Id: <uuid>     ← idempotency
{
  "body": "hello",
  "reply_to": null,
  "attachments": []
}
→ 201 { "message_id": "m_42", "server_ts": 1714770000123, "seq": 891 }
```

`Client-Message-Id` lets the server dedup retries; `seq` is the per-channel monotonic sequence.

### History (infinite scroll)

```http
GET /v1/channels/{id}/messages?before_seq=1000&limit=50
→ { "messages": [...], "next_cursor": "..." }
```

Keyset paginate by `seq`, never by offset.

### Read receipt

```http
POST /v1/channels/{id}/read
{ "last_seen_seq": 891 }
```

Stored as `(channel, user, last_seen_seq)`. Computing unread count is `current_seq - last_seen_seq` — O(1).

### WebSocket frames

```
S→C  {"type":"message", "channel":"c1", "seq":892, "body":"..."}
S→C  {"type":"typing", "channel":"c1", "user":"u_alice"}
S→C  {"type":"presence", "user":"u_alice", "status":"away"}
C→S  {"type":"ack", "channel":"c1", "seq":892}     // optional client ack for delivery receipts
```

---

## 10–25 min: Data Model + High-Level Architecture

### Tables (logical)

```
channels
├── channel_id (PK)
├── type            // dm | group | broadcast
├── created_at, owner

memberships
├── channel_id, user_id (composite PK)
├── role            // member | admin
├── joined_at, last_seen_seq

messages
├── channel_id (PK)             ← shard key
├── seq         (PK)            ← per-channel monotonic
├── message_id  (UNIQUE)
├── sender_id, body, server_ts
├── client_msg_id  (for dedup)
PARTITIONED BY channel_id, ORDERED BY seq

user_inbox                    // fan-out-on-write target (for small/medium channels)
├── user_id (PK)
├── channel_id, seq, server_ts
├── delivered, read
PARTITIONED BY user_id, ORDERED BY server_ts DESC

presence  (Redis, NOT durable)
├── user_id → {status, last_seen_ms, device_count}

channel_counter
├── channel_id → next_seq    // monotonic counter; Redis with persist
```

Storage: Cassandra (or DynamoDB) for `messages` (write-heavy, easy partitioning by channel_id), Redis for presence and channel_counter.

### Architecture

```
mobile/web ── WebSocket ──► Gateway (stateful, sticky)
            └─ HTTPS ──────► Send API ──┐
                                         ▼
                                   Message Svc
                                         │
                              ┌──────────┼──────────┐
                              ▼          ▼          ▼
                          messages   Kafka     channel_counter
                          (Cassandra) │         (Redis)
                                      │
                                      ▼
                              Fan-out Service
                                      │
                       ┌──────────────┼──────────────┐
                       ▼              ▼              ▼
                  user_inbox      Push Service   Search Indexer
                  (Cassandra)     (APNs/FCM)     (Elasticsearch)
                       │
                       ▼
                 Gateway WS push back to recipients
```

| Component         | Role                                                          |
| ----------------- | ------------------------------------------------------------- |
| Gateway           | Holds 100K+ WebSockets; routes by user_id                     |
| Send API          | REST entrypoint; idempotency dedup; persists + emits to Kafka |
| Message Svc       | Owns `messages` table; assigns `seq` via Redis counter        |
| Fan-out Service   | Reads Kafka, writes per-user inbox rows, pushes to Gateway    |
| Presence Svc      | Tracks online status; gossip across Gateway pods              |
| Push Svc          | Hits APNs/FCM for offline users                               |

### The Send Flow

```
1. Sender → POST /messages (Client-Message-Id idempotency)
2. Send API:
     - INSERT INTO messages (channel_id, seq=INCR(channel_counter), ...)
       (Cassandra: lightweight transaction or use channel_counter from Redis)
     - On dedup hit (Client-Message-Id seen): return existing row
     - PUBLISH to Kafka topic `chat.fanout` key=channel_id
3. Return 201 to sender immediately with {message_id, seq}
4. (async) Fan-out Service consumes:
     - Lookup memberships(channel_id) → list of user_ids
     - For each user_id:
         - INSERT user_inbox(user_id, channel_id, seq, …)
         - Lookup which Gateway holds this user's WebSocket → push frame
         - If user offline → enqueue push notification
```

> Step 3 returns in **<50 ms**; the fan-out happens asynchronously. Sender doesn't wait for delivery to N recipients.

### Why seq, not server_ts?

- Two messages sent in the same millisecond on different shards would tie on `server_ts`.
- Clients render strictly by `seq`; absolute time is for display only.
- Reading "all messages where seq > 891" is the unread query.

---

## 25–45 min: Deep Dives (pick 2)

### Deep Dive A: Fan-out — On Write vs On Read

#### Fan-out on write (the default for small/medium channels)

```
Send → INSERT messages → for each member: INSERT user_inbox + push
```

- **Pro:** read is trivial — `SELECT … FROM user_inbox WHERE user_id=$u`. Unread count is O(1).
- **Con:** a message to a 10K-member channel writes 10K inbox rows. A burst of broadcasts overwhelms the fan-out service.

#### Fan-out on read (the only option for huge channels)

```
Send → INSERT messages (only)
Read → JOIN memberships + messages WHERE channel_id IN (user's channels) AND seq > last_seen
```

- **Pro:** O(1) writes regardless of channel size. Broadcasts scale.
- **Con:** every client refresh scans across all the user's channels. Unread count = sum over channels of `(current_seq - last_seen_seq)` — still cheap if memberships are indexed.

#### Hybrid (what real systems ship)

| Channel size      | Strategy             | Why                                          |
| ----------------- | -------------------- | -------------------------------------------- |
| 1–1, small groups | Fan-out on write     | Cheap; per-user inbox is trivially fast      |
| 1K–10K            | Fan-out on write with bounded inbox depth | Trim inbox to last N entries; older messages reached via channel scan |
| 10K+              | Fan-out on read      | Avoid 10K writes per message                 |

**Tagging the channel** at create time (`type: broadcast`) tells the fan-out service which strategy to apply.

#### What about presence + typing?

Never write to `user_inbox`. Push directly through Gateway WebSocket. If recipient is offline, just drop — typing/presence is ephemeral.

### Deep Dive B: Ordering, Dedup, and the WebSocket

#### Per-channel ordering

`seq` from `channel_counter` (Redis INCR or a per-channel partition counter in Cassandra) guarantees a total order **per channel**. There is no global total order — that would require a single global lock, which doesn't scale and isn't needed (no client cares about cross-channel order).

#### Dedup on send

The client may retry the same POST after a 5xx. We dedup on `Client-Message-Id` (a uuid the client picks). The first INSERT wins; later attempts find the existing row and return it.

#### Dedup on receive

The WebSocket may push the same message twice (sender's own client gets it through both echo and inbox). Client tracks the last `seq` per channel and ignores duplicates.

#### Half-open WebSockets

```
Server pushes → TCP buffer fills → kernel doesn't deliver → no ack from client
Server thinks "delivered"; client thinks "no message"
```

Three defenses:
1. **App-layer ping/pong every 30 s** over WebSocket. No pong in 90 s → server closes the socket.
2. **Resume on reconnect:** client reconnects with `last_seen_seq`; server replays anything newer from `user_inbox`.
3. **Idempotent inbox row:** `(user_id, channel_id, seq)` is unique; a replay can't duplicate.

#### "Send timestamp drift"

Clients display `server_ts`, not `client_ts`. If you display `client_ts`, you'll see "11:59 PM" messages appearing above "12:01 AM" messages because clocks disagree.

### Deep Dive C: Presence + Typing at Scale

#### Presence model

```
Redis: presence:{user_id} → { status, last_heartbeat_ms, device_count }
TTL: 90 s — if no heartbeat in 90 s, user falls off → status=offline
```

WebSocket keepalive doubles as a heartbeat (every 30 s, refresh the TTL).

#### Who knows you're online?

Naive: push your status to every contact. Doesn't scale. (If you have 1000 contacts and 100M users online, that's 100B pushes/sec just for presence.)

**Pull on demand:** when a user opens a chat, the client GETs presence for participants. Only the small set of currently-rendered users requires updates, delivered via the same Gateway WebSocket.

#### Typing indicator

Pure ephemeral signal. Push directly through Gateway to recipients of the channel, never persist. Client auto-stops typing after a few seconds of idle.

> Persistence rule: messages → durable. Presence + typing → memory only, lose on restart.

### Deep Dive D: Push Notifications for Offline Users

When the fan-out service finds the recipient has no live WebSocket, it produces to a `push_notifications` Kafka topic. A Push Service consumes that and calls APNs / FCM:

```
push_notifications → Push Svc
  - dedupe (user already received via WebSocket since msg created? skip)
  - format per-platform payload
  - call APNs / FCM with collapse_id = channel_id (avoid badge spam)
  - track delivery; retry on transient failures
```

Critical design point: **wait a short grace period (~1 s)** before pushing. If the user is just about to open the app (and a WebSocket connects), skip the push — otherwise everyone gets phantom notifications.

---

## 45–55 min: Scale + Failure Handling

### Sharding

- `messages` partitioned by `channel_id` (and clustered by `seq`). All messages of one channel co-located → range scans are local.
- `user_inbox` partitioned by `user_id`. All inboxes per user co-located → "load my chats" is one partition read.
- Gateway pods sharded by `user_id % N`; service discovery routes a user's connection to the same pod (stickiness).
- `channel_counter` in Redis sharded by `channel_id`.

### Connection scale

WebSockets cost memory, not CPU. A modern Gateway pod handles ~50K connections; 100M connections → ~2000 pods. Use a connection-aware load balancer (consistent hashing on user_id) so reconnects land on the same pod when possible — preserves local state caches.

### Caching

| Layer  | What                                                | TTL                  |
| ------ | --------------------------------------------------- | -------------------- |
| Redis  | presence                                            | 90 s heartbeat       |
| Redis  | channel_counter                                     | persistent (RDB+AOF) |
| Redis  | last 50 messages per active channel                 | 5 min                |
| In-mem | Gateway-side routing table (user → pod)             | seconds              |

### Failure modes

| Failure                                  | What we do                                                |
| ---------------------------------------- | --------------------------------------------------------- |
| Gateway pod dies                         | Clients reconnect, replay `since last_seen_seq` from user_inbox |
| Cassandra hotspot on a chatty channel    | Time-bucket the partition key (`channel_id, day`); accept slightly more complex scans |
| Redis counter loses INCR (rare)          | Cassandra's atomic counter as backup; on restart, MAX(seq)+1 |
| Fan-out service lag                      | Show recipient's existing messages; new ones appear with delay; emit `inbox_lag_p99` |
| Kafka backed up                          | Apply backpressure to Send API (429); never drop messages |
| APNs / FCM down                          | Push Service retries with backoff; user gets bundled notification on next online |
| Half-net for one shard                   | Gateway evicts unreachable users → marked offline → push fallback kicks in |

### Spam, abuse, rate limits

- Per-user send rate limit (Redis sliding window).
- Per-channel send rate limit (esp. broadcasts).
- Spam classifier on the Send API path.
- All optional first-pass; mention they live as middleware.

---

## 55–60 min: Trade-offs / Common Mistakes

| Mistake                                                | Effect                                            | Fix                                                  |
| ------------------------------------------------------ | ------------------------------------------------- | ---------------------------------------------------- |
| One global message ordering                            | Single global lock; impossible at scale           | Per-channel `seq` only                               |
| Fan-out-on-write for 100K-member channels              | Cascading write storms                            | Fan-out-on-read above a threshold                    |
| WebSocket as the only delivery mechanism               | Lost messages when socket goes half-open          | REST for send; inbox persists; resume on reconnect   |
| Presence written to durable storage                    | Massive write amplification on every blink        | Redis with TTL; lose-on-restart is fine              |
| Push notification fires before grace period            | Phantom notifications when user is actively using | Wait 1–2 s; suppress if app went online              |
| Showing `client_ts` instead of `server_ts`             | Out-of-order display from clock drift             | Always render `server_ts`                            |
| No `Client-Message-Id` dedup                           | Retries → duplicate messages                      | Client uuid as idempotency key on Send               |
| Unread count = SELECT COUNT(*) WHERE …                 | O(messages) per refresh                           | `last_seen_seq` + subtract                           |
| Typing indicator persisted in DB                       | Insane write volume for ephemeral signal          | Push-only; never persist                             |
| No back-pressure on broadcast channels                 | One viral message takes the system down           | Per-channel rate limit at Send API                   |

### Key Concepts for the Interview

| Topic                                | What to say                                                                       |
| ------------------------------------ | --------------------------------------------------------------------------------- |
| Per-channel `seq` for ordering       | Total order per channel, no global lock. Cheap and correct.                       |
| Hybrid fan-out                       | Write for small, read for huge — switch is per-channel `type`                     |
| Idempotency on send AND receive      | `Client-Message-Id` server-side; client tracks `last_seen_seq` for de-dup         |
| Resume on reconnect                  | WebSockets are flaky; the inbox + `last_seen_seq` is the recovery primitive       |
| Presence is ephemeral                | Redis TTL, pull on demand, never durable                                          |
| Push grace period                    | Avoid phantom notifications by waiting before APNs/FCM                            |
| Decouple send latency from fan-out   | Sender gets 201 in <50 ms; recipients get pushed asynchronously                   |
| Server timestamps only               | Clients lie about time                                                            |

### Wrap-Up

| Aspect                          | Solution                                          |
| ------------------------------- | ------------------------------------------------- |
| Per-channel ordering            | `seq` from Redis counter; durable in messages row |
| Send latency                    | REST + Kafka outbox; 201 in <50 ms                |
| Real-time delivery              | WebSocket Gateway with sticky routing             |
| Recovery from socket drop       | `since last_seen_seq` replay from user_inbox      |
| Huge channels                   | Fan-out-on-read above 1K–10K members              |
| Presence + typing               | Redis with TTL; push-only, never durable          |
| Offline delivery                | APNs / FCM with grace period to avoid phantom     |
| Storage                         | Cassandra messages + Redis hot cache              |
| Connection scale                | ~50K WS/pod; consistent-hash routing on user_id   |
