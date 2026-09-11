# Design a Ticket System — Airbnb Internal Support

Design an internal ticketing system where users can submit tickets via email,
API, or contact form. Support agents can view and claim tickets based on routing
rules such as language, location, issue type, and priority. Managers can view
average first response time and average close time over a selected time window.

**Time budget**

| Minutes | What you do |
| ------- | ----------- |
| 0–5     | Clarify requirements + scale |
| 5–10    | APIs and ticket lifecycle |
| 10–25   | Architecture + data model |
| 25–45   | Deep dives: routing/claiming + metrics |
| 45–55   | Failure handling + consistency |
| 55–60   | Trade-offs and wrap-up |

---

## 0–5 min: Clarify

### Functional requirements

- Users can submit tickets from:
  - **Email**: support@airbnb.com, reply-to thread handling.
  - **API**: internal services or partner integrations.
  - **Contact form**: web/app form with structured fields.
- Normalize all channels into one `Ticket` object.
- Agents can:
  - view queues they are eligible for
  - filter by language, location, priority, category, SLA
  - claim one ticket atomically
  - reply, add internal notes, assign/transfer, close/reopen
- Managers can view metrics for a time range:
  - average first response time
  - average close time
  - optionally by queue/team/language/location/category

### Non-functional requirements

| Requirement | Target |
| --- | --- |
| Ticket creation | durable, no lost inbound messages |
| Claim correctness | one ticket claimed by one agent at a time |
| Agent queue latency | p95 < 500 ms for filtered queue page |
| Metrics freshness | near real-time or 1–5 min delayed is okay |
| Auditability | every state change and agent action recorded |
| Search | by ticket id, user id, reservation id, text |

### Capacity estimate

```text
10 M tickets/day peak incident day
avg ticket metadata ~5 KB
messages/notes per ticket ~5
write QPS avg ~100, peak ~5K/sec during incidents
agents ~10K globally
queue reads are much higher than writes
```

Take-away: ticket writes are durable workflow events; agent queue reads need
indexes; metrics should be computed from an event stream, not heavy OLTP scans.

---

## 5–10 min: APIs

### Create ticket

```http
POST /v1/tickets
Idempotency-Key: contact_form_submit_abc
{
  "source": "contact_form",
  "user_id": "u_123",
  "reservation_id": "r_456",
  "category": "payment",
  "language": "zh-TW",
  "location": "TW",
  "priority": "normal",
  "subject": "Refund question",
  "body": "...",
  "attachments": ["s3://..."]
}
→ { "ticket_id": "t_789", "status": "open" }
```

### Agent queue

```http
GET /v1/agent/queue?language=zh-TW&location=TW&category=payment&status=open&cursor=...
→ {
  "tickets": [
    { "ticket_id": "t_789", "priority": "normal", "created_at": "...", "sla_due_at": "..." }
  ],
  "next_cursor": "..."
}
```

### Claim ticket

```http
POST /v1/tickets/t_789/claim
{ "agent_id": "a_42" }
→ { "ticket_id": "t_789", "status": "claimed", "assignee": "a_42" }
```

Claim must be atomic:

```sql
UPDATE tickets
SET assignee_id = :agent_id, status = 'claimed', claimed_at = now()
WHERE ticket_id = :ticket_id
  AND status = 'open'
  AND assignee_id IS NULL;
```

If affected rows = `1`, claim succeeded. If `0`, someone else got it first.

### Reply / close

```http
POST /v1/tickets/t_789/replies
{ "agent_id": "a_42", "body": "..." }

POST /v1/tickets/t_789/close
{ "agent_id": "a_42", "resolution": "refund_processed" }
```

### Manager metrics

```http
GET /v1/metrics/tickets?from=2026-06-01&to=2026-06-07&group_by=language,location
→ [
  {
    "language": "zh-TW",
    "location": "TW",
    "ticket_count": 12000,
    "avg_first_response_seconds": 620,
    "avg_close_seconds": 18700
  }
]
```

---

## High-Level Architecture

```text
Email Gateway ─────┐
API Gateway ───────┼──▶ Ingestion Service ──▶ Ticket DB
Contact Form ──────┘          │                 │
                              │                 ├──▶ Search Index
                              │                 ├──▶ Queue Index
                              │                 └──▶ Ticket Events
                              │                         │
                              ▼                         ▼
                    Attachment Store           Metrics Pipeline
                                                     │
                                                     ▼
                                              Metrics Warehouse

Agent UI ──▶ Queue Service ──▶ Queue Index / Ticket DB
Agent UI ──▶ Claim Service ──▶ Ticket DB conditional update
Manager UI ──▶ Metrics API ──▶ Metrics Warehouse / OLAP store
```

---

## Data Model

### `tickets`

```sql
ticket_id PK
source ENUM(email, api, contact_form)
requester_user_id
reservation_id nullable
category
language
location
priority
status ENUM(open, claimed, waiting_on_user, closed, reopened)
assignee_id nullable
created_at
claimed_at nullable
first_agent_response_at nullable
closed_at nullable
sla_due_at
version
```

### `ticket_messages`

```sql
message_id PK
ticket_id
sender_type ENUM(user, agent, system)
sender_id
body
created_at
is_public
```

### `ticket_events`

Append-only audit/event stream:

```sql
event_id PK
ticket_id
event_type ENUM(created, claimed, replied, transferred, closed, reopened)
actor_id
old_status
new_status
created_at
metadata_json
```

This is the source for metrics and audit. Do not compute historical metrics by
repeatedly scanning mutable ticket rows.

### `agent_profiles`

```sql
agent_id PK
team_id
languages[]
locations[]
categories[]
skill_level
active_status
max_concurrent_tickets
```

---

## Deep Dive 1: Routing Rules

Routing decides which agents are eligible to see a ticket.

Example rule:

```text
ticket.language in agent.languages
AND ticket.location in agent.locations
AND ticket.category in agent.categories
AND agent.active_status = online
```

Implementation options:

- **Simple version**: compute eligibility at query time using indexed ticket
  fields and agent profile filters.
- **Scalable version**: maintain a `queue_membership` or queue index per
  rule bucket.

Example queue key:

```text
queue: language=zh-TW, location=TW, category=payment, priority=normal
```

Ticket creation writes the ticket into one or more eligible queue buckets. Agent
queue read pulls from buckets matching the agent profile.

Important: queue membership is an optimization, not the source of truth. Claim
must still validate the ticket status and agent eligibility against the DB.

---

## Deep Dive 2: Claiming Correctness

Problem: two agents see the same ticket and both click claim.

Solution: optimistic conditional update in the primary DB.

```text
Agent A claims t1 ─┐
Agent B claims t1 ─┼──▶ Ticket DB conditional update
                  │
                  ├── one succeeds
                  └── one gets 409 Conflict
```

API behavior:

```http
409 Conflict
{ "error": "ticket_already_claimed", "assignee": "a_42" }
```

After claim succeeds:

1. Update ticket row.
2. Append `claimed` event.
3. Remove or hide ticket from queue index asynchronously.

If queue index lags, another agent may still see stale ticket, but claim will
fail safely.

---

## Deep Dive 3: Metrics

### Definitions

First response time:

```text
first_agent_response_at - created_at
```

Close time:

```text
closed_at - created_at
```

Only count tickets:

- first response metric: tickets with at least one public agent reply
- close metric: tickets that reached `closed`

### Pipeline

```text
ticket_events
   └──▶ stream processor / batch job
          group by time bucket + dimensions
          compute sums and counts
              first_response_sum_seconds
              first_response_count
              close_sum_seconds
              close_count
          write metrics table
```

Metrics table:

```sql
ticket_metrics_hourly
bucket_start
team_id
language
location
category
ticket_created_count
first_response_sum_seconds
first_response_count
close_sum_seconds
close_count
```

Query:

```sql
avg_first_response = SUM(first_response_sum_seconds) / SUM(first_response_count)
avg_close_time     = SUM(close_sum_seconds) / SUM(close_count)
```

Why sums/counts, not precomputed averages:

```text
average of averages is wrong if buckets have different counts
```

---

## Failure Modes

- **Email duplicate delivery**: use idempotency key from message-id/thread-id.
- **API retry duplicates**: require `Idempotency-Key`.
- **Queue index stale**: okay; claim DB conditional update is authoritative.
- **Metrics lag**: show freshness timestamp in manager UI.
- **Agent eligibility changed after ticket appears in queue**: re-check
  eligibility at claim time.
- **Attachment upload fails**: create ticket with attachment status pending or
  reject before ticket creation depending on UX.

---

## Interview Summary

> "I would normalize email, API, and contact-form submissions into a single
> Ticket model, store authoritative ticket state in a transactional DB, and emit
> append-only ticket events for audit and metrics. Agents read from indexed
> queues based on language/location/category rules, but claiming is a conditional
> DB update so only one agent can own a ticket. Manager metrics should come from
> event-derived sums and counts over time windows, not expensive scans or
> average-of-averages."
