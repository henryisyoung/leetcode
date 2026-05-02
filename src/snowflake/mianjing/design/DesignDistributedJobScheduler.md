# Design a Distributed Job Scheduler

> Source: <https://www.systemdesignhandbook.com/guides/design-a-distributed-job-scheduler/>

Design a system that runs millions of background jobs every day across thousands of worker machines. Think cron, but at the scale of a whole company: financial reports at midnight, marketing emails at 9 AM, retries for failed payment captures every few minutes, ML training pipelines triggered when new data lands.

The main challenge is not "how do I run code." It is **never running the same job twice** (when a worker crashes mid-execution and the queue redelivers), **never losing a job** (when the scheduler itself crashes), and **never letting one bad job take the whole pipeline down**.

---

## Phase 1: What We Need to Build

### Functional Requirements

- **Submit Job**: A user or service registers a job with a schedule (one-shot at time T, or cron-style "every 5 minutes").
- **Cancel Job**: Stop future runs of a recurring job.
- **Query Status**: "Did my 9 AM report finish? What was the result?"
- **Retry Failed Jobs**: With exponential backoff and a max-retry limit.
- **Dead Letter Queue (DLQ)**: After N failures, move the job aside so it stops blocking the pipeline.

We will not discuss workflow / DAG support (that's Airflow / Temporal territory) or in-process scheduling (that's Quartz). This is a fleet-wide, durable, multi-tenant scheduler.

### Non-Functional Requirements

| Requirement         | Target            | Why?                                                                                  |
| ------------------- | ----------------- | ------------------------------------------------------------------------------------- |
| At-least-once       | No silent drops   | If a worker dies mid-job, the system must retry. Exactly-once is application-level.   |
| Scheduling latency  | < 5s p99          | A "9:00 AM" job should fire by 9:00:05 at the latest.                                 |
| Throughput          | ~6K jobs/sec peak | 100M jobs/day with 5× burst headroom.                                                 |
| High availability   | 99.99%            | If this is down, every cron in the company stops.                                     |
| Fault tolerance     | Worker death OK   | A crashed worker must not strand its jobs.                                            |

The "at-least-once with no duplicates" tension is the hardest part. We solve it with **lease + idempotency key**, not with "try really hard not to deliver twice."

### Capacity Math

| Metric                       | Value          |
| ---------------------------- | -------------- |
| Daily jobs                   | 100 million    |
| Average jobs/sec             | ~1,160         |
| Peak jobs/sec (5× burst)     | ~6,000         |
| Average job metadata size    | 2 KB           |
| Hot dataset (active 1 day)   | ~200 GB        |
| Annual archive               | ~70 TB         |
| Worker fleet                 | 1K – 10K nodes |
| Heartbeat overhead           | ~10K QPS       |

The data is **modest** (200 GB hot fits on one big SQL box). The QPS is **modest** (thousands, not millions). What blows up is the **completed-job history** — that's where tiered storage matters.

---

## Phase 2: Data Model

### Core Tables

```text
jobs
├── job_id (PK)
├── owner / tenant_id
├── job_type            // "send_email", "generate_report", ...
├── schedule            // cron expr, or one-shot timestamp
├── payload             // job-specific args (JSON / Protobuf)
├── retry_policy        // max_retries, backoff_base
├── next_run_at         // INDEXED — the only column the scheduler reads at scale
├── state               // ACTIVE | PAUSED | CANCELLED
├── created_at, updated_at

job_executions
├── execution_id (PK)
├── job_id (FK)
├── scheduled_run_at    // UNIQUE with job_id — see "idempotent enqueue" below
├── status              // PENDING | RUNNING | SUCCEEDED | FAILED | DLQ
├── attempt_number
├── worker_id           // who claimed it
├── lease_expires_at    // when the claim expires (TTL)
├── started_at, finished_at
├── result_url          // pointer to S3 if output is big

UNIQUE INDEX on (job_id, scheduled_run_at)
```

### The Strategy: Decouple Decision from Execution

The single most important architectural choice:

> The **Scheduler** decides *what should run now*. The **Worker** decides *I am running this*. They never share state directly — only through a queue.

This separation means:

- The scheduler can shard by `job_id` and run leader-election per shard. No global bottleneck.
- The worker fleet scales independently of the timer logic.
- If all workers die, the queue holds the work until they come back.
- If the scheduler dies mid-poll, the unique index on `(job_id, scheduled_run_at)` prevents duplicate enqueue when the new leader takes over.

```text
Submit API → jobs table → Scheduler (per-shard leader, polls next_run_at)
                                ↓ (enqueue)
                          Job Queue (Kafka / SQS, partitioned by tenant)
                                ↓ (pull + lease)
                          Workers (stateless, heartbeat their lease)
                                ↓ (write status)
                          job_executions table + Result Store (S3)
```

---

## Phase 3: How Services Talk to Us (API)

### Submit a Job

```http
POST /v1/jobs
Headers:
  X-Tenant-Id: team-payments
  Idempotency-Key: report_2026_05_01

Request:
{
  "job_type": "generate_daily_report",
  "schedule": "0 9 * * *",
  "payload": { "team": "payments" },
  "retry_policy": { "max_retries": 5, "backoff_base_seconds": 30 }
}

Response (200):
{ "job_id": "job_789", "next_run_at": "2026-05-02T09:00:00Z" }
```

### Cancel a Job

```http
POST /v1/jobs/{job_id}/cancel
```

Sets `state = CANCELLED`. Future scheduling skips this row.

### Query Status

```http
GET /v1/jobs/{job_id}/executions?limit=10

Response:
{
  "executions": [
    { "execution_id": "exec_111", "scheduled_run_at": "...", "status": "SUCCEEDED", "duration_ms": 4231 },
    { "execution_id": "exec_110", "scheduled_run_at": "...", "status": "FAILED", "attempt_number": 3 }
  ]
}
```

### Worker Pulls a Job (Internal)

```http
POST /v1/internal/jobs/claim
Headers: X-Worker-Id: worker_42
Request: { "max_jobs": 10, "lease_seconds": 60 }

Response:
{
  "claims": [
    {
      "execution_id": "exec_222",
      "job_id": "job_789",
      "payload": {...},
      "lease_expires_at": "2026-05-01T09:00:60Z"
    }
  ]
}
```

### Worker Reports Status

```http
POST /v1/internal/jobs/{execution_id}/heartbeat   // extend lease
POST /v1/internal/jobs/{execution_id}/complete    // success
POST /v1/internal/jobs/{execution_id}/fail        // failure → retry or DLQ
```

The Submit / Status APIs are **synchronous** for users. The Claim / Heartbeat / Complete APIs are between the worker fleet and the scheduler — typically over a queue protocol (Kafka, SQS) rather than HTTP.

---

## Phase 4: How It Works

### Architecture Overview

| Part                 | Role                                                                 |
| -------------------- | -------------------------------------------------------------------- |
| Submit API           | Stateless HTTP layer. Validates and writes to `jobs` table.          |
| Scheduler Tier       | Sharded by `job_id`. Each shard has a leader (etcd / ZooKeeper).     |
| Job Queue            | Kafka / SQS. Partitioned by `tenant_id`. Has DLQ topic per job class.|
| Worker Fleet         | Stateless. Pull from queue, claim job, run, heartbeat, ack.          |
| Job Metadata Store   | PostgreSQL (hot) + S3/Cassandra (cold history).                      |
| Reaper               | Background worker. Resets jobs whose lease has expired.              |
| Coordination         | etcd / ZooKeeper. Holds scheduler-shard leader leases.               |

### The Submit Flow

```sql
BEGIN;

INSERT INTO jobs (job_id, owner, job_type, schedule, payload, retry_policy,
                  next_run_at, state, created_at)
VALUES ($job_id, $owner, $type, $schedule, $payload, $policy,
        $first_run_at, 'ACTIVE', NOW())
ON CONFLICT (idempotency_key) DO NOTHING;

COMMIT;
```

Idempotent on the `Idempotency-Key` header so a client retry doesn't create two jobs.

### The Schedule Flow (Per-Shard Leader)

The leader of each scheduler shard runs a loop:

```sql
SELECT job_id, schedule, next_run_at
FROM jobs
WHERE state = 'ACTIVE'
  AND next_run_at <= NOW()
  AND shard_id = $my_shard
ORDER BY next_run_at
LIMIT 1000;
```

For each row, the leader **atomically claims the slot** in `job_executions` and enqueues:

```sql
INSERT INTO job_executions (execution_id, job_id, scheduled_run_at, status, attempt_number, created_at)
VALUES ($exec_id, $job_id, $next_run_at, 'PENDING', 1, NOW())
ON CONFLICT (job_id, scheduled_run_at) DO NOTHING
RETURNING execution_id;
```

The unique index on `(job_id, scheduled_run_at)` is the **dedup primitive**. Even if two leaders race during a failover, exactly one INSERT wins. The other gets a constraint violation and skips.

If the INSERT succeeded, push the execution to Kafka. Then advance `next_run_at` in the `jobs` table.

### The Execute Flow

A worker pulls from Kafka (lease semantics — SQS calls this "visibility timeout"):

1. Consume message → extract `execution_id`.
2. Mark `RUNNING` in `job_executions`, set `worker_id` and `lease_expires_at = NOW() + 60s`.
3. Run the actual job code.
4. Heartbeat every 20s to extend the lease (`heartbeat = lease_ttl / 3` is the standard ratio).
5. On success → mark `SUCCEEDED`, ack the message, write result URL.
6. On failure → mark `FAILED`, schedule a retry with exponential backoff + jitter, or move to DLQ if `attempt_number > max_retries`.

### The Reaper Flow (Cleanup)

If a worker dies between steps 3 and 5, no one will mark the job `SUCCEEDED` or `FAILED`. The reaper handles this:

```sql
UPDATE job_executions
SET status = 'PENDING',
    worker_id = NULL,
    lease_expires_at = NULL,
    attempt_number = attempt_number + 1
WHERE status = 'RUNNING'
  AND lease_expires_at < NOW()
  AND attempt_number < max_retries;
```

The job goes back into the queue automatically (most queues redeliver on visibility-timeout expiry too — the reaper is the belt-and-suspenders for cases where the queue forgets).

### Three-Layer Defense Against Stuck Jobs

1. **Heartbeat** keeps the lease alive while the worker is healthy.
2. **Lease TTL expires** in the queue → automatic redelivery.
3. **Reaper** sweeps the DB for stuck rows the queue missed.

---

## Phase 5: Handling Heavy Traffic

### 1. Sharding the Scheduler

A single scheduler thread can poll a few thousand QPS comfortably. At 100M jobs/day with bursts, you need many.

**Solution: Shard by `hash(job_id) % N`.** Each shard:

- Owns its own slice of `jobs` rows.
- Runs its own leader election in etcd.
- Polls only its own slice — `WHERE shard_id = $my_shard`.

No shard can stall the others. Failover is per-shard, so you only lose a fraction of capacity during a leader transition.

### 2. Avoiding Thundering Herd

A naive scheduler will fire 50,000 cron jobs at exactly `00:00:00` and crash everything downstream.

**Mitigations:**

- **Jitter the enqueue** — spread "due now" jobs across a few seconds.
- **Jitter worker poll intervals** — never let 10K workers wake up on the same wall-clock tick.
- **Per-tenant rate limit** at the queue — Kafka producer quotas, SQS throughput limits.

### 3. Tiered Storage

| Tier | Tech                       | What lives here                                       | Lifetime           |
| ---- | -------------------------- | ----------------------------------------------------- | ------------------ |
| Hot  | PostgreSQL                 | `jobs`, `job_executions` (last 7 days), index on `next_run_at` | Active + 7 days |
| Hot  | Kafka / SQS                | "Run now" messages, partitioned by tenant             | Minutes – hours    |
| Hot  | Redis                      | Per-tenant rate-limit counters, hot status cache      | Seconds – minutes  |
| Coordination | etcd / ZooKeeper   | Scheduler leader leases                               | Persistent         |
| Cold | S3 / Cassandra             | Completed-job history, logs, big payloads             | Years              |

Move executions older than 7 days from PostgreSQL to S3 / Cassandra nightly. The hot DB stays small and fast.

### 4. Why No Cache for Lease State?

You might want to put Redis in front to make claim-a-job fast. **Don't put the lease state there.**

The DB unique index on `(job_id, scheduled_run_at)` is the **source of truth** for "did this fire?" If you put it in Redis only, a Redis crash + restart can lose lock state and cause duplicate execution. Redis is fine for caching read-only status (`GET /jobs/{id}`); it is not fine as the authority for dedup.

### 5. Exponential Backoff with Jitter

```text
delay = min(max_delay, base * 2^attempt) * (1 + random(-0.1, 0.1))
```

Without jitter, every failed job retries in lockstep and you get a herd at every retry boundary. With jitter, retries spread out naturally.

### 6. Scaling Levels

| Scale              | Architecture                                              |
| ------------------ | --------------------------------------------------------- |
| < 10K jobs/day     | Single PostgreSQL, single scheduler, one worker pool.     |
| 100K – 10M jobs/day | Sharded PostgreSQL, sharded scheduler (4–16 shards).     |
| 100M jobs/day      | Many shards (32–128), Kafka queue, S3 history tiering.    |
| > 1B jobs/day      | Per-region clusters, event-driven triggers replace polling. |

### 7. Event-Driven Beats Polling at Scale

Polling `WHERE next_run_at <= NOW()` doesn't scale forever. For non-time-based jobs (e.g. "run when an S3 object lands"), use the source's event stream directly. This eliminates the timer poll entirely for that class of jobs.

---

## Common Mistakes

- **One scheduler does everything (timer + dispatch + execute).** Single point of failure; can't scale axes independently. Always separate scheduler / queue / worker tiers.
- **All workers poll the DB for "what's due."** Thundering herd at the top of the hour. Scheduler enqueues; workers consume from the queue.
- **Redis lock as the only dedup mechanism.** Redis can lose lock state under partition or restart. Pair it with a DB unique-index idempotency key.
- **At-least-once semantics with non-idempotent side effects.** Duplicate emails, double charges, double "delete user." Make every job idempotent — or wrap it in a transactional outbox.
- **No DLQ; failing jobs retry forever.** A poison message blocks the queue and exhausts workers. DLQ after N retries with exponential backoff + jitter.
- **Storing all job history in the hot SQL DB.** Table grows unbounded; queries slow; vacuum pain. Tier completed jobs to S3 / Cassandra after 7 days.
- **`heartbeat_interval >= lease_ttl`.** Worker still alive but lease expires; queue redelivers; job runs twice. Always `heartbeat = lease_ttl / 3`.
- **Cron-style "fire at 09:00 sharp" assumed accurate to ms.** Different nodes' wall clocks disagree. Use a single leader per partition; accept ±N seconds; document it.
- **Scheduler shard owns `next_run_at` but doesn't claim atomically.** Two shards both enqueue the same job. Unique index on `(job_id, scheduled_run_at)` before enqueue.
- **No visibility into job lag.** Pipelines silently fall behind by hours. Emit `time_in_queue`, `lag_p99`, `dlq_size`, `worker_busy_pct` as first-class metrics.

---

## Key Concepts for the Interview

| Topic                              | What to Say                                                                                                |
| ---------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| Decouple decision from execution   | Scheduler decides *what*; queue holds it; worker pulls. Each scales on its own axis.                       |
| At-least-once + idempotency        | Exactly-once doesn't exist on a network. We get there with `(job_id, scheduled_run_at)` as a unique key.   |
| Three-layer defense (lease)        | Heartbeat keeps lease alive; lease expiry → queue redelivery; reaper sweeps anything the queue forgot.     |
| Per-shard leader, not global       | One global leader = one global bottleneck. Shard the scheduler by `job_id % N`.                            |
| Unique index = cheapest dist lock  | Push dedup into the DB's primary key. It's transactional, durable, and free.                               |
| DLQ + exponential backoff + jitter | Standard pattern. Mention jitter explicitly to avoid synchronized retries.                                 |
| Tiered storage                     | Hot SQL (7 days) → cold S3/Cassandra (years). Job metadata is small; job history is huge.                  |
| Pull, not push                     | Workers pull from queue → automatic backpressure, no need to track which workers are alive.                |
| Observability is part of the design | Job lag, DLQ depth, worker heartbeat ratio, lease-expiry rate. Without these, undebuggable in production. |

---

## Wrap-Up

| Aspect                          | Solution                                          | Why?                                                  |
| ------------------------------- | ------------------------------------------------- | ----------------------------------------------------- |
| Scale to 100M/day               | Sharded scheduler + queue + stateless workers     | Each tier scales on its own axis.                     |
| Avoid duplicate execution       | Lease + DB unique index on `(job_id, exec_id)`    | Belt + suspenders; the DB is the source of truth.    |
| Survive worker crashes          | Heartbeat-based lease + reaper                    | Stuck jobs re-enter the queue automatically.          |
| Survive scheduler crashes       | Per-shard leader election (etcd / ZooKeeper)      | No global SPOF; failover in seconds.                  |
| Avoid thundering herd at 09:00  | Jittered enqueue + worker-side jittered poll      | Smooth out cron-aligned bursts.                       |
| Handle poison jobs              | DLQ after N retries, exponential backoff + jitter | Don't let one bad job block the pipeline.             |
| Long-tail history               | Tier completed jobs from SQL → S3 / Cassandra     | Hot DB stays small and fast.                          |
| Big job outputs                 | Workers write to S3; URL only in DB               | Metadata layer stays lightweight.                     |
| Clock drift                     | NTP + per-shard leader owning `next_run_at`       | Avoid wall-clock comparisons across nodes.            |
| Operability                     | Emit `lag_p99`, `dlq_size`, `lease_expiry_rate`   | The scheduler must be debuggable from metrics alone.  |
