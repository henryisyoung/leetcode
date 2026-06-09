# Translating Yammer GDPR Experience for Privacy Interviews

A playbook for framing pre-Microsoft / Microsoft-era Yammer GDPR work in the vocabulary senior privacy interviewers (Waymo, Meta, Apple, etc.) want to hear.

---

## Why Yammer is a perfect GDPR case study

Yammer in the 2017-2018 GDPR push had every painful pattern at once:

- **Multi-tenant B2B** (data-processor model, not B2C controller model)
- **Legacy pre-Microsoft data stores** (AWS-era MySQL/Mongo/ES sprawl)
- **Rich UGC** (posts, files, reactions, polls, praise, groups)
- **Cross-tenant collaboration** (external networks)
- **Forced migration into Microsoft 365 Substrate**

Almost every GDPR design pattern shows up in this surface area.

---

## Lead with: "We deprecated many DBs"

This is the **single most important** pattern and should be the opener. Technical name: **data minimization + source-of-truth consolidation**.

### The before / after

```
BEFORE GDPR (typical Yammer-era sprawl)
────────────────────────────────────────
                   ┌──── Yammer User DB (MySQL/Mongo)
                   ├──── Yammer Search index (ES)
   user 123 ──────►├──── Analytics warehouse (Vertica/Cosmos)
                   ├──── Push notification service DB
                   ├──── Mobile sync cache (DynamoDB)
                   ├──── Audit log store
                   ├──── Email digest service
                   └──── 15+ other downstream services

   To answer "delete user 123": 20+ teams, 20+ runbooks,
   no single owner, no proof of completion.

AFTER GDPR
──────────
                   ┌──── Microsoft Substrate (authoritative)
   user 123 ──────►│        │
                   │        ▼ change feed
                   └──── downstream services subscribe,
                          delete-on-tombstone

   Old service-local DBs deprecated / migrated.
   One DSR request → one tombstone event → fan-out.
```

### The interview-ready paragraph

> "Pre-GDPR, Yammer had ~N services each with their own copy of user data — legacy from the pre-Microsoft AWS architecture. For GDPR Article 17 we couldn't credibly promise deletion across that surface area. We led a multi-quarter effort to **consolidate user-identifying data into the Microsoft 365 Substrate as the single source of truth**, then **deprecated the per-service user DBs** in favor of a **change-feed / tombstone model** where downstream services subscribe to deletion events and purge their local derived data. This reduced the DSR fan-out from N teams to 1 pipeline and gave us a single auditable proof of deletion."

That paragraph alone is a senior-staff-level GDPR answer.

---

## The full Yammer GDPR checklist

### 1. DSR pipeline (the visible product of GDPR)

Microsoft built a unified **Data Subject Request portal** that fanned out across all M365 services. Yammer plugged into it.

| Right | Article | What Yammer had to build |
|---|---|---|
| Access | 15 | Export user's posts, comments, reactions, files, group memberships, profile, presence history |
| Portability | 20 | Same data but in **machine-readable JSON/CSV** with documented schema |
| Erasure | 17 | Hard-delete the user; **redact** their content from threads (or anonymize author = "former user") |
| Rectification | 16 | Profile edit flows |
| Restrict / Object | 18, 21 | Opt-out of analytics, ML training, marketing |

**The hard part of erasure on a social platform:** the **author-of-a-thread** problem. If you nuke user 123 but they wrote a popular post with 200 replies, what happens to the thread? Standard answer:

- **Anonymize the author field** ("Former Yammer user")
- **Delete the profile**
- **Keep the post body** since it's now part of *other* users' conversation context
- Unless the requester explicitly asks for content deletion too

This nuance is what interviewers love.

### 2. The B2B tenant model — the secret weapon

**Most candidates miss this** and it's where Yammer experience is genuinely differentiating.

| | B2C (Facebook, consumer Google) | B2B (Yammer, Slack, Teams) |
|---|---|---|
| Data controller | Platform itself | Customer company (Contoso) |
| Data processor | N/A | Platform |
| Who exercises DSR | End user directly | Tenant admin on behalf of employee |
| Consent flow | Per user | Per tenant DPA |
| Deletion scope | Global user | **Tenant-scoped** (same person may exist in other tenants) |

Strong opener:

> "I worked on the **data-processor side** of GDPR in a multi-tenant B2B platform, where DSRs are **tenant-scoped** and the data controller is the customer, not the end user."

Most interviewers have only seen the B2C framing.

### 3. Cross-network / external messaging

Yammer had **external networks** where users from different companies collaborated. Tenant-scoped deletion gets nasty:

> User A from Contoso sends a message into a shared network owned by Fabrikam — whose deletion authority governs that message?

Standard answer:

- The **owner of the network** is the controller of messages in it
- The **sender's tenant** can delete from the sender's view (a tombstone)
- Both copies must be reconciled

Great interview material — shows you've thought about **multi-party data**.

### 4. EU data residency

Pre-GDPR, Yammer ran in US data centers. GDPR + (later) Schrems II forced **regional storage**:

- Stamp every tenant with a **geo affinity** at provisioning
- Route all reads/writes to the regional stamp
- Disable cross-region replication for those tenants
- Audit every downstream service for accidental US egress (logs, telemetry, support tooling)

### 5. Telemetry & logging scrubbing

Yammer's client apps sent tons of usage telemetry via Microsoft's **Aria / OneCollector** pipeline. GDPR forced:

- **Classification** of every telemetry field as PII / non-PII / pseudonymous
- **Opt-out** flows ("Optional diagnostic data")
- **Server-side scrubbing** of PII from already-collected logs
- **Retention caps** on logs (90 days typical)
- **No PII in metric tags / dimension fields** (they're effectively forever in time-series stores)

If you worked on **deprecating logging code paths** or **scrubbing log fields**, that's also a GDPR story.

### 6. Retention policies

Pre-GDPR default: "keep forever." GDPR forces **purpose-limited retention**:

| Data class | Typical retention |
|---|---|
| Posts | While tenant is active, N days after tenant cancels |
| Audit logs | Compliance window (e.g., 1 year), then purge |
| Telemetry | 30-90 days |
| Backups | Documented rolling window |
| Deleted-user tombstones | Forever (not PII — just `user_id` + `deleted_at`) |

### 7. Sub-processor management

Every third party touching Yammer data (Apple/Google push gateways, SendGrid, CDNs, captcha, support tools) became a **sub-processor** requiring a **DPA**.

Engineering side: audit every outbound integration, often **rip out vendors that wouldn't sign EU SCCs** (Standard Contractual Clauses). Another flavor of "deprecated services" you might remember.

### 8. Audit trail = proof of compliance

Deletion itself is half the work. The other half is **proving it happened**:

- Every DSR gets a request ID
- Every service logs `(request_id, user_id, action, timestamp, result)` to an immutable audit store
- Portal shows customer a unified "completed at" with per-service breakdown
- Internal compliance team can pull audit trail in a regulator inquiry

---

## The Yammer → Waymo bridge

The framing that makes you sound senior in a Waymo privacy interview:

> "At Yammer I worked on the **data-processor side** of GDPR for a multi-tenant B2B social platform. The two biggest patterns we drove were:
>
> 1. **Source-of-truth consolidation** — deprecating per-service user DBs in favor of a single authoritative store with change-feed-driven fan-out.
> 2. The **tenant-scoped DSR pipeline** that integrated with the Microsoft 365 compliance portal.
>
> The hardest design problems were:
> - **Multi-party data** (cross-network messages where multiple tenants had legitimate interest)
> - **Derived data** (search indexes, ML features, analytics) that had to subscribe to deletion tombstones
> - **Proving deletion** with an audit trail rather than just executing it
>
> I'd expect Waymo to have analogous problems with **bystander data** captured by the AV fleet:
> - **Multi-party**: the rider, pedestrians, other drivers
> - **Derived**: perception models trained on raw sensor data
> - **Audit trail**: defensible per-DSR proof of deletion across raw, derived, and model artifacts."

That last sentence flips it from "candidate has past experience" to **"candidate already understands our problem space."**

---

## Quick-reference: GDPR articles to name-drop

| Article | What it covers | Yammer example |
|---|---|---|
| Art. 5 | Principles (lawfulness, minimization, accuracy, storage limitation) | Retention policies |
| Art. 6 | Lawful basis for processing | Tenant DPA = contract basis |
| Art. 15 | Right of access | Export pipeline |
| Art. 16 | Rectification | Profile edit |
| Art. 17 | Erasure / RTBF | Tombstone fan-out |
| Art. 18 | Restriction | Pause processing |
| Art. 20 | Portability | JSON/CSV export with documented schema |
| Art. 21 | Object | Opt-out of profiling / marketing |
| Art. 25 | Privacy by design / by default | Source-of-truth consolidation |
| Art. 28 | Processor obligations | Yammer-as-processor model, sub-processor DPAs |
| Art. 30 | Records of processing activities | Per-DB / per-service inventory |
| Art. 32 | Security of processing | Encryption, access control, audit |
| Art. 33 | Breach notification (72h) | Incident response runbook |
| Art. 35 | DPIA (Data Protection Impact Assessment) | Required for new high-risk features |
| Art. 44-49 | International transfers | EU data residency, SCCs, Schrems II |

Name-dropping 2-3 of these by article number in an answer is enough to signal depth without sounding like a lawyer.
