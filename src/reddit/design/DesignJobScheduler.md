# Design a Distributed Job Scheduler — 60-min Interview Version

> Quick / interview-paced version. Companion to `snowflake/mianjing/design/DesignDistributedJobScheduler.md` (full deep-dive).

Run millions of background jobs per day across thousands of workers. Examples: nightly financial reports, send marketing email at 9 AM, retry failed payment captures every 5 min, fire ML training when new data lands.

**Time budget**

| Minutes | What you do                              |
| ------- | ---------------------------------------- |
| 0–5     | Clarify + capacity                       |
| 5–10    | API                                      |
| 10–25   | Architecture + the three core flows      |
| 25–45   | Pick 2 deep dives                        |
| 45–55   | Scale + failure handling                 |
| 55–60   | Trade-offs and wrap-up                   |

---

## 0–5 min: Clarify

### Step 1 — Pin the hard invariants

The interviewer is listening for three things by name:

1. **At-least-once delivery** (we will retry; "exactly-once" is a fairytale).
2. **Idempotency** (the *application* job must be safe to run twice).
3. **No silent drops** (durability of submitted jobs is non-negotiable).

> Say out loud: *"I'll design for at-least-once + idempotency. Exactly-once over a network is impossible; we make it irrelevant by giving every execution a unique key."*

### Step 2 — Functional requirements

- **Submit** a one-shot or cron-style recurring job.
- **Cancel** a recurring job.
- **Query status** of a job and its recent executions.
- **Retry** failed jobs with exponential backoff.
- **DLQ** poison jobs after N retries.

Out of scope: DAG / workflow orchestration (Airflow / Temporal territory). In-process schedulers (Quartz).

### Step 3 — Non-functional requirements

| Requirement              | Target                  |
| ------------------------ | ----------------------- |
| Scheduling latency       | < 5 s p99               |
| Throughput               | ~6 K jobs/sec peak      |
| At-least-once            | No silent drops         |
| Availability             | 99.99%                  |
| Worker death tolerance   | Stuck job auto-recovers |

### Step 4 — Capacity math

```
100 M jobs/day           ≈ 1,160 jobs/sec average
Peak (5×)                ≈ 6,000 jobs/sec
Avg job metadata         2 KB
Hot dataset (1 day)      ~200 GB
Annual archive           ~70 TB → S3 / Cassandra
Worker fleet             1K – 10K nodes
```

> **Take-away:** the data is modest; the QPS is modest; what blows up is **completed-job history**. Plan tiered storage from the start.

---

## 5–10 min: API

### Submit

```http
POST /v1/jobs
Headers: Idempotency-Key: report_2026_05_01
{
  "job_type": "generate_daily_report",
  "schedule": "0 9 * * *",
  "payload":  {...},
  "retry_policy": { "max_retries": 5, "backoff_base_seconds": 30 }
}
→ 201 { "job_id": "job_789", "next_run_at": "2026-05-02T09:00:00Z" }
```

`Idempotency-Key` lets the client retry without creating two jobs.

### Cancel / Status

```http
POST /v1/jobs/{id}/cancel
GET  /v1/jobs/{id}/executions?limit=10
```

### Worker-side (internal, usually queue protocol)

```http
POST /v1/internal/jobs/claim         { worker_id, max_jobs, lease_seconds }
POST /v1/internal/executions/{id}/heartbeat   // extend the lease
POST /v1/internal/executions/{id}/complete    // success
POST /v1/internal/executions/{id}/fail        // failure → retry or DLQ
```

> Submit / status APIs are HTTP for users. Worker APIs are typically Kafka / SQS messages, not HTTP — pull, not push.

---

## 10–25 min: Architecture + The Three Core Flows

### Decouple decision from execution

> The **Scheduler** decides *what should run now*. The **Worker** decides *I am running this*. They never share state directly — only through a queue.

This separation buys:
- Sharded scheduler (per-shard leader, no global bottleneck).
- Stateless workers (any can take any job).
- If all workers die, the queue holds the work.
- If a scheduler shard fails over, the unique `(job_id, scheduled_run_at)` index prevents duplicate enqueue.

### Components

```
Submit API → jobs table ── Scheduler (per-shard leader, polls next_run_at)
                                       │ (enqueue)
                                       ▼
                                 Job Queue (Kafka / SQS, partitioned by tenant)
                                       │ (pull + lease)
                                       ▼
                                 Workers (stateless; heartbeat their lease)
                                       │ (write status)
                                       ▼
                                 job_executions table  +  Result Store (S3)
                                       ▲
                                       │
                                     Reaper (sweeps stuck leases)
```

| Component        | Role                                                         |
| ---------------- | ------------------------------------------------------------ |
| Submit API       | Stateless HTTP. Validates, writes `jobs`.                    |
| Scheduler tier   | Sharded by `job_id`. Each shard has a leader (etcd lease).   |
| Job Queue        | Kafka or SQS. Partitioned by tenant.                         |
| Worker fleet     | Pull from queue, claim lease, run, heartbeat, ack.           |
| Metadata store   | PostgreSQL (hot) + S3/Cassandra (cold).                      |
| Reaper           | Resets jobs whose lease expired without ack.                 |
| Coordination     | etcd / ZooKeeper. Scheduler shard leader leases.             |

### The three core flows

#### Submit

```sql
INSERT INTO jobs (job_id, schedule, payload, ..., next_run_at, state)
VALUES (...)
ON CONFLICT (idempotency_key) DO NOTHING;
```

#### Schedule (per-shard leader, every few seconds)

```sql
SELECT job_id, next_run_at FROM jobs
WHERE state='ACTIVE' AND next_run_at <= NOW() AND shard_id = $my_shard
ORDER BY next_run_at LIMIT 1000;
```

For each row, atomically claim the slot before enqueueing:

```sql
INSERT INTO job_executions (execution_id, job_id, scheduled_run_at, status='PENDING', attempt=1)
ON CONFLICT (job_id, scheduled_run_at) DO NOTHING
RETURNING execution_id;
```

The **unique index on `(job_id, scheduled_run_at)`** is the dedup primitive. Even on a leader failover race, only one INSERT wins. The loser skips silently. Push the row id to Kafka, advance `next_run_at`.

#### Execute (worker pulls from queue)

```
1. Consume message → execution_id
2. UPDATE job_executions SET status='RUNNING', worker_id=…, lease_expires_at=NOW()+60s
3. Run the job code
4. Heartbeat every 20s  (heartbeat = lease_ttl / 3)
5. On success → status='SUCCEEDED', ack
6. On failure → status='FAILED', schedule retry with exp backoff + jitter
                or move to DLQ if attempt > max_retries
```

---

## 25–45 min: Deep Dives (pick 2)

### Deep Dive A: The Three-Layer Defense Against Stuck Jobs

A worker crashes between steps 3 and 5. Nobody marks the job done. What stops the job from being stuck forever?

| Layer            | What it does                                                                |
| ---------------- | --------------------------------------------------------------------------- |
| **Heartbeat**    | Worker extends the lease every ~`ttl/3` while it's healthy. If it stalls or dies, the lease expires. |
| **Queue lease**  | Most queues redeliver on visibility-timeout expiry. Kafka with consumer-group offsets + manual commits gives equivalent semantics. |
| **Reaper**       | Background sweep of `job_executions` where `status='RUNNING' AND lease_expires_at < NOW()` → reset to PENDING and bump `attempt`. Belt-and-suspenders for cases where the queue forgets. |

#### The killer detail

> `heartbeat_interval` MUST be strictly less than `lease_ttl`. The standard ratio is **1:3** so two missed heartbeats don't expire a healthy worker.

If a candidate sets `heartbeat=60s, lease=60s`, the conversation derails — the worker is alive but the lease expires anyway, leading to duplicate execution. Mention the ratio explicitly.

#### Idempotency is the user's job, not the system's

We deliver at-least-once. If your job sends an email and doesn't dedup on `(execution_id)`, you'll send duplicate emails. The scheduler exposes `execution_id` for exactly this reason. Application is responsible for idempotency; system makes it possible.

### Deep Dive B: Sharding the Scheduler + Avoiding Thundering Herd

#### Sharding

Single scheduler thread polls ~few-thousand QPS comfortably. At 100M jobs/day with bursts we need many.

```
shard_id = hash(job_id) % N        // typically N = 32 or 128
```

Each shard:
- Owns its slice of `jobs` rows.
- Runs leader election via etcd lease (one leader per shard).
- Polls only its own shard.

Failover is per-shard — losing one shard's leader for 5 s loses 1/Nth of capacity for 5 s, not the whole system.

#### Thundering herd

50,000 jobs scheduled at `09:00:00.000` will all enqueue in the same second and overload downstream services.

Three mitigations:
- **Jittered enqueue**: spread "due now" jobs across a few seconds.
- **Per-tenant rate limit at the queue**: Kafka producer quotas / SQS throughput limits.
- **Worker poll jitter**: never let 10K workers wake on the same wall-clock tick.

Also: **exponential backoff with jitter** on retries:

```
delay = min(max_delay, base * 2^attempt) * (1 + random(-0.1, 0.1))
```

Without jitter, every retry storm is synchronized — you get a herd at every retry boundary too.

### Deep Dive C: Tiered Storage for Job History

Active jobs are small. **Completed-job history** grows unbounded — 70 TB/yr in our capacity math.

| Tier        | Tech                  | What lives here                                   | Lifetime           |
| ----------- | --------------------- | ------------------------------------------------- | ------------------ |
| Hot         | PostgreSQL            | `jobs`, last 7 days of `job_executions`           | active + 7 days    |
| Hot         | Kafka / SQS           | "Run now" messages                                | minutes – hours    |
| Hot         | Redis                 | Hot status cache, per-tenant rate-limit counters  | seconds – minutes  |
| Coordination | etcd / ZooKeeper     | Scheduler leader leases                           | persistent         |
| Cold        | S3 / Cassandra        | Completed history, logs, big payloads             | years              |

Nightly job moves `job_executions` older than 7 days from Postgres → S3 / Cassandra. Hot DB stays small + fast. Query layer is one façade reading from hot first, cold on miss.

### Deep Dive D: Why No Redis-Only Lock for Dedup?

You'll be tempted to put Redis in front of `claim_job` for speed. **Don't put the source-of-truth lock there.**

The DB unique index on `(job_id, scheduled_run_at)` is **the** dedup primitive — transactional, durable, free. Redis can lose lock state under partition / restart / failover, and a recovered Redis serving stale `SETNX` results means **duplicate execution**.

Redis is fine for caching read-only status (`GET /jobs/{id}`); it is not fine as the authority for dedup. This is the same lesson as the booking system using SQL row locks instead of Redis for double-book prevention.

---

## 45–55 min: Scale + Failure Handling

| Failure                          | Behavior                                                               |
| -------------------------------- | ---------------------------------------------------------------------- |
| Worker crashes mid-job           | Lease expires → queue redelivers → reaper resets the row               |
| Scheduler shard leader dies      | etcd lease expires (~5 s) → new leader elected → resumes               |
| All Postgres replicas down       | Submit API returns 503; queue keeps draining existing work             |
| Kafka partition unavailable      | Scheduler buffers enqueues; emits metric; backpressure on Submit       |
| Poison job (always fails)        | After `max_retries` → DLQ; doesn't block the partition                 |
| Wall-clock drift across nodes    | Per-shard leader owns `next_run_at` updates; NTP for everyone          |
| Long-running job exceeds lease   | Worker keeps heartbeating; lease stays alive                           |
| Cascading downstream failure     | Per-tenant rate limit; circuit breaker on outbound calls               |

### Scaling levels (mention them)

| Scale               | Architecture                                                  |
| ------------------- | ------------------------------------------------------------- |
| < 10K jobs/day      | Single Postgres + one scheduler + one worker pool             |
| 100K – 10M / day    | Sharded Postgres + 4–16 scheduler shards                      |
| 100M / day          | 32–128 shards, Kafka queue, S3 history tiering                |
| > 1B / day          | Per-region clusters; event-driven triggers replace polling    |

### Event-driven beats polling at huge scale

For non-time-based jobs ("run when an S3 object lands"), subscribe to the source's event stream directly — eliminates the timer poll for that class of jobs entirely.

---

## 55–60 min: Trade-offs / Common Mistakes

| Mistake                                                                       | Effect                                          | Fix                                              |
| ----------------------------------------------------------------------------- | ----------------------------------------------- | ------------------------------------------------ |
| One process does timer + dispatch + execute                                   | Single point of failure                         | Separate scheduler / queue / worker tiers        |
| All workers poll the DB                                                       | Thundering herd at top of hour                  | Scheduler enqueues; workers consume from queue   |
| Redis lock as only dedup                                                      | Lock loss → duplicate execution                 | DB unique index `(job_id, scheduled_run_at)`     |
| At-least-once with non-idempotent job code                                    | Duplicate emails, double charges                | Make jobs idempotent OR transactional outbox     |
| No DLQ                                                                        | Poison job blocks the partition forever         | DLQ after N retries                              |
| Hot DB holds all history                                                      | Tables grow unbounded; queries slow             | Tier > 7-day executions to S3                    |
| `heartbeat >= lease_ttl`                                                      | Worker alive but lease expires → double run     | `heartbeat = lease_ttl / 3`                      |
| Cron "fire at 09:00 sharp" assumed accurate to ms                             | Clocks disagree across nodes                    | Single leader per shard; document ±N s SLO       |
| No observability                                                              | Pipelines silently fall behind by hours         | `lag_p99`, `dlq_size`, `lease_expiry_rate`       |
| Submit API not idempotent                                                     | Client retry creates duplicate jobs             | `Idempotency-Key` header → DB unique constraint  |

### Key Concepts for the Interview

| Topic                              | What to say                                                                                       |
| ---------------------------------- | ------------------------------------------------------------------------------------------------- |
| Decouple decision from execution   | Scheduler decides what; queue holds it; worker pulls. Each scales on its own axis.                |
| At-least-once + idempotency        | Exactly-once doesn't exist on a network. We get there with `(job_id, scheduled_run_at)`.          |
| Three-layer defense                | Heartbeat → queue lease → reaper. Pick two and you'll lose jobs sometimes.                        |
| Per-shard leader, not global       | One global leader is one global bottleneck. Shard by `job_id % N`.                                |
| Unique index = cheap dist lock     | The DB primary key IS the lock. Transactional, durable, free.                                      |
| Pull, not push                     | Workers pull → automatic backpressure, no need to track liveness.                                  |
| DLQ + exp backoff + jitter         | Standard. Always mention jitter to avoid synchronized retries.                                     |
| Tiered storage                     | Hot SQL (7 days) → cold S3 (years). History is huge; metadata is small.                            |
| Observability is part of design    | `lag_p99`, `dlq_size`, `lease_expiry_rate`, `worker_busy_pct`. Without them, undebuggable.        |

### Wrap-Up

| Aspect                          | Solution                                              |
| ------------------------------- | ----------------------------------------------------- |
| Scale to 100M / day             | Sharded scheduler + queue + stateless workers         |
| Avoid duplicate execution       | Lease + DB unique index `(job_id, exec_id)`           |
| Survive worker crashes          | Heartbeat lease + queue redelivery + reaper           |
| Survive scheduler crashes       | Per-shard leader election (etcd / ZooKeeper)          |
| Avoid thundering herd           | Jittered enqueue + poll + retry                       |
| Poison jobs                     | DLQ after N retries with exp backoff + jitter         |
| Long-tail history               | Tier completed jobs SQL → S3 / Cassandra              |
| Big outputs                     | Worker writes to S3; URL only in DB                   |
| Clock drift                     | NTP + per-shard leader owns `next_run_at`             |
| Operability                     | `lag_p99`, `dlq_size`, `lease_expiry_rate` as SLOs    |
