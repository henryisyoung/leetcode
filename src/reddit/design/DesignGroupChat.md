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
| 1–1 DM               | Fan-out on write to each member's inbox                            |
| 10–1K (typical)      | Fan-out on write to each member's inbox + live push to online      |
| 1K–10K (Slack)       | No inbox rows; push live only to members who have the channel open |
| 100K+ (broadcast)    | No inbox rows, no live push to most members — badge only, pull on open |

Say out loud: *"Every message is stored once in the channel's partition. Small channels also get a per-user inbox row (fan-out on write), so a phone syncs all its chats with one query. Huge channels skip the inbox (fan-out on read), because 100K rows per message is too much."* The interviewer will love that you named the bend.

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
Fan-out: avg 5 members → 10 M deliveries/sec (= 10 M user_inbox writes/sec)
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
GET /v1/channels/{id}/messages?before_seq=1000&limit=50    ← scroll up (older)
GET /v1/channels/{id}/messages?after_seq=891&limit=200     ← catch up / fill a gap (newer)
→ { "messages": [...], "next_cursor": "..." }

GET /v1/me/channels                                        ← chat list with unread counts
→ { "channels": [ { "channel_id": "c1", "latest_seq": 900, "unread": 9 }, ... ] }

GET /v1/me/inbox?since={server_ts}&limit=500               ← sync all small chats in one call
→ { "events": [ { "channel_id": "c1", "seq": 901, "kind": "new", "body": "..." }, ... ],
    "next_cursor": "..." }
```

Keyset paginate by `seq`, never by offset.

### Read receipt

```http
POST /v1/channels/{id}/read
{ "last_seen_seq": 891 }
```

Stored in `read_cursors` as `(user, channel, last_seen_seq)`. Computing unread count is `current_seq - last_seen_seq` — O(1).

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
├── joined_at                   // rarely changes

read_cursors                    // changes often, no transaction needed → Redis + Cassandra
├── user_id (partition key), channel_id (clustering key)
├── last_seen_seq               // only moves forward: max(old, new)

messages
├── channel_id (PK)             ← shard key
├── seq         (PK)            ← per-channel monotonic
├── message_id  (UNIQUE)
├── sender_id, body, server_ts
├── client_msg_id  (for dedup)
PARTITIONED BY channel_id, ORDERED BY seq

presence  (Redis, NOT durable)
├── user_id → {status, last_seen_ms, device_count}

channel_counter
├── channel_id → next_seq    // monotonic counter; Redis with persist

channel_activity  (Redis, rebuildable from messages)
├── channel_id → {last_seq, last_ts, preview}   // for the chat list: sort + unread badge

user_inbox                      // fan-out on write, only channels with ≤ 1K members
├── user_id (partition key)
├── server_ts, channel_id, seq (clustering key, ASC)  // same key on replay → upsert, no dup
├── message_id, sender_id, body                       // small copy, so sync needs no 2nd read
├── kind            // new | edit | delete
TTL 30 days
```

A message is written once to `messages` (the source of truth). For small channels, the fan-out service also writes one `user_inbox` row per member. The inbox is only a **sync log**: "what's new for me, across all my chats, since X". Unread counts still come from `last_seq − last_seen_seq`, and the chat list still comes from `memberships` + `channel_activity`.

Storage: Cassandra (or DynamoDB) for `messages`, `user_inbox` and `read_cursors` (write-heavy, easy partitioning), Redis for presence, channel_counter and channel_activity.

### Architecture

```
mobile/web ── WebSocket ──► Gateway (stateful, sticky) ── connect / heartbeat / disconnect ──► Presence Svc
            │                                                                                (Redis, TTL)
            │                                                         user → {status, gateway_id}  ▲
            │                                                                                      │ lookup
            └─ HTTPS ──────► Send API ──┐                                         (from Fan-out)  │
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
               ┌──────────────┬───────┴──────┬──────────────┐
               ▼              ▼              ▼              ▼
          user_inbox      Gateway WS     Push Service   Search Indexer
          (Cassandra,     push (online)  (APNs/FCM,     (Elasticsearch)
           ≤1K members)                   offline)
```

| Component         | Role                                                          |
| ----------------- | ------------------------------------------------------------- |
| Gateway           | Holds 100K+ WebSockets; routes by user_id                     |
| Send API          | REST entrypoint; idempotency dedup; persists + emits to Kafka |
| Message Svc       | Owns `messages` table; assigns `seq` via Redis counter        |
| Fan-out Service   | Reads Kafka, writes `user_inbox` rows (small channels only), pushes to Gateways (online) or Push Svc (offline) |
| Presence Svc      | Written by Gateway on connect/heartbeat/disconnect; read by Fan-out to route (which Gateway) or fall back to push (offline) |
| Push Svc          | Hits APNs/FCM for offline users                               |

### The Send Flow

```
1. Sender → POST /messages (Client-Message-Id idempotency)
2. Send API:
     - INSERT INTO messages (channel_id, seq=INCR(channel_counter), ...)
       (Cassandra: lightweight transaction or use channel_counter from Redis)
     - On dedup hit (Client-Message-Id seen): return existing row
     - HSET channel_activity:{channel_id} last_seq, last_ts, preview
     - PUBLISH to Kafka topic `chat.fanout` key=channel_id
3. Return 201 to sender immediately with {message_id, seq}
4. (async) Fan-out Service consumes:
     - Lookup memberships(channel_id) → list of user_ids
     - Members ≤ 1K → INSERT user_inbox for each member (batched by user partition)
       Members > 1K → skip; those users read the channel directly
     - Ask Presence Svc: online? which Gateway holds the socket? (batched, one call per channel)
     - Online  → send frame to that Gateway → it pushes over the WebSocket
     - Offline → enqueue push notification
     - A missed push is fine: the client fills it from user_inbox (or `messages` by seq).
```

> Step 3 returns in **<50 ms**; the fan-out happens asynchronously. Sender doesn't wait for delivery to N recipients. The sender's path is **one** database write; the 500 inbox rows for a 500-member group are written later by the fan-out service.

### The Read Flow

Four moments, from opening the app to marking a channel read.

```
A. Open the app → chat list with unread badges
   Client → GET /v1/me/channels
   Server:
     - memberships WHERE user_id = me            → my channels
     - read_cursors WHERE user_id = me           → last_seen_seq per channel (one partition)
     - channel_activity for each channel (Redis pipeline, one round trip) → last_seq, last_ts, preview
     - unread = last_seq − last_seen_seq          → O(1) per channel, no row counting
     - sort by last_ts, return
   (Users in thousands of channels: keep a small per-user "recent channels" list,
    one row per channel — not per message — and only load the top 50.)

B. Open a channel → first page of history
   Client → GET /v1/channels/{id}/messages?limit=50           (newest page)
   Server → SELECT … FROM messages WHERE channel_id = ? ORDER BY seq DESC LIMIT 50
            → one partition, already sorted by seq, no scan
   Scroll up → ?before_seq={oldest seq on screen}             (keyset pagination)
   Client renders by seq and remembers max_seq it has shown.

C. Channel is open → live messages
   Fan-out (send flow step 4) → Gateway → S→C {"type":"message", "seq":892, …}
   Client:
     - seq == max_seq + 1 → append
     - seq <= max_seq     → duplicate, drop
     - seq >  max_seq + 1 → gap (a push was lost) → GET …/messages?after_seq={max_seq}
   Optional C→S {"type":"ack", "seq":892} → marks delivered (✓✓) for the sender

D. Reconnect after a socket drop / app was in background
   Client reconnects WebSocket, then:
     1. Small channels (all of them, one query):
        GET /v1/me/inbox?since={inbox_cursor − 60 s}
        → SELECT … FROM user_inbox WHERE user_id = me AND server_ts > ? LIMIT 500
     2. Huge channels (few, only open or recently viewed):
        GET /v1/channels/{id}/messages?after_seq={max_seq}
   Client drops anything with seq <= max_seq for that channel.
   Then live pushes resume as in C. The seq makes this exact: nothing missed, nothing doubled.
   (The 60 s overlap covers inbox rows that land late because of fan-out lag.
    Cursor older than the 30-day TTL → fall back to per-channel fetch.)

E. User has seen the messages → mark read
   Client → POST /v1/channels/{id}/read { "last_seen_seq": 892 }      (debounced, ~1/s)
   Server:
     - Redis: set read:{user}:{channel} = max(current, 892) (small Lua script) ← never moves back
     - Every few seconds, flush changed cursors to read_cursors (Cassandra) in a batch
     - unread badge on my other devices updates via S→C frame
     - small channels: emit a read receipt to the senders ("read by Bob")
```

**Why reads are cheap:** every read is either one partition of `messages` (by `channel_id`, ordered by `seq`) or a counter subtraction. Nothing scans or counts rows. Writes pay for the ordering (`seq` from the counter); reads get it for free.

**Why `read_cursors` is split from `memberships`:** a cursor is written when a user reads (about once per second per active reader), not once per message. It also doesn't need a transaction — losing a few seconds only makes an unread badge come back. So it goes to Redis (fast `max()` update) and is flushed to Cassandra in batches, while `memberships` stays a row that rarely changes. Cassandra alone would pick "last write wins", not "largest value wins", so two devices sending `892` then `880` out of order would move the cursor back. The `max()` in Redis prevents that.

**`seq` is different:** it lives in Redis too, but it must never go backward, or two messages get the same `seq`. After a Redis failover, set the counter to `MAX(seq)` from `messages` + 1 before accepting writes.

**Cache:** the newest ~50 messages of hot channels live in Redis (`recent:{channel_id}`), because almost every open (B) asks for the newest page. Older pages go to Cassandra.

### Why seq, not server_ts?

- Two messages sent in the same millisecond on different shards would tie on `server_ts`.
- Clients render strictly by `seq`; absolute time is for display only.
- Reading "all messages where seq > 891" is the unread query.

---

## 25–45 min: Deep Dives (pick 2)

### Deep Dive A: Fan-out — Storage vs Live Push

Split "fan-out" into two questions. They have different answers.

#### 1. Storage: hybrid — inbox for small channels, read by channel for huge ones

```
Small (≤ 1K):  Send → INSERT messages (1 row) → fan-out: INSERT user_inbox (N rows)
               Sync → user_inbox WHERE user_id = me AND server_ts > cursor   ← 1 query, all chats
Huge (> 1K):   Send → INSERT messages (1 row), nothing per user
               Read → messages WHERE channel_id = ? AND seq > max_seq        ← 1 query per channel
```

| | Fan-out on write (`user_inbox`) | Fan-out on read (`messages` only) |
|---|---|---|
| Writes per message | N (one per member) | 1 |
| Phone sync after being offline | 1 query for all chats | 1 query per channel |
| Good for | DMs, small groups (most traffic) | Channels with 1K–100K+ members |

- **Why have an inbox at all?** A typical user is in 50–200 small chats. Without it, waking up the phone means 200 queries ("anything new in c1? c2? …"). With it, one partition read returns everything new, in order.
- **Why not for huge channels?** 100K inbox rows per message is a write storm, and most members never open the message. Those users are in only a few huge channels, so a few per-channel queries are cheap.
- **Cost:** avg 5 members → about 10 M inbox writes/sec at peak. Cassandra handles that with enough nodes (writes are cheap appends); TTL 30 days keeps the table small.
- **Edits / deletes:** the inbox holds a copy, so it can go stale. Write an inbox row with `kind = edit | delete` for each member (same fan-out). `messages` stays the source of truth; history pages always read from it.
- **Channel grows past 1K:** flip the channel to "no inbox" and tell clients (a flag on the channel). Members then sync it by `after_seq` like any huge channel.

#### 2. Live push: who gets a WebSocket frame (depends on size)

| Channel size      | Live push to                         | Why                                          |
| ----------------- | ------------------------------------ | -------------------------------------------- |
| 1–1, small groups | Every online member                  | Cheap; everyone expects instant delivery     |
| 1K–10K            | Members who have the channel open    | Most members aren't looking; badge is enough |
| 10K+ (broadcast)  | Only active viewers; others get a badge on next app open | Avoid 10K pushes per message |

**Tagging the channel** at create time (`type: broadcast`) tells the fan-out service which rule to apply.

#### What about presence + typing?

Never persist. Push directly through Gateway WebSocket. If recipient is offline, just drop — typing/presence is ephemeral.

### Deep Dive B: Ordering, Dedup, and the WebSocket

#### Per-channel ordering

`seq` from `channel_counter` (Redis INCR or a per-channel partition counter in Cassandra) guarantees a total order **per channel**. There is no global total order — that would require a single global lock, which doesn't scale and isn't needed (no client cares about cross-channel order).

#### Dedup on send

The client may retry the same POST after a 5xx. We dedup on `Client-Message-Id` (a uuid the client picks). The first INSERT wins; later attempts find the existing row and return it.

#### Dedup on receive

The client may get the same message twice (a live push plus a gap-fill fetch, or the sender's own echo). Client tracks the last `seq` per channel and ignores duplicates.

#### Half-open WebSockets

```
Server pushes → TCP buffer fills → kernel doesn't deliver → no ack from client
Server thinks "delivered"; client thinks "no message"
```

Three defenses:
1. **App-layer ping/pong every 30 s** over WebSocket. No pong in 90 s → server closes the socket.
2. **Resume on reconnect:** client reads `user_inbox` since its cursor (all small chats at once), and `messages?after_seq={max_seq}` for each huge channel it has open.
3. **Dedup by seq:** `(channel_id, seq)` is unique; a refetch can't create duplicates on screen.

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
- `user_inbox` partitioned by `user_id`. All my new messages co-located → "sync my chats" is one partition read.
- `read_cursors` partitioned by `user_id`. All my cursors co-located → "load my chats" reads one partition + one Redis round trip for `channel_activity`.
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
| Gateway pod dies                         | Clients reconnect, sync from `user_inbox` (small) + `after_seq` (huge channels) |
| Cassandra hotspot on a chatty channel    | Time-bucket the partition key (`channel_id, day`); accept slightly more complex scans |
| Redis counter loses INCR (rare)          | Cassandra's atomic counter as backup; on restart, MAX(seq)+1 |
| Fan-out service lag                      | Pushes and inbox rows arrive late, but messages are already stored; opening a channel reads them directly. Inbox sync overlaps 60 s. Emit `fanout_lag_p99` |
| Redis loses `channel_activity`           | Rebuild per channel from the newest row in `messages`; badges are briefly off |
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
| Inbox rows for huge channels too                       | 100K writes per message; write storms             | Inbox only ≤ 1K members; huge channels read by channel |
| WebSocket as the only delivery mechanism               | Lost messages when socket goes half-open          | REST for send; inbox + messages persist; sync on reconnect |
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
| Hybrid fan-out by size               | Inbox (write) for ≤ 1K members, read by channel for huge; live push to viewers only above 1K |
| Idempotency on send AND receive      | `Client-Message-Id` server-side; client tracks `last_seen_seq` for de-dup         |
| Resume on reconnect                  | WebSockets are flaky; inbox cursor (+ `after_seq` for huge channels) is the recovery primitive |
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
| Recovery from socket drop       | `user_inbox` since cursor; `after_seq` for huge   |
| Huge channels                   | No inbox rows; live push only to active viewers   |
| Presence + typing               | Redis with TTL; push-only, never durable          |
| Offline delivery                | APNs / FCM with grace period to avoid phantom     |
| Storage                         | Cassandra messages + user_inbox; Redis hot cache  |
| Connection scale                | ~50K WS/pod; consistent-hash routing on user_id   |
