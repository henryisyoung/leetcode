# Design Subreddit Chat Rooms / Live Chat (like WhatsApp / WeChat)

Reported Reddit prompt: *design Reddit chat. ~1M DAU, ~500K topics (subreddit rooms), group chat only, no 1:1, text only. Talk about the presence service and why AP.* The interviewer wants a discussion, not a monologue.

Generic chat mechanics (per-channel `seq`, dedup, half-open WebSockets, push notifications) are covered in depth in [DesignGroupChat](../../airbnb/New2026/SystemDesign/DesignGroupChat.md). This doc focuses on what is **different for Reddit**: big public rooms, lots of lurkers, moderation, and presence at scale. The last section compares it with WhatsApp / WeChat.

**Time budget**

| Minutes | What you do |
| --- | --- |
| 0–7 | Clarify. Room types, sizes, what's out of scope |
| 7–12 | Rough numbers — find the real stress point |
| 12–20 | API + data model |
| 20–35 | Architecture + send/receive flow |
| 35–50 | Deep dives: fan-out to big rooms, presence (AP), moderation |
| 50–60 | Failure handling, trade-offs, WhatsApp comparison if asked |

---

## 0–7 min: Clarify

> *"Let me confirm what kind of chat this is. Reddit chat rooms are different from WhatsApp groups, and that changes the design."*

| Question | My default |
| --- | --- |
| Room type? | One or more public chat rooms per subreddit. Anyone who can see the subreddit can join |
| Room size? | **Heavy-tailed.** Most rooms have a few active people; a big subreddit room can have 50K+ members and 5K+ online during an event |
| 1:1 DMs? | Out of scope (prompt says group only) |
| Content? | Text only. No media, no voice |
| History? | Yes, scroll back. Newest first |
| Ordering? | Messages in one room must appear in the same order for everyone |
| Delivery guarantee? | At-least-once to the client, dedup on the client. A message we acked must never be lost |
| Read receipts per user? | **No** — meaningless in a 50K room. Only an unread count / "new messages" marker |
| Presence? | Yes: online count per room, online status for people you see in the room |
| Typing indicator? | Small rooms only. In big rooms it's noise |
| Moderation? | **Yes — Reddit-specific.** Mods can delete, ban, mute, slow mode. Automod and safety rules apply |
| Push notifications? | Mentions and replies only. Not every message in a big room |

**Out of scope:** media, voice/video, end-to-end encryption, search inside chat, bots.

---

## 7–12 min: Rough numbers

```
DAU                         1M
Peak concurrent online      ~20%   → ~200K open WebSockets
Messages per user per day   ~20    → 20M/day ≈ 230/s avg, ~2K/s peak
Message size                ~300 B → ~6 GB/day, ~2 TB/year raw. Small.
Rooms                       500K, but most are idle at any moment
Biggest room online         ~5K–50K viewers during a live event
```

> **Take-away:** writes and storage are small. **The stress point is fan-out in big rooms.** One message in a room with 20K viewers is 20K deliveries. At 10 messages/s in that room, that's 200K deliveries/s from one room. Everything in the design serves this.

---

## 12–20 min: API + data model

### Two transports

- **WebSocket** for live traffic: send message, receive messages, presence, typing.
- **REST** for everything else: join/leave, room list, history, moderation actions.

```
WebSocket frames (client → server)
  { "type": "send",   "room_id": "r1", "client_msg_id": "uuid", "text": "hi" }
  { "type": "open",   "room_id": "r1", "last_seq": 1041 }   // I'm looking at this room
  { "type": "close",  "room_id": "r1" }                      // I left the screen
  { "type": "heartbeat" }

WebSocket frames (server → client)
  { "type": "ack",     "client_msg_id": "uuid", "seq": 1042 }
  { "type": "message", "room_id": "r1", "seq": 1042, "user_id": "u9", "text": "hi", "ts": ... }
  { "type": "removed", "room_id": "r1", "seq": 1042 }        // mod / safety deleted it
  { "type": "presence","room_id": "r1", "online_count": 5231 }

REST
  POST   /v1/rooms/{room_id}/join
  POST   /v1/rooms/{room_id}/leave
  GET    /v1/rooms/{room_id}/messages?before_seq=1042&limit=50   // history
  GET    /v1/users/me/rooms                                       // joined rooms + unread counts
  POST   /v1/rooms/{room_id}/messages/{seq}/remove                // mod action
  POST   /v1/rooms/{room_id}/bans    { "user_id": "u9", "duration": "24h" }
```

`open` / `close` is the important Reddit-specific part: **we only stream live messages for rooms the user is looking at right now.** See Deep Dive A.

### Data model

```
rooms        (room_id PK, subreddit_id, name, settings{slow_mode_s, min_karma, ...}, created_at)
memberships  (user_id, room_id, role{member|mod}, joined_at, last_read_seq,
              PRIMARY KEY (user_id, room_id))            -- "my rooms" + unread
room_members (room_id, user_id, ...)                     -- reverse index, for mods / mentions
bans         (room_id, user_id, until, by_mod)

messages     (room_id, bucket, seq, user_id, text, created_at, status{ok|removed},
              PRIMARY KEY ((room_id, bucket), seq DESC))
```

- **`messages` in Cassandra** (or ScyllaDB). Write-heavy, append-only, read by "latest N in a room". Partition key `(room_id, bucket)`, clustering key `seq DESC`. `bucket` = a time window (e.g. a week), so one busy room doesn't make one partition grow forever.
- **`memberships` and `rooms` in Postgres.** Small, relational, needs transactions for joins/bans.
- **Unread count** = `room.latest_seq - membership.last_read_seq`. No per-message read rows.

---

## 20–35 min: Architecture

```
           Clients (web / iOS / Android)
                 │ WebSocket            │ REST
                 ▼                      ▼
        ┌────────────────┐      ┌──────────────┐
        │ Gateway fleet  │      │ API service  │  (join, history, mod actions)
        │ (holds sockets)│      └──────┬───────┘
        └───┬───────▲────┘             │
     send   │       │ deliver          │
            ▼       │                  ▼
        ┌────────────────┐     ┌──────────────┐     ┌───────────────┐
        │ Chat service   │────▶│  Cassandra   │     │  Postgres     │
        │ (stateless)    │     │  (messages)  │     │ (rooms, mbrs, │
        │ auth, bans,    │     └──────────────┘     │  bans)        │
        │ rate limit,    │                          └───────────────┘
        │ assign seq     │──publish──▶ Pub/Sub by room (Redis pub/sub / NATS)
        └───────┬────────┘                 │
                │                          └──▶ gateways subscribed to that room
                ▼
             Kafka (all messages) ──▶ Safety rules engine, push notifications,
                                      analytics, search indexing (later)

        Presence service (Redis, TTL keys)  ◀── heartbeats from gateways
```

**Components and why:**
- **Gateway:** the only stateful tier. Holds WebSockets, nothing else. ~50K–100K connections per box, so ~4–10 boxes for 200K. Kept thin so it rarely needs a deploy — every deploy drops connections.
- **Chat service:** stateless business logic. Checks bans, mute, slow mode, rate limit, then assigns `seq`, writes, publishes.
- **Pub/Sub by room:** a gateway subscribes to `room:{id}` only while at least one of its local users has that room **open**. Publish once per gateway, not once per user.
- **Kafka:** durable stream of every message for things that can be async — safety, push, analytics. Same pattern as Reddit's real-time safety rules engine.

### Send flow

1. Client sends `{room_id, client_msg_id, text}` over the socket.
2. Gateway forwards to chat service with `user_id` from the authenticated socket.
3. Chat service checks: member? banned/muted? slow mode? rate limit? Cheap sync spam checks (blocked links, flood).
4. **Assign `seq`** for the room. Atomic `INCR room_seq:{room_id}` in Redis, or route each room to an owner shard by `hash(room_id)` that hands out numbers in memory.
5. Write to Cassandra with `QUORUM`. Use `client_msg_id` to drop duplicates on retry.
6. Ack the sender with `seq`. **Ack only after the durable write** — that's the "never lose an acked message" promise.
7. Publish to `room:{id}` → each subscribed gateway pushes to its local viewers.
8. Produce to Kafka → safety rules engine, push for @mentions.

### Receive / reconnect flow

- Client opens a room: sends `open{room_id, last_seq}`. Gateway subscribes if needed; client fetches anything after `last_seq` from history, then gets live messages.
- **Gap detection:** the client sees `seq` 1040 then 1043 → it fetches 1041–1042 over REST. Pub/sub can drop messages; the `seq` makes that safe.
- Duplicates: the client keeps the last seen `seq` per room and ignores anything already shown.

---

## 35–50 min: Deep dives

### Deep Dive A — fan-out to big rooms (the main one)

**Problem:** 20K viewers × 10 messages/s = 200K deliveries/s from one room.

**1. Only deliver to people who are looking.** Most members of a big subreddit room aren't viewing it. Members who don't have it open get **no live stream** — only an unread badge, computed from `latest_seq - last_read_seq` when they open the app. This one decision often cuts fan-out by 10–100×.

**2. Fan out per gateway, not per user.** Publish one message to pub/sub. Each of ~10 gateways receives it once and writes it to its local sockets. The expensive part (many sockets) stays local to each box.

**3. Batch in hot rooms.** If a room is above N msgs/s, the gateway sends a batch every 100–200 ms instead of one frame per message. Fewer frames, same content, a small delay nobody notices.

**4. Product limits for really hot rooms.** Slow mode (one message per user per X seconds), minimum karma or account age to post, or a read-only "event" room for mods. Real systems all have these. In an interview, say it — product limits are part of system design.

**5. If one room is still too hot:** shard that room's pub/sub channel across several brokers. Readers stay ordered by `seq`, because ordering comes from the sequencer, not from pub/sub.

> **Fan-out on write vs. on read:** WhatsApp-style chat writes a copy into every member's inbox (fan-out on write). That's fine for 256-person groups and impossible for 50K-person rooms. Reddit rooms are **fan-out on read**: one copy per room, readers pull history and get a live stream only while viewing.

### Deep Dive B — presence, and why AP

**What we show:**
- Big rooms: an **online count** ("5.2K here"). Not a list.
- In the message list: a green dot for the people you see, fetched in a batch for visible users only.

**How it works:**
- Gateways send a heartbeat per connected user every ~30 s → `SET presence:{user_id} gateway_id EX 60`.
- Offline = the key expired. No explicit "I'm going offline" needed (apps get killed without notice).
- Room online count: each gateway counts its local viewers per room and reports every few seconds; a presence service sums them. Approximate, cheap, never a list of 50K users.

**Why AP (available + partition-tolerant, eventually consistent):**
- If presence is wrong for 30 s, nobody is harmed. If presence is unavailable, the whole chat UI looks broken.
- Presence changes are very frequent and worthless a minute later. Strong consistency would cost a lot for no benefit.
- So: Redis with TTLs, no replication guarantees, best-effort updates. If the presence store dies, show no dots and rebuild from the next round of heartbeats.

**Be precise that messages are different:** message **storage** must be durable (quorum write before ack) and ordered per room (single sequencer per room). The honest answer is *"AP for presence, typing and live delivery; durable and ordered for stored messages."* That distinction is what the interviewer is checking.

### Deep Dive C — moderation (the Reddit part)

- **Sync checks before accepting** (fast, on the send path): banned/muted, slow mode, rate limit, blocked domains, simple flood detection.
- **Async checks after accepting** (via Kafka): safety rules engine and ML text classifiers. If a message is removed later, write `status=removed` and broadcast a `removed{seq}` frame so clients hide it.
- **Mod tools:** remove message, ban/mute for a duration, slow mode, lock room. Mod actions go through the same send path, so they are ordered with normal messages.
- **Reports:** a user reports a message → moderation queue.
- **Trade-off:** sync checks add latency to every message; async checks let bad content be visible for a second or two. Keep the sync path to cheap checks only.

---

## 50–60 min: Failure handling and scale

| Failure | What happens | Mitigation |
| --- | --- | --- |
| Gateway crashes | Its users disconnect | Clients reconnect with exponential backoff **+ jitter** (avoid a thundering herd), resend `open{last_seq}`, fill gaps from history |
| Pub/sub drops a message | Some viewers miss it | Gap detection by `seq`, fetch from Cassandra |
| Chat service box dies | In-flight sends fail | Stateless; client retries with the same `client_msg_id` → deduped |
| Sequencer (Redis) fails over | Possible duplicate or skipped `seq` | Skips are fine (clients fetch and find nothing). Avoid duplicates: on failover, jump the counter forward from `max(seq)` in Cassandra |
| Cassandra node down | — | RF=3, quorum reads/writes keep working with one node down |
| Presence store down | No green dots | Show nothing; rebuilds from heartbeats in ~30 s |
| Deploying gateways | Every connection drops | Drain slowly: stop new connections, close old ones gradually over minutes |

**Scale notes:**
- Cassandra partitions by `(room_id, bucket)`; busy rooms spread across buckets over time.
- Gateways scale by connection count; chat services by message rate; they scale separately.
- Multi-region: put gateways close to users. Each room has a home region that owns its sequencer and writes; other regions forward sends and subscribe to that room's stream. Cost: cross-region latency for senders far from the room's home.

---

## Make it a discussion

- *"I'm only streaming to users who have the room open. The cost is that someone who opens the app sees a fetch first, then live. Is that OK for the product?"*
- *"For huge rooms I'd add slow mode. Is that acceptable here, or do you want me to solve it purely in infrastructure?"*
- *"I'm treating presence as AP and messages as durable. Do you see a case where presence must be exact?"*

**Common mistakes:**
- Fan-out on write to 50K inboxes.
- Per-user read receipts in big rooms.
- Saying "the whole system is AP" — stored messages can't be "maybe lost".
- Ordering by server timestamp across machines instead of a per-room `seq`.
- Putting business logic in the gateway, so every logic change drops all connections.

---

## WhatsApp / WeChat vs. Subreddit rooms

Same building blocks (gateways, WebSockets, pub/sub, durable store), very different trade-offs:

| | Subreddit chat rooms | WhatsApp / WeChat |
| --- | --- | --- |
| Main shape | Big public rooms, many lurkers | 1:1 and small private groups (WhatsApp max ~1K) |
| Fan-out | **On read**: one copy per room, live stream only to viewers | **On write**: a copy into each recipient's inbox / queue |
| Offline users | Read history when they come back; push only for mentions | **Store-and-forward**: server keeps the message until each device acks it, then (WhatsApp) deletes it |
| Receipts | Unread count only | Sent ✓ / delivered ✓✓ / read (blue) per recipient |
| Encryption | Server sees plain text (needed for moderation) | **End-to-end encrypted** (Signal protocol). Server only stores ciphertext and can't moderate content |
| Moderation | Core feature: mods, automod, safety rules | Mostly user reports and metadata/behavior signals |
| Multi-device | Simple — every device reads the same room history | Hard with E2E: each device has its own keys; messages are encrypted per device |
| Presence | Online count + dots for visible users | "Last seen" / online per contact, with privacy settings |
| Push | Mentions/replies only | Every message when the app is in the background (APNs / FCM) |

**If the interviewer switches to "design WhatsApp":**
1. Per-user inbox (message queue per device) instead of per-room history.
2. Delivery receipts as normal messages going back to the sender.
3. Server stores messages only until delivered (WhatsApp) or keeps history server-side (WeChat).
4. Groups are small, so fan-out on write is fine: for each member device, write to their inbox and push.
5. E2E encryption means the server is a blind router. Say clearly that this rules out server-side content moderation — that is the real product trade-off between the two designs.
