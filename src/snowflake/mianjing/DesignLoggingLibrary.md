# Design a Logging Library (Web + Mobile)

Design a logging library that runs in web frontends and mobile apps, and ships
logs to a backend service — **without slowing down the host application or
overwhelming the backend.**

The central rule throughout: **the caller's thread is never blocked on a log
operation.** Every other decision (batching, sidecars, sampling, fallback)
follows from that one principle.

---

## Phase 1: What We Need to Build

### Functional Requirements

| API | Purpose |
| --- | ------- |
| `log(level, message, context)` | The only call the application makes. Returns immediately. |
| `setUserId / setDevice / setEnv` | Attach metadata that gets stamped on every log line. |
| `flush()` | Optional — caller asks "send everything now" (used at shutdown / app-background). |
| Configurable levels | DEBUG, INFO, WARN, ERROR, FATAL, with a runtime-tunable threshold. |

We do **not** support:
- Synchronous "log and block until durable on server" — that's a different feature (audit log).
- Querying logs from the SDK — read path lives in the backend.

### Non-Functional Requirements

| Requirement | Target | Why |
| ----------- | ------ | --- |
| **Caller latency** | `log()` returns in < 1 ms (P99) | Logging on the hot path must be invisible. |
| **Memory cap** | Bounded ring buffer (e.g. 4 MB on mobile, 16 MB on web) | A buggy log loop must not OOM the app. |
| **Loss tolerance** | Best-effort: < 0.1% loss in normal ops | Missing one DEBUG line is fine; ERROR lines we try harder. |
| **Backend QPS at peak** | 100K – 1M log lines / sec | Many clients × bursty events (e.g. crash storms). |
| **Network efficiency** | Batched + compressed | A 4G connection on mobile can't sustain per-line POSTs. |
| **Offline support (mobile)** | Persist locally, replay on reconnect | Phones spend hours offline. |

### Capacity Math (rough)

| Metric | Value |
| ------ | ----- |
| DAU                          | 50 M    |
| Logs / user / day            | 50      |
| Avg log size                 | 500 B   |
| Daily volume                 | ~1.25 TB|
| Peak QPS (per service)       | ~30 K   |
| Burst (crash storm)          | 10× peak ≈ 300 K |

So we plan for **bursts of ~300 K QPS**, not the average.

---

## Phase 2: The Two-Layer Architecture (Client SDK + Sidecar)

```
┌────────────────────────┐
│   App / Web / Mobile   │
│                        │
│   logger.log("...")    │  ← never blocks
│        │               │
│        ▼               │
│  ┌──────────────────┐  │
│  │ In-memory buffer │  │  ← ring buffer, fixed size
│  └──────────────────┘  │
│        │ (background)  │
└────────┼───────────────┘
         ▼
┌────────────────────────┐
│   SIDECAR  (own proc)  │  ← only on web/server side
│                        │
│  Aggregates from many  │  ← localhost socket / shared memory
│  app instances on the  │
│  same machine, batches │
│  + compresses + ships  │
└────────┼───────────────┘
         ▼
┌────────────────────────┐
│  Log Ingestion API     │
│  (HTTP/gRPC)           │
└────────┼───────────────┘
         ▼
   Kafka / Kinesis ──► Workers ──► Object store + index (S3 + Elastic / Loki)
```

**Two layers because they have different jobs:**

- The **SDK** runs *inside* the app process. It must be tiny, allocation-free
  on the hot path, and never the cause of an outage.
- The **sidecar** runs *next to* the app (separate process, same host or pod).
  It owns network I/O, batching, retries, and crash isolation. The host
  service's CPU/memory budget is unaffected if the sidecar is doing heavy
  compression or retrying.

For mobile, there's no sidecar — the SDK ships directly to the backend, but
with the same async/batch strategy and a local SQLite backing store.

---

## Phase 3: The Client SDK (the part that matters most)

### The hot path — `log(level, message, ctx)`

```
1. Cheap-reject if level < threshold       (atomic bool read)
2. Build a Record { ts, level, msg, ctx }  (pre-sized struct)
3. Try to enqueue into a ring buffer       (lock-free or single-mutex)
4. If buffer is full → drop + bump dropCounter (NEVER block!)
5. Return.
```

That's it. Everything else happens **on a separate background thread**:
serialization, compression, network I/O.

### The Ring Buffer

A fixed-size circular buffer of `Record` slots:

| Property | Why |
| -------- | --- |
| Fixed capacity (e.g. 8K records) | Worst-case memory is bounded. |
| Lock-free single-producer multi-consumer (or vice-versa) | The hot path takes microseconds. |
| Drop-on-full policy | Slowing the caller is forbidden. We bump a `dropped` counter and continue. |
| Overflow metric reported to backend | Observability into backpressure events. |

On web, a single JS thread means we use a plain array. On native mobile and
the server SDK, we use a true lock-free queue (e.g. LMAX-style disruptor).

### Background Flusher (the batcher)

A single background thread reads from the ring buffer and flushes when **any**
of these conditions is met — whichever comes first:

| Trigger | Default | Why |
| ------- | ------- | --- |
| Batch size reached | 256 records or 64 KB | Amortize HTTP overhead across many lines. |
| Time elapsed | 1 second | Bound staleness — we still want fresh logs. |
| Memory pressure | 75% of buffer used | Ship before we have to drop. |
| Explicit `flush()` | called on shutdown / page-hide / app-backgrounded | Don't lose the final logs. |

This is the standard **"size OR time OR pressure"** trigger pattern. Batching
turns N small POSTs into 1 large POST — usually a 50–200× throughput win.

### Wire format

- Newline-delimited JSON (`ndjson`) — easy to parse and stream.
- gzip or zstd compression — typically 5–10× shrink for log text.
- Single POST to `/v1/logs/batch` with the gzipped body.
- Schema:

```json
{ "ts": 1700000000123, "lvl": "WARN", "msg": "...", "trace": "...", "user": "u_42", "app": "ios-3.4.1" }
```

### Backpressure: "the buffer is filling up — what now?"

This is the question your interviewer is teeing up. Five strategies, used in
combination:

| Strategy | When | Effect |
| -------- | ---- | ------ |
| **Drop oldest (or newest)** | Buffer full | Bounded memory; reports drop count. |
| **Down-sample by level** | Sustained pressure | Drop DEBUG/INFO first; keep WARN/ERROR/FATAL. |
| **Dynamic sampling** | Volume above SLO | Keep 1-in-K of repeated identical messages. |
| **Disk spill (mobile/server)** | Buffer near full | Spill to local SQLite or a memory-mapped file ring; drain back into RAM when network catches up. |
| **Adaptive batch size** | Backend slow | Grow batches and back off — fewer round-trips, give server breathing room. |

The order is important: dropping low-value records is far better than blocking
the caller or dropping FATALs.

### Mobile-specific concerns

- **Persistent ring buffer** — on iOS/Android the user can kill the app at any
  time, taking everything in RAM with it. Use SQLite or `NSPersistentStore` so
  unflushed records survive a kill.
- **Wi-Fi vs cellular** — on cellular, batch larger and ship less often. On
  Wi-Fi, ship aggressively.
- **App-lifecycle hooks** — flush on `onPause` / `applicationWillResignActive`.
- **Battery awareness** — defer non-critical levels when battery < 20%.
- **Retry with exponential backoff + jitter** — same pattern as any resilient
  HTTP client.

---

## Phase 4: The Sidecar (server-side)

For services running on a server (or in a Kubernetes pod), we put a small
**sidecar process** next to each app instance. The app's SDK now talks to
`localhost:5555` over a Unix socket or local HTTP, instead of going to the
backend directly.

```
┌─ Pod ─────────────────────────────────────┐
│                                           │
│   ┌──────────┐         ┌──────────────┐   │
│   │ App proc │ ──UDS──►│  Sidecar     │ ──HTTPS──► Ingestion API
│   │ (SDK)    │         │  (Fluent Bit │   │
│   └──────────┘         │   / Vector / │   │
│                        │   custom)    │   │
│                        └──────────────┘   │
└───────────────────────────────────────────┘
```

### Why a sidecar — not "just a thread in the app"?

| Concern | Without sidecar | With sidecar |
| ------- | --------------- | ------------ |
| **CPU spent on gzip / TLS** | Charged to the main service's CPU budget — competes with serving requests. | Lives in the sidecar; main service is untouched. |
| **Memory headroom** | Ring buffer + retry queue all in app heap. | Separate process, separate heap. App heap stays small. |
| **Crash isolation** | If the log code segfaults, the app dies. | Sidecar can crash and restart without affecting the app. |
| **Independent deploys** | Logging changes need a full app redeploy. | Sidecar upgrades roll out independently. |
| **Backpressure cost** | Backend slowdown affects the app threads. | Backend slowdown is absorbed by the sidecar's disk-buffered queue. |

This is exactly the "service mesh" / "Envoy proxy" pattern: keep the
infrastructure concerns out of the application binary.

### What the sidecar adds beyond what the SDK already does

- **Cross-process aggregation** — many app instances on the same node funnel
  through one sidecar. The backend sees one big batch instead of N small ones.
- **Local disk-backed queue** — when the backend is slow, the sidecar can
  buffer GB to disk; the main service never feels it.
- **TLS termination, auth, schema enrichment** — boring things you don't want
  every app to re-implement.
- **Adaptive flow control** — the sidecar throttles the SDK back via a
  per-connection rate (e.g. via a 429 + `Retry-After` over the local socket).

### Communication channel

| Choice | When |
| ------ | ---- |
| Unix domain socket | Same host. Lowest latency. |
| Localhost HTTP/gRPC | Cross-language, easy to debug. |
| Shared memory ring | Extreme throughput (>1M/s). Rarely needed in practice. |

The SDK still keeps its own ring buffer — even talking to localhost can fail
(sidecar restart) and we don't want to lose those logs.

---

## Phase 5: Handling Volume on the Backend

Even with batching and a sidecar, peak QPS can be enormous (300K+ batches/s).
The standard pipeline:

```
Sidecar  ─►  Ingestion API  ─►  Kafka topic  ─►  Workers  ─►  Storage
            (stateless,         (per-tenant       (parse,       (S3 cold,
             auto-scaled)        partitioned)     index)        Elastic warm)
```

### Why Kafka in the middle?

The Ingestion API does the bare minimum: validate, append to Kafka, return
202. That decouples the **write QPS from the parse/index throughput**. If the
indexing tier slows down, Kafka absorbs the lag — clients are unaffected.

### Sharding strategy

| Shard by | Pros | Cons |
| -------- | ---- | ---- |
| `user_id` | All of one user's logs in one place — easy to follow a session. | Hot users create skewed partitions. |
| `tenant_id` (B2B) | Per-tenant SLAs, isolation. | Same hot-tenant problem at a coarser level. |
| Random / round-robin | Perfectly balanced load. | "Show me all logs for user X" is now a fan-out query. |

A common compromise: hash on `tenant_id` with **secondary sub-partitioning**
on `user_id` for hot tenants only.

### Sampling at the backend (tail-based sampling)

The SDK can't tell whether a log line is "interesting" yet — that depends on
context (was there an error in the same trace?). The backend can:

1. Buffer all logs for a trace for a few seconds.
2. If no ERROR/FATAL in the trace → sample at 1%.
3. If any ERROR/FATAL → keep 100%.

This is the classic distributed-tracing sampling strategy adapted to logs.

---

## Phase 6: Reliability & Observability

### What we promise the caller

- **`log()` is non-blocking and bounded-cost.** Always.
- **Logs are best-effort with at-most-once delivery.** Easier to reason about
  than at-least-once + dedup.
- **`flush()` is best-effort, not a guarantee.** If the network is unreachable
  during shutdown, logs may be lost (mobile mitigation: persist to disk).

### Built-in metrics the SDK reports back

The SDK should report **its own health** as part of the next batch:

| Metric | What it tells you |
| ------ | ----------------- |
| `dropped_total` | Backpressure happened — too much volume or slow backend. |
| `buffer_high_water` | How close we got to overflow. |
| `flush_failures_total` | Network or backend issues. |
| `flush_latency_p99` | Backend health from the client's perspective. |
| `disk_spill_bytes` | Mobile / server fell back to local persistence. |

Without these, you're flying blind — the logging system is the *one* tool that
must observe its own failure modes.

### What about FATAL / crashes?

- **Crash before flush** = lost logs. Mitigation:
  - SDK catches `uncaughtException` / signals and synchronously writes the
    last few records to disk.
  - On next launch, the SDK uploads any pending records first.
- **Out-of-order timestamps after replay** are expected. The backend sorts on
  ingest by `ts` field, not arrival time.

---

## Common Mistakes

| Mistake | Why it's bad | Fix |
| ------- | ------------ | --- |
| Synchronous HTTP call from `log()` | Hot path takes 50–500 ms; one slow log call freezes UI. | Always async. |
| Unbounded in-memory queue | Memory leak under any backend stall. | Bounded ring buffer + drop policy. |
| Per-line POSTs to backend | TCP/TLS handshake dominates; backend QPS unmanageable. | Batching. |
| No backpressure signal | SDK silently drops logs forever. | Counter + emit drop count. |
| Ship on the app's main thread | UI jank on mobile; request latency on server. | Background thread / sidecar. |
| Logs in the same process as the work | Heap, GC pause, CPU competition. | Sidecar. |
| Forget mobile lifecycle | App killed → all unflushed logs gone. | Persistent buffer on disk. |
| Forgot crash storms | One bug → 100K errors/sec → backend OOM. | Per-client rate limit + level-based sampling. |

---

## Key Concepts for the Interview

| Topic | What to say |
| ----- | ----------- |
| **Async, never block the caller** | The single most important rule. |
| **Bounded ring buffer + drop policy** | "Lose data, don't lose the app." |
| **Batch trigger: size OR time OR pressure** | Standard pattern; mention all three triggers. |
| **Sidecar pattern** | "I'd put a sidecar like Fluent Bit / Vector next to each app instance to keep CPU/memory off the main service and to absorb backend slowness via a local disk-backed queue." |
| **Mobile = SQLite-backed buffer** | Survives app kill; replay on reconnect. |
| **Sampling** | Drop low-priority records first; tail-based sampling at the backend for traces with errors. |
| **Backend = stateless ingest + Kafka + workers** | Decouples write QPS from index throughput. |
| **Sharding by tenant + sub-partition by user** | Avoids hot keys; preserves locality for queries. |

---

## Wrap-Up

| Aspect | Solution | Why |
| ------ | -------- | --- |
| Caller latency | Bounded async ring buffer | Hot path < 1 ms. |
| Memory safety | Fixed buffer + drop-on-full | Worst case is a metric, not an OOM. |
| Network efficiency | Batch (size/time/pressure) + gzip | 50–200× fewer round trips. |
| Server CPU / memory | Sidecar process | Logging cost charged to the sidecar, not the app. |
| Backend slowness | Local disk-backed queue in sidecar | Decouples client from backend. |
| Volume bursts | Kafka queue + per-client rate limit + sampling | Smooth out 10× spikes. |
| Mobile offline | SQLite-backed buffer + replay | Phones spend hours disconnected. |
| Crashes | Pre-crash flush to disk + replay on launch | Don't lose the last 50 lines that explain the bug. |
| Observability | SDK self-reports `dropped`, `buffer_high_water`, etc. | Logging system observes itself. |
