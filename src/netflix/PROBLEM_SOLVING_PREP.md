# Netflix Problem-Solving Round — Prep Playbook

Companion to `PROBLEM_SOLVING_ROUND.md` (the Netflix-published standards).
This file is the practice plan: a reusable script, a scenario bank to
drill against, and one fully-walked example in the Netflix style.

---

## 1. The 5-Phase Script (what to say in each phase)

Treat this like the rubric — you're literally giving the interviewer the
signals they're scoring on, in order.

### Phase 1 — Clarify (first 3–7 minutes; do not write code)

Always ask 4–6 questions across these four categories:

- **Scale**: How many users? Requests/sec? Data size? P99 latency target?
- **Goals**: What is the business optimizing? Latency, cost, accuracy, freshness?
- **Constraints**: Where does this run — client, edge, server, batch? What can change vs. what's fixed?
- **Boundaries**: What's in scope vs. out of scope today? Single region or global?

Then state your **assumptions out loud** so the interviewer can correct
them: "I'm going to assume X for now — let me know if that's off."

### Phase 2 — Name the Shape

In one sentence, classify the problem against something the interviewer
recognizes:

> "This is a **producer-consumer with backpressure**" / "This looks like
> **TSP / VRP**" / "This is essentially **inverted-index retrieval with
> phrase scoring**" / "This is a **cache-coherence problem with TTL**."

This grounds the rest of the conversation. It also signals you've seen
problems like this before.

### Phase 3 — Two Approaches with Trade-offs (the load-bearing phase)

You should never propose one solution. Always offer at least two:

- **Approach A** (simple/baseline): cheap to build, what does it do well, what does it sacrifice.
- **Approach B** (more sophisticated): why it's better on the metric you defined in Phase 1, what does *it* sacrifice (complexity, cost, ops burden, latency).
- **Pick one** and justify against the goals.

Sketch a small trade-off table out loud — "Approach A is O(n²) memory
but O(1) per query; Approach B is O(n) memory but O(log n) query."

### Phase 4 — Deep Dive (when asked)

The interviewer picks one piece. Be ready to go deeper on **any** of:

- Pseudo-code / real code for a hot path
- Data structures (why a min-heap vs. TreeMap; why a Bloom filter vs. exact set)
- Failure modes (what breaks under partial failure, and how you detect it)
- Capacity math (back-of-envelope QPS × bytes × replication)

### Phase 5 — Ship It

Even if not asked, close with this mini-list:

- **Testing**: unit, integration, load, chaos / failure injection.
- **Rollout**: dark launch → 1% → region → global. What gates each step.
- **Observability**: 3–5 metrics that would page you. Dashboards, structured logs, traces.
- **Success metrics**: customer-visible KPI (e.g. p99 startup latency, completion rate). How you measure regression.

---

## 2. Scenario Bank — Drill These

Order is rough difficulty, ascending. Each takes 30–45 min if you do all
5 phases.

### Routing / scheduling (TSP-flavored, like the doc's example)

1. **DVD Delivery Route** — the standards-doc example. Variants: time-of-day traffic, refusal rate, customer time windows.
2. **Encoder Farm Scheduling** — N video files in 7 ladder bitrates, M GPU workers. Assign jobs minimizing total wall time. (Job-shop / load balancing.)
3. **CDN Edge Pre-Warming** — pick which titles to push to which POPs before a major release. (Knapsack with location-affinity.)
4. **Live-Event Origin Selection** — multi-region live stream, choose which origin each viewer gets. (Constrained assignment / latency-aware load balancing.)

### Streaming + content

5. **Continue Watching Service** — millions of devices write playback positions every few seconds; reads must be low-latency at app open. Design the read/write path.
6. **Subtitle Sync at Scale** — fix subtitle drift across millions of files. Detect and correct offset.
7. **Search Autocomplete for 100M titles** — sub-50ms with prefix + fuzzy + ranking by popularity.
8. **Recommendation Cold Start** — brand-new user, no history. What rows do you fill the homepage with?

### Data / messaging

9. **Notification Throttling** — "new episode" notifications when a season drops; don't spam users; respect quiet hours by timezone.
10. **A/B Test Bucketing** — deterministic assignment, mutually-exclusive experiments, gradual ramps, holdouts.
11. **Watch History Backfill** — re-process last 90 days of plays for a new ML feature, without affecting live traffic.
12. **Distributed Rate Limiter** — protect a downstream API at 10k QPS shared across hundreds of pods.

### Tooling / ops

13. **Build a `jq`-like Query Tool** — what you have in `MiniJqQuery.java`; extend with array indexing, slicing, recursive descent `..`.
14. **Document Search Service** — what you have in `InvertedIndex.java`; extend with ranking (BM25), pagination, multi-field search.
15. **Config Rollout System** — push a config change to 1k services without a thundering herd; verify in flight; roll back in seconds.

Pick **3 from different rows** to drill before the loop. Don't over-fit
on one category.

---

## 3. Worked Example — *"Homepage Continue-Watching Row"*

The full 5-phase script on a Netflix-flavored scenario you might
actually get.

**Prompt.** "Design the Continue Watching row on the homepage. When a
member opens the app, the row should show the videos they've started but
not finished, in the order most likely to be resumed."

### Phase 1 — Clarify

> *I want to scope this. Some questions:*
>
> - **Scale.** ~250M members, ~1B "play" events/day, app opens ~5/day per member. Target p99 row-render budget?
> - **Devices.** Single sign-on across phone, TV, browser? How quickly must a position written on phone show up on TV?
> - **Definition of "started but not finished".** Threshold (e.g. >2 min watched and <90% complete)? What about completed-then-restarted?
> - **Order.** "Most likely to resume" — is recency a fine proxy or do we want a model? OK to start with recency + small bias?
> - **Ranking universe.** Cap at, say, top 20 items per member?
> - **Out of scope today.** Personalized thumbnails, downloaded titles, kids profile separation — flag, defer.
>
> *Assumptions I'll proceed with:* row p99 ≤ 50ms server-side;
> cross-device propagation budget ≤ 5s; "in progress" =
> `2min ≤ pos < 0.9·duration`; max 20 items.

### Phase 2 — Name the Shape

> "This is a **read-heavy, write-frequent personalized cache** problem
> with a **freshness vs. cost trade-off**. Writes come from device
> heartbeats; reads come from app opens. The right shape is an
> **eventually-consistent per-member materialized view**."

### Phase 3 — Two Approaches

**Approach A — Read-time aggregation.**
- Heartbeats land in an event store (Kafka). On app open, the homepage service queries the last 30 days of events for that member, filters in-progress, ranks by recency, returns top 20.
- ✅ Simple, no extra storage, always fresh.
- ❌ Read latency unpredictable (days × items), CPU-heavy on every open, expensive at app-open spikes (TV peak hour).

**Approach B — Write-time materialized view (recommended).**
- Each heartbeat upserts into a per-member sorted set keyed by `lastWatched`. Service-side reads are an O(20) prefix on a single key.
- Storage: KV store (e.g. Cassandra / DynamoDB) keyed by `(memberId)` → `Map<videoId, {position, duration, lastWatched}>`.
- Cache: hot members in Memcached/Redis with TTL of a few minutes. App-open path: cache → KV fallback.
- ✅ p99 << 50ms, predictable, scales linearly with members not events.
- ❌ Two write paths to keep consistent, eventual consistency on cross-device, cache invalidation is a thing.

> "I'd pick **B** — the read SLO is the stronger constraint. The
> cross-device delay budget of 5s is well within KV propagation."

### Phase 4 — Deep Dive (suppose interviewer picks the write path)

Sketch:

```
device --HTTP--> playback-service --kafka--> "playback-events"
                                        |
                                        v
                              continue-watching-consumer
                                        |
                          per-member upsert into KV
                          + invalidation message to cache
```

Trade-offs to call out:

- **At-least-once delivery** → idempotent upserts using `(memberId, videoId)` and `lastWatched` as a high-water mark (only overwrite if newer).
- **Hot keys** (a wildly popular member or a celebrity account) — partition by `(memberId, hash(videoId))` to spread writes.
- **Eviction**: prune entries older than 30 days lazily on read.

### Phase 5 — Ship It

- **Testing.** Contract tests against KV; replay-tests on real production event traffic with output diff vs. Approach A.
- **Rollout.** Dual-write Approach A and B for 1 week, compare diffs, then read from B for 1% of members. Ramp by region.
- **Observability.** Metrics: `cw_row_render_p99`, `kv_write_lag_seconds`, `cache_hit_rate`, `cross_device_propagation_seconds`, `event_drop_rate`. Page on the first three.
- **Success metric.** *Resume rate from the Continue Watching row*. Compare against control.
- **Risks.** A consumer outage stalls the row. Backstop: fall back to Approach A read for the affected members; alarm on consumer lag > 60s.

---

## 4. Drill Routine (one week)

| Day | Time | What |
|---|---|---|
| Mon | 45 min | Pick one scenario from row "Routing/scheduling". Write Phase 1 questions out loud (record yourself). |
| Tue | 45 min | Same scenario, run Phases 2–3. Force yourself to two approaches with trade-offs. |
| Wed | 30 min | Same scenario, Phase 4 (pseudo-code) + Phase 5 (ship). |
| Thu | 45 min | New scenario from row "Streaming + content". Full loop. |
| Fri | 45 min | New scenario from row "Data / messaging". Full loop. |
| Sat | 30 min | Mock with a friend on one fresh prompt — they only listen, no hints. |
| Sun | 30 min | Review your own recordings. Look for: did you ask before solving? Did you propose two approaches? Did you close with shipping? |

**The single highest-leverage habit**: every time you catch yourself
coding before clarifying, stop and back up. The Netflix rubric
explicitly rewards Phase 1 over Phase 4.
