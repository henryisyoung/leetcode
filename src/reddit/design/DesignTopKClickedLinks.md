# Design Top-K: Most-Clicked External Links per Day (Heavy Hitters)

Reported Reddit prompt (see `../Design.md`): *Reddit doesn't track clicks on external links today. Design a scalable system that can tell us the top 10 most-clicked links each day.*

Architecture follows [System Design Interview – Top K Problem (Heavy Hitters)](https://www.youtube.com/watch?v=kx-XDoPjoHw): a **lambda architecture** with a **fast path** (approximate, seconds–minutes fresh, count-min sketch) and a **slow path** (exact, hours late, partition + MapReduce). Same idea for "top K videos / searches / hashtags".

**Time budget**

| Minutes | What you do |
| --- | --- |
| 0–7 | Clarify. Exact or approximate? Which time windows? Raw or unique clicks? |
| 7–12 | Rough numbers |
| 12–25 | Build up: single host → many hosts → why both paths |
| 25–40 | The full architecture, fast path and slow path |
| 40–55 | Deep dives: count-min sketch, merging correctness, link cleanup and bots |
| 55–60 | Trade-offs, what to drop if the requirement is only "daily" |

---

## 0–7 min: Clarify

| Question | My default |
| --- | --- |
| Time window? | **Daily top 10** is required. Ask if they also want "last 5 min / last hour" (near real-time). **This decides whether we need the fast path at all** |
| Exact or approximate? | Daily report: exact. Near-real-time view: approximate is fine |
| Raw clicks or unique users? | Ask. Default: count each user once per link per day (harder to game) |
| Global only, or per subreddit? | Global first. Per subreddit is the same pipeline with key `(subreddit, link)` |
| How fresh? | Daily list ready within ~1 hour after midnight UTC |
| Who reads it? | Internal (analytics, safety, trending). Low read QPS |
| Bots / spam? | Must filter — otherwise the top 10 is whoever runs the biggest bot farm |

**Out of scope:** dashboards UI, ad-click billing (that needs exactly-once and audit — a different system).

---

## 7–12 min: Rough numbers

```
Clicks on external links      ~1B/day   → ~12K/s avg, ~50K/s peak
Event size                    ~200 B    → ~200 GB/day raw
Distinct links per day        ~50M
Exact counts in memory        50M × (~100 B url + 8 B count) ≈ 5 GB  → too big for one fast in-memory box,
                                                                        fine for a batch job
Read QPS for the result       tiny
```

> **Take-away:** the write stream is big, the result is tiny (10 rows/day). We can't keep exact counts for everything in one machine's memory in real time — that's what pushes us to partitioning (exact) or a sketch (approximate).

---

## 12–25 min: Build up the design (how the video argues it)

Tell it as a story. Each step fixes the problem of the previous one.

1. **One host, hash map + heap.** Count `link → clicks` in a hash map; at the end, a min-heap of size K gives the top K in `O(n log K)`. *Problem:* one host can't take 50K events/s or hold 50M keys.
2. **Many hosts, each counts what it receives, then merge top-K lists.** *Problem:* **wrong answer.** A link that is #11 on every host can be #1 overall. Merging top-K lists is only correct if **each link is counted on exactly one host.**
3. **Partition by link.** `hash(link) % N` → every click on a link goes to the same host. Now each host's top K is exact for its links, and merging N top-K lists **is** correct. *Problems:* still a lot of memory per host, and if a host dies we lose its counts. We need durable storage and re-processing.
4. **Two needs, two paths.**
   - Exact, durable, re-runnable → **slow path**: store events, batch-count with MapReduce / Spark.
   - Fresh within minutes → **fast path**: small fixed memory with a **count-min sketch**, approximate.
   That's the lambda architecture.

---

## 25–40 min: The architecture

```
 Reddit clients (web / apps)
     │ click on external link → goes through redirect service  out.reddit.com/?url=...
     ▼
 ┌──────────────────────┐
 │ API Gateway /        │  logs every click; a background process on each gateway
 │ Redirect service     │  pre-aggregates in memory for a few seconds:  (link → count)
 └──────────┬───────────┘  then flushes — 10× fewer messages downstream
            ▼
   Distributed messaging (Kafka)  topic: link_clicks
            │
     ┌──────┴───────────────────────────────────────────┐
     │ FAST PATH (approximate, seconds–minutes)          │ SLOW PATH (exact, hours)
     ▼                                                   ▼
 ┌───────────────┐                              ┌──────────────────┐
 │ Fast          │ count-min sketch per          │ Data partitioner │ re-key by hash(link)
 │ processors    │ 1-minute window + min-heap    └────────┬─────────┘ into Kafka partitions
 │               │ of K candidates                        ▼
 └──────┬────────┘                              ┌──────────────────┐
        │ every minute: top-K list               │ Partition        │ aggregate per window,
        ▼                                        │ processors       │ write files to
 ┌───────────────────────┐                       └────────┬─────────┘
 │ Storage (SQL)         │ ◀──────────────┐               ▼
 │ topk(window_start,    │                │      Distributed file system (HDFS / S3)
 │      window_size,     │                │               ▼
 │      rank, link,      │                │      ┌──────────────────────────────┐
 │      count, source)   │                └───── │ MapReduce / Spark, daily:    │
 └──────────┬────────────┘                       │ 1. Frequency count job       │
            ▼                                    │ 2. Top-K job                 │
      Top-K query service ◀── internal callers   └──────────────────────────────┘
```

### Ingest

- Clicks go through a **redirect service** (Reddit already uses `out.reddit.com`). That gives a server-side event we trust more than a client beacon, and it works even with ad blockers that kill JS beacons.
- Event: `{link_raw, user_id or anon_id, post_id, subreddit_id, ts, ip_hash, user_agent}`.
- The gateway **pre-aggregates** in memory for a few seconds before sending to Kafka. Cost: if a gateway dies, a few seconds of counts are lost — fine for the fast path. The slow path should read the **raw** event log, not the pre-aggregated one, so it stays exact.

### Fast path

- Fast processors consume Kafka. Each keeps a **count-min sketch** for the current 1-minute window plus a **min-heap of size K** for candidates (the sketch can't list keys, so the heap remembers them).
- Every minute: emit that minute's top K to storage, then reset.
- Fixed memory no matter how many distinct links. Can run without partitioning by link (each processor's sketch covers whatever it sees, sketches can be **added** together cell by cell).
- "Top K in the last hour" = combine the 60 per-minute results. **This is approximate** (see Deep Dive B).

### Slow path

- **Data partitioner** reads raw events, cleans the link (Deep Dive C), and re-publishes keyed by `hash(link)` — so every click on one link lands in one partition.
- **Partition processors** aggregate in memory per short window and write compact files to HDFS / S3. This makes the batch job much cheaper than reading raw events.
- **Daily MapReduce / Spark:**
  1. **Frequency count job:** `map(link, 1) → reduce: sum` per link for the day. If counting unique users: first `distinct (link, user_id)`, then count.
  2. **Top-K job:** each mapper keeps a local heap of K, one reducer merges them. Correct, because job 1 already produced **one total per link**.
- Write the exact daily top 10 to storage with `source = 'batch'`. It **replaces** the fast-path estimate for that day.

### Read

`GET /v1/top-links?window=day&date=2026-10-01&k=10` → query service reads storage. Prefer `batch` rows when they exist; otherwise return `fast` rows and mark them as approximate.

---

## 40–55 min: Deep dives

### Deep Dive A — count-min sketch

- A 2-D array: `d` rows × `w` columns of counters, one hash function per row.
- **Add(link):** for each row `i`, increment `table[i][hash_i(link) % w]`.
- **Estimate(link):** the **minimum** over the `d` counters.
- **It only over-counts, never under-counts** (collisions only add). Taking the minimum picks the row with the fewest collisions.
- Sizing: error ≤ `ε × total_clicks` with probability `1 − δ`, where `w = ⌈e/ε⌉`, `d = ⌈ln(1/δ)⌉`. For example, ε = 0.01%, δ = 0.1% → w ≈ 27K, d ≈ 7 → ~190K counters ≈ 1.5 MB. **Megabytes instead of gigabytes.**
- Sketches with the same size and hash functions can be **merged by adding cells** → easy to combine across processors.
- Why it's fine for top K: heavy hitters have large counts, so a small absolute error rarely changes who is in the top 10. Small links might be over-estimated, but they're far from the top.

### Deep Dive B — when is merging top-K lists correct?

The most common correctness bug in this problem. Say it explicitly:

| Merge | Correct? | Why |
| --- | --- | --- |
| Top-K lists from hosts partitioned **by link** | **Yes** | Each link's full count lives in one place |
| Top-K lists from hosts that each saw **random** traffic | **No** | A link can be #11 everywhere and #1 overall |
| 60 per-minute top-K lists → top K for the hour | **No** (approximate) | A link steady at #11 every minute is missed. That's why the daily answer comes from the slow path |
| Count-min sketches added cell by cell, then query | Yes (within sketch error) | The sketch keeps counts for every key, not only the top K |

Mitigation on the fast path: keep more than K per minute (e.g. top 100) to make the hourly merge more accurate. Trade-off: more storage, still not exact.

### Deep Dive C — link cleanup and abuse (Reddit-specific)

- **Normalize URLs** or the same article splits into many keys: lowercase host, drop `www.`, drop tracking params (`utm_*`, `fbclid`), drop `#fragment`, expand known short-links (`bit.ly`, `t.co`) asynchronously.
- **Bot and spam clicks:** drop clicks with a bad bot score (reCAPTCHA / traffic reputation), known bad IP ranges, or impossible rates from one user. Count **unique users per link per day**, so one script clicking 1M times counts once.
- **Policy-violating links:** the top 10 may contain phishing or malware. Join against the link-safety (Web Risk / link hygiene) result before publishing anything user-facing.
- **Hot key in the slow path:** one viral link sends all its clicks to one partition. Pre-aggregation at the gateway and partition processors flattens it. If still too hot, split that key into `link#0..link#9` and sum the 10 parts in the frequency job.

### Deep Dive D — top 10 every minute / every hour (exact, streaming)

The slow path is "hours late" only because it is a once-a-day batch. With events **partitioned by link**, a streaming processor can keep exact counts per window and publish within seconds of the window closing.

```
on each event (link, ts):   count[minute(ts)][link] += 1
                            count[hour(ts)][link]   += 1      ← count the hour directly
at window close (+ late-click allowance, e.g. 30 s for minute, few min for hour):
    each partition emits its top 10 → merger → global top 10 → store → drop that map
```

- **Exact,** because each link's full count lives on one partition (Deep Dive B, row 1).
- **Hour ≠ merge of 60 minute lists** (Deep Dive B, row 3). Count the hour in its own bucket.
- **"Last 60 minutes", updated every minute:** keep a ring of 60 minute-maps plus a running total. Each minute: add the newest minute, subtract the one that fell out, take top 10 with a heap.
- **Memory too big for a window?** Switch that window to count-min sketch + heap (approximate). Fine for "trending now".
- **Flink:** `keyBy(link)` → tumbling window → count → per-window top-N, with watermarks for late clicks.

```
top_links (window_type, window_start, rank, link, count,
           PK ((window_type, window_start), rank))       -- 'minute' | 'hour' | 'day'
GET /v1/top-links?window=hour&start=2026-10-05T10:00Z   → one partition, 10 rows
```

Batch is still useful for the **final** daily number: richer bot filtering, dedup by event ID, and easy re-runs after a bug.

---

## 55–60 min: Trade-offs

| Choice | Pro | Con |
| --- | --- | --- |
| Lambda (fast + slow) | Fresh **and** exact | Two pipelines, two code paths that must agree |
| Slow path only | Simple, exact, cheap | Answer is hours late |
| Fast path only | Simple, fresh | Approximate; hard to replay or fix bugs |
| Kappa (one streaming job, e.g. Flink with exact keyed state, replay from Kafka) | One code path, can be exact | Large state to manage; replay of a whole day is slower than a batch job |

> **The judgment call to say out loud:** *"If the requirement is only a daily top 10, I'd build just the slow path. Partition by link, count with Spark once a day, done. I'd add the count-min-sketch fast path only if someone needs 'trending in the last 5 minutes'. Lambda is the right answer when both requirements exist, not by default."*

**Failure handling:**
- Kafka keeps events for days → any processor can restart and replay.
- Fast processor dies → that minute's estimate is missing; the daily batch result is unaffected.
- Batch job fails → re-run from HDFS; results are idempotent per `(date)`.
- Duplicate events (at-least-once delivery) → slight over-count on the fast path; on the slow path, dedup by event ID or rely on unique-user counting.

**Common mistakes:**
- Merging top-K lists from un-partitioned hosts.
- Saying a count-min sketch "gives the exact top K".
- Forgetting the sketch can't list keys — you need the heap of candidates beside it.
- Counting raw URLs without normalization.
- Letting bots decide the top 10.
