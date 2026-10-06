# Design a Top-10 Leaderboard Service for Game Companies (Startup Version)

We are a startup. We sell a **backend-only** service: game companies call our API to submit scores and to show the **top 10** of a leaderboard. No frontend. Traffic is **small at the start**.

The interview has two halves:
1. **Build the smallest system that works.** No estimation — the interviewer already said "startup, small traffic". Cheap and simple wins. Over-engineering loses points.
2. **"Traffic is growing. What now?"** Find the bottleneck, fix it, explain why, repeat.

**Time budget**

| Minutes | What you do |
| --- | --- |
| 0–8 | Clarify features. Say what's out of scope |
| 8–15 | API + data model |
| 15–30 | Phase 1: one machine. Full write and read flow, empty board vs full board |
| 30–50 | Phase 2: scaling, one bottleneck at a time |
| 50–60 | Trade-offs, open questions |

> **Pick where to spend time.** If scaling is your strength, move fast through Phase 1 to leave room for it. If you go slowly in Phase 1, leave strong data points there, because Phase 2 will only get a high-level pass.

---

## 0–8 min: Clarify

> *"Let me confirm the scope first. I'll say what I'd assume; correct me if it's different."*

| Question | My default |
| --- | --- |
| Backend API only, no UI? | Yes. Customers build their own UI |
| Who are the customers? | Game companies. Each game has one or more leaderboards. Multi-customer isolation and billing are out of scope |
| Who calls "submit score"? | **The customer's game server**, not the player's phone. Otherwise players can fake scores. Anti-cheat is the customer's job, but we only accept server-to-server calls with an API key |
| **What is a score?** Best single score, or cumulative total? | **Ask this.** It decides the update rule: best score keeps `max(old, new)`, cumulative keeps `old + new`. Support both as a per-leaderboard setting |
| Can one player appear twice in the top 10? | Default no — one row per player |
| Tie-break? | Whoever reached the score first ranks higher |
| Do we need "my rank is 1,234"? | Default no — only top 10. But we store every player anyway, so it's easy to add |
| Will it become top 100 later? | Ask. It doesn't change the design: we keep all scores, so top 100 is the same query with `LIMIT 100` |
| Latency / freshness? | Top 10 can be a second or two stale. Submit should be fast (< 100 ms) |
| Consistency? | Eventual is fine for reads. A submitted score must never be lost after we return 200 |
| Time windows (daily / weekly)? | Out of scope for now; easy to add as a separate leaderboard ID |

**Out of scope (say it out loud):** frontend, anti-cheat logic, player accounts, payments and billing details.

> **No estimation.** *"You said startup and small traffic, so I'll skip the math and start with one machine. When something breaks, we add machines. I'll explain each step."*

---

## 8–15 min: API + data model

### API

All calls need `Authorization: Bearer <api_key>`, so only game servers can call us.

```
POST /v1/leaderboards/{leaderboard_id}/scores
  body: { "player_id": "p123", "score": 9800, "submission_id": "uuid" }
  → 200 { "player_id": "p123", "score": 9800 }   // the player's current best / total
  // submission_id makes retries safe (idempotent)

GET  /v1/leaderboards/{leaderboard_id}/top?limit=10
  → 200 { "entries": [ { "rank": 1, "player_id": "p9", "score": 12000, "at": "..." }, ... ] }

POST /v1/leaderboards            // customer creates a leaderboard for a game
  body: { "game_id": "g1", "name": "season-1", "sort_order": "desc", "score_mode": "best" }
```

The customer stores player display names on their side. We only store `player_id`, which keeps us out of user-data problems.

### Data model (Postgres)

```sql
leaderboards (leaderboard_id PK, game_id, name, sort_order, score_mode, created_at)
-- sort_order: 'desc' = higher wins (points), 'asc' = lower wins (lap time).
--   For 'asc' boards store -score, so one index (score DESC, reached_at ASC) serves both
--   and ties still go to whoever got there first.
-- score_mode: 'best' = GREATEST(old, new), 'cumulative' = old + new.

-- Every submission, append-only. Source of truth; lets us rebuild anything.
score_events (leaderboard_id, submission_id, player_id, score, created_at,
              PRIMARY KEY (leaderboard_id, submission_id))

-- One row per player: current best (or total). What we read from.
player_scores(leaderboard_id, player_id, score, reached_at,
              PRIMARY KEY (leaderboard_id, player_id))
INDEX (leaderboard_id, score DESC, reached_at ASC)
```

**Why keep everything:** storage is cheap and traffic is low, so there's nothing to save by throwing rows away. Keeping every player and every submission means top 100, "my rank", time-window boards, and rebuilding after a bug are all possible later without a migration. The index makes top 10 a short range read no matter how many players there are.

### Storage choice

| | Postgres (all scores + index) | Redis sorted set |
| --- | --- | --- |
| Top-10 read | Index range scan, `LIMIT 10` — reads ~10 index entries | `ZREVRANGE 0 9` |
| Write | One upsert + index update | `ZADD` / `ZINCRBY` |
| Exact rank of any player | `COUNT(*)` of higher scores — slow on very big boards | `ZREVRANK`, O(log n) |
| Durability | Built in | Needs persistence + replica, or rebuild from a DB |
| Ops cost for a startup | One system we already need | A second system to run |

> **My Phase 1 pick:** *Postgres only. One system, durable, and with the index the top-10 query stays cheap even with millions of players. Redis is the answer when we need exact rank for any player or the DB can't keep up — not on day one.*

---

## 15–30 min: Phase 1 — one machine

```
 Customer game servers
          │  HTTPS + API key
          ▼
 ┌─────────────────────────────┐
 │  One VM                     │
 │   API process (stateless)   │
 │   Postgres (same box)       │
 └─────────────────────────────┘
          + nightly backup to object storage (S3)
```

> *"One VM runs both the API and Postgres. It's cheap and easy to debug. The only thing I won't skip is backups: a nightly dump plus WAL archiving to S3, so a dead disk doesn't lose customer data. If data loss is a big worry, add one standby replica. It's not free, so I'd ask first."*

### Write flow

1. Check the API key is valid and the leaderboard exists.
2. In one transaction:
   - `INSERT INTO score_events ... ON CONFLICT (leaderboard_id, submission_id) DO NOTHING`. If nothing was inserted, it's a retry → return the current score and stop. This is what keeps a retry from adding points twice in cumulative mode.
   - Upsert the player's row:
     - **Best score:** `INSERT ... ON CONFLICT (leaderboard_id, player_id) DO UPDATE SET score = GREATEST(player_scores.score, EXCLUDED.score), reached_at = CASE WHEN EXCLUDED.score > player_scores.score THEN now() ELSE player_scores.reached_at END`
     - **Cumulative:** `... DO UPDATE SET score = player_scores.score + EXCLUDED.score, reached_at = now()`
3. Commit, return the player's current score.

No application-level locks needed: the upsert is atomic on the player's row, so two concurrent submits for the same player can't lose an update.

### Read flow

```sql
SELECT player_id, score FROM player_scores
WHERE leaderboard_id = ?
ORDER BY score DESC, reached_at ASC
LIMIT 10;
```

Served straight from the index — the DB walks about 10 index entries, it does not sort the table.

**Early vs later behavior — say this explicitly:**
- **Brand-new board:** no rows. Return `[]`, not an error. With 3 players, return 3 entries. Every submit creates a new player row.
- **Mature board:** many players, and the top 10 rarely changes. The read cost stays the same thanks to the index. Most submits update a player's row but don't touch the top 10 — that's what makes caching work well in Phase 2.

---

## 30–50 min: Phase 2 — traffic grows

Go one bottleneck at a time. **Name the bottleneck, then the fix, then the cost of the fix.**

**Step 1 — split the API and the DB.**
- API boxes: stateless (no data on the box, so any box can serve any request and can be added or killed freely). Light work — HTTPS, auth, JSON, then wait on the DB — so small, cheap general-purpose instances behind a load balancer.
- DB box: needs RAM (keep the index and hot rows in memory) and fast disk (writes + WAL) → a memory/storage-optimized instance.
- *Why:* they need different hardware, and paying for one big box that's good at both wastes money. Stateless APIs can also be deployed and restarted without downtime.

**Step 2 — high availability for the DB.** Primary + one synchronous standby, auto-failover. *Why now:* we have paying customers, and one box is a single point of failure.

**Step 3 — cache the top 10.** Reads (every player opening the game) far outnumber writes.
- Cache key: `top:{leaderboard_id}`, holding 10 entries — tiny.
- Invalidate only when a submit can change the top 10: the board has fewer than 10 players, or the player's new score ≥ the cached 10th score. On a mature board that's a small fraction of submits, so the hit rate stays high. Add a short TTL (1–2 s) as a safety net.
- Start with in-process cache; move to Redis when we have many API boxes and want them to agree.
- *Trade-off:* readers may be 1–2 s stale. We agreed that's fine.

**Step 4 — read replicas.** Only if cache misses still overload the primary. Usually the cache is enough for top-10, so say this is optional.

**Step 5 — shard only what grows.**
- `leaderboards` stays small. Don't shard it.
- `score_events` grows fastest — one row per submit — but nothing reads it on the hot path. Partition it by month and move old partitions to cheap object storage. No sharding needed.
- `player_scores` grows with players. Shard by `leaderboard_id`, so one board's top-10 query stays on one shard.

**Step 6 — one viral game (hot leaderboard).** Sharding by board doesn't help when one board gets all the writes.
- Put submits on a queue (Kafka / SQS) and have a worker apply them in batches. Absorbs spikes. Inside a batch, collapse submits for the same player first (keep the max, or sum them), so one hot player is one upsert, not hundreds. Cost: a little lag before a score appears.
- If one worker still can't keep up: split the board's rows across N shards by `hash(player_id) % N`. Reads take the top 10 from each shard and merge N × 10 entries — cheap, because **top-10 merges cleanly** (each player lives on one shard, so the global top 10 is always inside the union of the shards' top 10s). Exact rank for any player does not merge this easily; if that's ever required, that's when Redis sorted sets with score-range partitions earn their place.

**Step 7 — global customers.** Deploy regions close to players: reads from a local cache or replica, writes routed to the board's home region. Or shard boards by region if a game is region-locked. Cost: cross-region latency on writes, plus more ops work.

**Step 8 — huge traffic, a lot of event writes: what to do with `score_events`.**

*When:* only when one Postgres primary can't keep up with inserts, even with the queue and batching from Step 6. Before that, a monthly-partitioned Postgres table is fine.

*Key idea:* `score_events` does two jobs. Split them:
1. **Duplicate check** — stop a retry from adding points twice. Needs only recent ids (a retry comes seconds later, not months later).
2. **History log** — keep every submit for audit and replay. Write a lot, read almost never.

Neither job needs a relational table at huge scale.

```
Game server → API ──append──▶ Kafka topic "score-events" ──▶ Worker ──batch upsert──▶ player_scores (Postgres)
                              (partition by leaderboard_id)   │  1. drop duplicates
                                                              │  2. collapse per player
                                                              └──▶ S3 (Parquet, by day) — full history
```

- **Kafka is the event log.** Appending to Kafka is cheap, and it scales by adding partitions. The API returns OK after Kafka acks the write, so the event is safe. Partition by `leaderboard_id`. For a hot board, use `(leaderboard_id, player_id)`, so one player's events stay in order on one partition.
- **Duplicate check moves to the worker.**
  - **Best-score mode needs no check at all.** `max(old, new)` gives the same result if the same score comes twice.
  - **Cumulative mode:** the worker keeps the recent `submission_id`s per board (24 h TTL) and skips any it has seen. With Flink, this state is saved together with the Kafka offset, so a crash does not lose a score or count it twice. A simpler option is Redis `SET NX EX 86400`. The cost is a small window: if the worker crashes after the `SET` but before the upsert, that one score is lost.
- **History goes to S3**, not to a database. Kafka keeps about 7 days. A sink job writes the events to S3 as Parquet, split by day. It's cheap and query-able with Spark/Athena for audits. If `player_scores` gets corrupted by a bug, rebuild it by replaying from S3 + Kafka.
- **Use Cassandra / DynamoDB only if the product needs to read history online**, e.g. "show this player's last 50 scores" in the game. Table: `PRIMARY KEY ((leaderboard_id, player_id), created_at DESC)`. It's built for heavy writes, and this read pattern is known up front. Don't add it just because writes are heavy — Kafka + S3 already handles that.

| Option | Good at | Bad at |
| --- | --- | --- |
| Postgres, partitioned by month | Simple. Duplicate check and upsert in one transaction | One primary limits writes |
| Kafka + worker + S3 | Huge write volume, cheap history, replay | Score shows up a few seconds late. More systems to run |
| + Cassandra | Fast online "my score history" reads | One more database. Only if the product needs it |

*Cost:* the submit is async, so a score appears a few seconds later; we lose the single transaction, so duplicate checking needs care; and there are more systems to run. That's why this is the last step, not the first.

> *"At huge scale I stop treating `score_events` as a table. Kafka is the log, the worker does the duplicate check and batches upserts into `player_scores`, and S3 keeps the history cheaply. I'd add Cassandra only if players need to see their score history in the game."*

**Monitoring:** submit p99, top-10 read p99, cache hit rate, queue lag, QPS per leaderboard.

---

## Make it a discussion, not an exam

Ask for opinions in a way that invites a real conversation, not "is this right?":

- *"I'm keeping only Postgres for now, no Redis. The cost is that exact rank for any player gets slow on huge boards. Do your customers care about 'my rank', or only the top 10?"*
- *"For a hot game, I can either queue writes and accept a little lag, or split the board. Which would your customers notice more — lag, or complexity?"*
- *"I'm putting the cache in the API process for now. Do you see a problem with it at this stage?"*

**When the interviewer points out something you missed:** think for a moment, explain the reason back in your own words, then change the design. *"You're right — if the game server retries after a timeout, cumulative mode adds the points twice. I'll make `submission_id` unique in `score_events` and skip the upsert when it's a duplicate."* That is exactly the communication signal they want.

Things that are easy to miss — check them yourself:
- Retries creating a double submit (cumulative mode adds points twice) → `submission_id`.
- Best-score mode overwriting a high score with a lower one → `GREATEST`, and don't reset `reached_at`.
- The same player appearing twice in the top 10.
- Clients calling submit directly → cheating.
- The cache getting stale after a write.

---

## Does ByteByteGo's "Real-time Gaming Leaderboard" cover this?

**Mostly the scaling half. Not the shape of this interview.** Use it as reference material for Phase 2, not as the script.

**What it covers well:**
- The API shape, and why score updates must come from the game server, not the client.
- Why a plain `ORDER BY` over millions of rows is slow for computing rank.
- Redis sorted sets (`ZINCRBY`, `ZREVRANGE`, `ZREVRANK`), and the skip list behind them.
- Sharding Redis — fixed range vs hash partitions, scatter-gather for top 10.
- Read replicas and failover, rebuilding Redis from a MySQL score log.
- Tie-breaking by timestamp, the message-queue trade-off, serverless as an option.

**What it does not cover, and this interview tests:**

| Gap | Why it matters here |
| --- | --- |
| **Backend-only API for game companies** | The chapter is one game with its own users. Here we serve many games and many boards, called server-to-server with an API key |
| **Start tiny, then grow** | The chapter starts at 5M DAU with estimates. This interview says *skip the estimate, one box first*. The "one machine → split → cache → shard" story is closer to its chapter 1, "Scale from Zero to Millions of Users" |
| **Postgres is enough for top 10** | The chapter rejects SQL because computing **every user's rank** is slow. Top 10 alone is an index range read with `LIMIT 10` — cheap at any size. Know the difference, or you'll add Redis on day one for no reason |
| **Best vs cumulative score** | The chapter assumes +1 per win. Here it's a per-leaderboard setting, and it decides the upsert rule |
| **Empty board vs mature board** | Not discussed. New board: return fewer than 10. Mature board: the top 10 rarely changes, which is why caching works |
| **Caching top 10** | The chapter says caching doesn't work because data changes constantly. For **top 10 only**, a 10-entry cache that is invalidated only when a score beats #10, plus a 1-second TTL, works well. Be ready to explain the difference |
| **Cost-driven choices** | Different hardware for API vs DB, shard only what grows, archive old submissions, don't add Redis on day one. Not discussed |
| **Correctness details** | Idempotent retries via `submission_id`, best-score never going down. Not discussed |

> **Bottom line:** read it for Redis sorted sets and sharding. Bring your own Phase 1 story, multi-tenancy, and the "Postgres is enough for top 10" argument. Those are what this interview is really about.
