# Design Historic Hot Posts (Top-K)

Show the most popular Reddit posts for any past calendar day. The user picks a date `YYYY-MM-DD` and sees that day's trending posts.

Two sources, used for different parts:

- [System Design Interview — Top K Problem (Heavy Hitters)](https://www.youtube.com/watch?v=kx-XDoPjoHw) is the pipeline: a stream of events, precomputed top-k, and why merging short top-k lists is not an exact longer list. Count-min sketch, partitioned exact counts, and a batch job all come from here.
- [Design a historic 'hot' posts page](https://www.hellointerview.com/community/questions/historic-hot-posts/cm7t9ars700023b6rgm7pusr2?company=Reddit&level=SENIOR_MANAGER) is the product: one closed day, queried by date, years of history. That fixed window is what makes the main read a lookup instead of a range merge.

**Time budget**

| Minutes | What you do |
| --- | --- |
| 0–8 | Clarify. What "hot on that day" means, and what is out of scope |
| 8–15 | API + data model. One row per closed day |
| 15–30 | One machine: vote log, daily counts, freeze top 100 at midnight |
| 30–50 | Scale: partition counts, viral posts, today vs a closed day, arbitrary ranges |
| 50–60 | Trade-offs. Exact vs approximate, lambda vs one path |

---

## 0–8 min: Clarify

> *"The page is a date picker, not a live feed. I'll assume one global list per UTC day, ranked by score gained that day. Tell me if you want per-subreddit or Reddit's hot formula instead."*

| Question | My default |
| --- | --- |
| What is the query? | `GET /hot?date=YYYY-MM-DD`. One calendar day, UTC. The main query is never an arbitrary start/end |
| What does "hot that day" mean? | **Net score gained during that day** (upvotes minus downvotes whose event time falls on that date). A post submitted yesterday can still trend today |
| Posts submitted that day, ranked by lifetime score? | Different product. Ask. Lifetime score needs a different cutoff, because votes keep arriving for years |
| Reddit hot formula (log score + age)? | That ranks "what should be on the front page right now." For a closed day, net score gained is the clearer definition. Mention the formula if they want "what r/all looked like" |
| k? | 100. Small and fixed. The video's point: k in the tens of thousands makes the merge and the storage expensive |
| Global or per subreddit? | **Global** for the first design. Per subreddit is the same daily job with a second key; say how, don't build it until they ask |
| Today, still in progress? | Yes. Show a list that can be a few seconds stale. Yesterday and older are frozen |
| One user, one vote? | Yes. A vote, an unvote, and a vote flip are updates, not extra +1 events |
| Removed / spam / NSFW? | Drop them at read time. Freeze a longer list (top 500) so a later removal doesn't leave a hole in the top 100 |
| How far back? | Years. The frozen lists are tiny. The vote log is the thing that grows |
| Freshness of a closed day? | Immutable after a short late-vote window (for example, recompute yesterday once more at 02:00 UTC) |
| Personalized? | No. Same list for every viewer of that date |

**Out of scope:** the live front page, home-feed personalization, comment ranking, search, notifications.

> *"If the interviewer turns this into 'top posts between any two timestamps,' that is the video's full problem. I'll do the day page first, then show what breaks when the window is arbitrary."*

---

## 8–15 min: API + data model

### API

```
GET /v1/hot?date=2019-05-01&limit=100
  → 200 {
      "date": "2019-05-01",
      "frozen": true,                  // false when date is today
      "entries": [
        { "rank": 1, "post_id": "t3_abc", "score_gained": 48210,
          "subreddit": "pics", "title": "...", "created_at": "..." }
      ]
    }

GET /v1/hot?date=2019-05-01&subreddit=pics&limit=100
  // same shape, only if we materialized that subreddit (see deep dive)
```

Post titles and bodies live in the existing post service. This system stores ids, the score gained that day, and enough fields to render a card. A missing date (before Reddit, or a future date) returns an empty list.

### Data model

```sql
-- Append-only. Source of truth. One row per vote change.
vote_events (
  event_id      PK,
  post_id,
  user_id,
  subreddit_id,
  delta         INT,          -- +1, -1, or +2/-2 when a vote flips
  event_time,
  day           DATE          -- UTC date of event_time, so the daily job is a partition prune
)
-- Partitioned by day. Idempotent on event_id.

-- Exact per-post total for one day. Rebuilt from vote_events. Dropped or archived after the day is frozen.
daily_counts (
  day, post_id, subreddit_id, score_gained,
  PRIMARY KEY (day, post_id)
)

-- What the API reads. Written once when the day closes.
daily_hot (
  day, rank, post_id, subreddit_id, score_gained,
  PRIMARY KEY (day, rank)
)
-- Also store ranks 101..500 so a later removal still leaves 100 servable posts.
```

**Why a delta and not a raw upvote count.** The video's events are "video A was viewed," and every event is +1 forever. A Reddit vote can be undone. If we counted every upvote as +1, a user who upvotes and then unvotes the same day would still add 1. `delta` is the change from that user's previous vote: none→up is +1, up→down is −2, up→none is −1. Applying deltas in event order yields the net score gained that day.

**Why keep the vote log.** The frozen top 100 cannot be rebuilt from itself. A bug in the ranking job, a spam wave taken down the next day, or a request for "top 1000 of that day" all need the log.

---

## 15–30 min: One machine

```
 vote service (already exists)
        │  vote_events, one row per change
        ▼
 ┌──────────────────────────────────┐
 │  One box                         │
 │   API                            │
 │   Postgres                       │
 │     vote_events  (partitioned)   │
 │     daily_counts                 │
 │     daily_hot                    │
 └──────────────────────────────────┘
```

### Write

The vote service already decided the user's new vote. It appends one `vote_events` row with the delta. This page does not sit on the vote path. A slow ranking job must not slow down voting.

### During the day

A job every few seconds:

```sql
SELECT post_id, SUM(delta) AS score_gained
FROM vote_events
WHERE day = CURRENT_DATE
GROUP BY post_id
ORDER BY score_gained DESC
LIMIT 500;
```

Upsert those rows into `daily_hot` with `frozen = false`. Readers of "today" hit this table. They can be a few seconds behind the vote log. That matches the product: the historic page is not the live front page.

On one machine this query is fine while the day's vote log still fits in memory and the box has spare CPU. It is the first thing that breaks, and that is the point of Phase 2.

### At day close

At 00:05 UTC, run the same aggregation for yesterday, write `daily_hot` for that date, set `frozen = true`. Run it once more at 02:00 UTC to pick up late events (retries, clock skew, a brief outage of the vote publisher). After that, the row is immutable.

A closed day is a primary-key read of about 100 rows. Cache it at the edge. It does not change, so the TTL can be hours and a stampede only happens the first time someone opens an obscure date.

**Early vs later behavior:**
- **Today, morning:** few posts, return as many as we have.
- **A closed day:** always the frozen 100, minus any that have since been removed.
- **A removed post:** skip it and take the next stored rank. This is why we freeze 500, not 100.

---

## The exactness trap (this is the video)

A top-k list is not a summary you can add up.

k = 1. Each minute a different post gets 1,000 votes. Post S gets 30 votes every minute and is never that minute's #1.

| Window | Top 1 if we only kept each minute's winner | True top 1 |
| --- | --- | --- |
| Any single minute | the post that got 1,000 | that same post |
| The whole hour | still those 60 posts, S never appears | **S with 1,800** |

So:

- The API for a **closed day** must be computed from the full day's counts, once. It must not be 1,440 merged one-minute lists.
- The API for **today** must be computed from running totals, not from merging the last N minute-level lists.
- Merging two frozen days to answer "May 1 through May 2" has the same bug. A post that was #101 both days can outrank a post that was #1 on only one of them.

That last case is the follow-up. The date picker never asks it. Say the limitation out loud before they do.

---

## 30–50 min: Scale

Go one bottleneck at a time.

**Step 1 — the day's `GROUP BY` stops fitting.**
Tens of thousands of votes per second, and millions of posts touched in a day. One `SUM` over the raw log every few seconds becomes the bottleneck.

Partition the **counts**, not the query. `hash(post_id) % N` picks a partition. Each partition holds the running `score_gained` for its posts for the open day. A vote event with delta d is added to that one counter. Each partition keeps a heap of its own top 500. A coordinator merges N heaps of 500 into the global top 500 and writes `daily_hot`.

This merge is exact. Every post lives on one partition, so the global top 500 is inside the union of the local top 500s. The same fact as the leaderboard's "top 10 merges cleanly." It stops being exact the moment a post's votes are spread across partitions, or we throw away anything below the local top 500 and later ask for a wider window.

**Step 2 — one viral post.**
All of its votes hash to one partition. Split only that post: `hash(post_id, user_id) % M` counter shards, sum them when refreshing the heap. Do this for posts that cross a rate threshold, not for every post. Cost: M counter increments and a sum at flush time, for a handful of posts.

**Step 3 — freeze from the counters, and keep the log for rebuilds.**
At midnight each partition flushes its full `daily_counts` (every post it saw, not just the top 500) to columnar files partitioned by day — S3/Parquet is enough. The top-500 job reads those files, writes `daily_hot`, and the counters for that day can be dropped. The vote log stays, partitioned by day, and moves to cheap storage after the late window. Rebuilding a day is a batch job over one day's files.

**Step 4 — reads.**
Closed days: CDN cache of the JSON, keyed by date. The origin is a point read.
Today: the same cache with a TTL of a few seconds, filled by the coordinator. Readers never touch the counters.

**Step 5 — per subreddit, if they ask.**
Each post belongs to one subreddit, and that id is on the counter. The midnight job groups `daily_counts` by `subreddit_id` and writes `daily_hot_subreddit (day, subreddit_id, rank, post_id, score_gained)` for subreddits above a minimum volume. Small subreddits are answered from the same files on demand and then cached. The vote path does not change.

**Step 6 — arbitrary range, if they ask.**
This is the video's retrieval section. Three answers, in the order I'd offer them:

| They want | What we do |
| --- | --- |
| Exact, and they can wait | Batch job over `daily_counts` (or the vote log) for those days. Same MapReduce shape as the video: map to `(post_id, delta)`, reduce by sum, then a second job finds top k. Minutes, not milliseconds |
| Exact, and the range is "last 7 closed days," predeclared | Materialize it at midnight the same way we materialize one day. A fixed set of windows is cheap. An arbitrary window is not |
| Approximate, in tens of milliseconds | Count-min sketch per day. A sketch is a small grid, a few rows of hash functions by a few thousand columns. A vote increments one cell per row. The estimate is the minimum across the rows, so collisions only overestimate. Sketches for several days merge by adding cell by cell. The sketch does not store post ids, so we still keep a candidate heap of posts we have actually seen and query the sketch for those ids |

I would not ship the sketch for the date-picker API. The closed day is exact and already precomputed. The sketch is the answer when they insist on arbitrary ranges at read time and will accept a wrong order at the bottom of the list.

**What I would not build first.** The video's full lambda setup: an approximate stream path and an exact batch path, stitched at query time. Jay Kreps's objection still holds — two pipelines that must agree. Here the product is one aligned window, so one pipeline is enough: partitioned counters while the day is open, one freeze when it closes. Lambda comes back only for "exact history and an approximate last five minutes" as two products.

---

## Failure and correctness

- **Duplicate vote events.** `event_id` is unique. A retry does not apply the delta twice. The vote service has to send a stable id.
- **Vote flip.** The event carries the delta from the previous vote, not the new absolute vote. Applying absolute votes in a stream double-counts.
- **Processor dies mid-day.** Counters are a cache of the log. Rebuild the partition from today's `vote_events` for its key range. Closed days are already in `daily_hot` and don't depend on the live counters.
- **Late events after the freeze.** The 02:00 recomputation covers the normal tail. Anything later stays in the log and does not move the page, unless we explicitly re-freeze. Say that cutoff.
- **Spam taken down the next week.** Read-time filter on the frozen 500. If more than 400 of them are removed, recompute that day from `daily_counts`.
- **Clock skew.** `day` is assigned by the vote service from event time, not by the aggregator's wall clock. Otherwise a vote near midnight lands on two days or on neither.

---

## Numbers to say out loud

Label them as assumptions.

```
Votes:            ~20 K/sec average, ~100 K/sec peak
Posts touched:    ~5 M distinct posts on a busy day
Counter size:     5 M × ~32 B ≈ 160 MB for one day's map, before replication
                  Partition for write rate, not because the map cannot fit.
                  (This is the difference from the video's YouTube premise,
                  where the id space does not fit in one hash table.)
Frozen page:      500 rows × ~200 B × 365 days × 15 years ≈ a few GB
Vote log:         20 K/sec × ~100 B ≈ 170 GB/day raw → partition by day, compress, archive
Read of a closed day: point read, cached. Not the scaling problem.
```

The video assumes hundreds of thousands to millions of events per second and an id space that cannot live in a hash table, so it reaches for a sketch early. At Reddit vote volume the exact partitioned counter fits. Use the sketch when the cardinality or the query window outgrows that, not on the first whiteboard.

---

## Discussion prompts

- *"I'm ranking by net score gained that UTC day, so a post from last week can still be #1 today. Do you want posts submitted that day instead?"*
- *"Closed days are exact because we sum every counter before we throw the rest away. If we only kept each hour's top 100, the daily list would be wrong. Want me to walk through that with numbers?"*
- *"Per-subreddit lists are a group-by in the midnight job, not a second vote pipeline. Should I include them?"*
- *"For an arbitrary date range I can either run a batch job or merge count-min sketches and accept overestimates. Which one is the product?"*

---

## What the video gives you, and what this prompt changes

| From the video | How it shows up here |
| --- | --- |
| Single host: hash map of counts + min-heap of size k, O(n log k) | The one-box version, and each partition's local top 500 |
| Partition by item id, each shard emits top k, merge the lists | Exact for **today's global list**, because each post has one home |
| You cannot rebuild an exact hour from 60 exact minutes | The S = 30/minute example. This is why `daily_hot` is computed from full counts |
| Count-min sketch: fixed memory, overestimates, merge by adding cells, still need the ids | Only for arbitrary ranges at read time |
| Fast path (seconds, approximate) and slow path (MapReduce, exact) | Collapsed. Open day = counters. Closed day = one batch freeze. Two pipelines only if they ask for both products |
| k cannot be huge | We freeze 500 and serve 100 |
| Lambda is complex (Kreps) | One path until the requirements force a second |
| Same shape as trending videos, popular products, busiest IPs | Here the event is a **signed vote delta**, so the map stores a sum of deltas, not a count of raw events |
