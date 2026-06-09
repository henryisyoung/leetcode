# Waymo Privacy Engineer — Full Prep Guide (Modules 1–6 + Quizzes)

A from-scratch study guide for the Waymo Privacy Engineer system-design round.
Assumes zero prior GDPR/privacy knowledge. Read top to bottom; reread Module 6
(cheat sheet) on the morning of the interview.

---

## Module 1 — The concepts (privacy from zero)

### Personal data
Anything that can identify a person, **directly or in combination** with other data.
- Direct: name, email, face, license plate, precise GPS trace.
- Indirect/combined: timestamp + location + device can re-identify even without a name.
- **Pseudonymized** data (real id swapped for a token) **still counts** as personal data.
- **Truly anonymous** data (no way back to a person, even combined) is *not* personal data
  and falls outside GDPR.

### Lawful basis (you need ONE to touch personal data)
This is the golden rule: before collecting/using data, you must have a legal reason.
- **Consent** — the person freely, specifically, and unambiguously agreed.
- **Contract** — needed to deliver a service they asked for (e.g. a rider's trip history).
- **Legal obligation** — a law requires it (tax records, lawful subpoena).
- **Legitimate interest** — a justifiable business need that doesn't override the person's
  rights (requires a balancing test). *This is the basis for filming bystanders.*
- (+ vital interest, public interest — rarely the answer in interviews.)

### Roles
- **Data controller** — decides *why* and *how* data is processed. **Waymo is the controller.**
- **Data processor** — acts only on the controller's instructions (e.g. a cloud vendor).

### Core principles
- **Lawfulness, fairness, transparency**
- **Purpose limitation** — use data only for the stated reason; don't silently repurpose.
- **Data minimization** — collect only the minimum needed.
- **Accuracy** — keep it correct.
- **Storage limitation** — keep it only as long as needed (retention).
- **Integrity & confidentiality** — secure it (encryption, access control).
- **Accountability** — be able to *prove* you do all of the above.

### Data subject rights
- **Access** — "show me my data."
- **Erasure / Right to be Forgotten (RTBF)** — "delete me." *Not absolute* (legal holds win).
- **Rectification** — fix wrong data.
- **Portability** — export my data in a usable format.
- **Object / Restrict** — stop or limit certain processing.

### Obligations
- **Breach notification within 72 hours** to the regulator.
- **DPIA** (Data Protection Impact Assessment) for risky/large-scale processing.
- **Privacy by design & by default** — bake privacy in from the start, don't bolt it on.
- **Records of processing** — maintain an inventory of what you do with data.

---

## Module 2 — How interviewers actually ask

Privacy questions rarely say "explain GDPR." They're disguised as concrete scenarios.
Your job is to **map the scenario to the underlying concept** and respond with structure.

Two warm-up gut-checks (with model answers):

**Q1 — A pedestrian is filmed by a Waymo car. Is that allowed? What must Waymo do?**
- Lawful basis: **legitimate interest** (safe operation of an AV) — the pedestrian can't
  realistically consent.
- Must: do a **balancing test / DPIA**, **minimize** intrusion (blur faces & plates,
  don't film private interiors), and **not identify** the person beyond what's needed.

**Q2 — Why is "delete me" (RTBF) hard for Waymo specifically?**
- A rider's data is **scattered across many independent systems** (accounts, trips, GPS,
  support, payments, analytics, ML training sets, logs).
- Needs **fan-out orchestration**, **proof** of deletion, handling of **immutable backups**
  and **trained ML models**, and **legal exceptions** (data you must keep).

---

## Module 3 — The 8-bucket checklist (your in-room reflex)

A fixed checklist you mentally walk every time, ordered by the **lifecycle of a piece of
data** ("we got it" → "we got rid of it"). You won't use all 8 per answer; scanning them
guarantees you never blank.

1. **Data inventory** — *What do we have, where does it live, who is it about?*
   You can't protect (or delete) what you haven't catalogued.
2. **Lawful basis & consent** — *Why are we allowed to have it?* How is consent captured,
   withdrawn, and proven?
3. **Purpose limitation & minimization** — *Only the stated reason, only the minimum.*
4. **Storage architecture** — *Stored safely?* Encryption at rest, KMS (key management),
   data classification, **region pinning** (keep EU data in the EU).
5. **Access control** — *Who can see it, and can we prove who looked?* Least privilege,
   just-in-time elevation, **audit logging**.
6. **Retention & deletion** — *When and how do we get rid of it?* Schedule per type; actually
   purge, including **backups, caches, logs, ML sets**.
7. **Subject rights pipeline** — *How does a person exercise access / RTBF?* ~30-day SLA,
   fan-out to every system, proof.
8. **Incident response** — *What happens when it goes wrong?* Detection, **72h** regulator
   notice, customer notice, post-mortem.

**How to use them:** when you hear a prompt, silently scan all 8, then **say the 2–3 it's
really about** before designing.

| Interviewer says...                       | Buckets it's really about              |
|-------------------------------------------|----------------------------------------|
| "Filming pedestrians on public streets"   | 2 (lawful basis), 3 (minimization), 4  |
| "A rider wants to be deleted"             | 7 (subject rights), 6 (deletion), 1    |
| "We got breached"                         | 8 (incident response), 5 (access)      |
| "Launch in Munich"                        | 4 (storage/residency), 2, cross-border |
| "Train ML on ride data"                   | 3 (purpose), 2 (consent), 6 (deletion) |

Opening line in the room:
> "Let me think about which buckets this hits... this is mostly a **purpose-limitation** and
> **lawful-basis** question with a **deletion** angle. Let me design around those three."

---

## Module 4 — Worked end-to-end designs

Every privacy design follows the same **spine**:
1. **Clarify** → 2. **Frame with 2–3 buckets** → 3. **Architecture** →
4. **Hard parts** (where the points are) → 5. **Tie back to principles**.

### 4A — Face & license-plate anonymization

**Clarify:** Is this **stored/exported data** or the **live driving feed**? (You *cannot* blur
the live perception feed — the car must see pedestrians to avoid them. Anonymization is for
stored data.) What are we protecting against? Data volume (petabytes/day → must be automated)?
Who consumes it downstream?

**Frame:** mostly **minimization** (3), plus **lawful basis** (2, legitimate interest) and
**access control** (5) for the raw originals.

#### Requirements
**Functional**
- Ingest sensor segments (camera + LiDAR) from the fleet at high throughput.
- Anonymize faces + plates; produce an "anonymized" derivative usable by ML, analytics, support.
- Privileged, time-boxed access to raw for crash investigations and labeling.
- Index segments by vehicle, time, geo, sensor for downstream retrieval.

**Non-functional**
- **Hard invariant:** no un-anonymized footage ever reaches a non-privileged consumer.
- Anonymization recall on a labelled bench **≥ 99.5%** (faces + plates), tracked as a safety SLI.
- Raw retention bounded (≤ 30 days default); anonymized retention longer.
- Encryption at rest with **per-vehicle / per-day** data keys (so we can crypto-shred a vehicle's
  history without scrubbing every backup).
- Residency: EU vehicles' data stays in EU (separate buckets, separate KMS realm).

#### Capacity (back-of-envelope)
- Fleet 10K vehicles × 8 h/day × ~50 GB/h compressed = **~4 PB/day raw**.
- Sustained pipeline throughput: **~46 GB/s**.
- GPU anonymization at ~1 GB/s per modern instance → ~50 instances steady-state, ~2× for
  catch-up after outages.
- Catalog rows: ~1 segment/min/vehicle (≈60-s segments) × 10K vehicles × 8 h ≈ **5M segments/day**
  → ~1.8 B/yr (consistent with 4 PB/day ÷ 10K ÷ 8 h ≈ 50 GB/h ≈ ~830 MB/min per vehicle).
- KMS calls: envelope-encrypt per file with cached data keys; KMS API itself ≪ 1 op/s.

#### API surface
```
# Vehicle uploads the EDGE-BLURRED segment (gRPC streaming, resumable, auth = mTLS + vehicle cert)
POST /v1/segments
  body: { vehicleId, startTs, endTs, sensors[], manifest, sha256, edgeModelVersion, edgeRecall }
  → { uploadId, signedPartUrls[] }   # lands in S3 Quarantine, not an ML-readable tier

# Downstream consumers
GET  /v1/segments/anon?vehicleId=&from=&to=         → presigned URLs (anon tier only)
GET  /v1/segments/{id}/meta                         → catalog row
POST /v1/segments/search { geo, time, sensorTypes } → list of segmentIds (anon tier)

# Privileged raw access (time-boxed, fully audited) — triggers an on-demand PULL of raw
# from the vehicle into the locked raw-pull bucket; raw is not in the cloud by default.
POST /v1/raw/access { segmentId, reasonCode, ticketId, durationMins }
  → { stsCredentials, expiresAt }   # short-lived, scoped to that single segment
```

#### Data model
- `segments` (Postgres, sharded by `vehicleId`):
  ```
  segmentId PK, vehicleId, startTs, endTs, regionCode, sensorTypes[],
  quarantineS3Path, anonS3Path, anonKmsKeyArn,
  status ENUM(edge_anonymized, anonymizing, anonymized, failed, expired),
  edgeModelVersion, edgeRecall, cloudModelVersion, cloudRecall, retentionExpiresAt
  ```
  (Note `status` starts at `edge_anonymized` — by the time anything reaches the cloud it has
  already been blurred once on the vehicle. There is no `uploaded`/raw-in-cloud state on the
  default path; raw only appears in the cloud via the explicit-pull bucket below.)
- Geo + time index for search → **PostGIS** (or ES) on the catalog only — never on raw bytes.
- Quarantine bucket: `s3://quarantine-segments-<region>/<vehicleId>/<date>/<segmentId>.tar`
  (edge-blurred, no ML IAM) with 30-day lifecycle, KMS, **deny by default IAM**.
- Anon bucket: `s3://anon-segments-<region>/...` with broader IAM, separate KMS realm.
- Pulled-raw bucket (explicit access only): `s3://raw-pull-<region>/...` — MFA-delete, very
  short TTL, every read audited; raw never lands here on the default path.

**Why these choices.** Postgres for catalog: row-level transactions, easy for the "deletion +
status" tag-based gate. S3 for blobs: cheap, lifecycle-native. PostGIS over ES for geo: fewer
moving parts and the geo index is itself sensitive (surveillance risk) so we want it in the
same blast radius as the catalog. Per-vehicle KMS keys make crypto-shred a one-API-call op.

#### Architecture — dual-stage anonymization, defense in depth
Blur at **both** the edge (on-vehicle, best-effort, minimize raw leaving the car) **and** the
cloud (authoritative, higher-recall). Neither alone is enough: edge is compute-limited;
cloud-only means raw PII already crossed the trust boundary.
```
ON-VEHICLE (edge)
  raw frames ─► quantized detector (faces+plates) ─► best-effort blur IN-PLACE (irreversible)
            ─► local buffers: pre-blurred (default upload) + raw (encrypted, short TTL,
               stays on the car unless explicitly pulled)
                              │  mTLS upload (pre-blurred is the default path)
                              ▼
CLOUD
  Ingest gRPC ──▶ S3 Quarantine (pre-blurred; NO ML access yet) ──┐
                       │                                          │ Kafka: edge-blurred-segments
                       └──catalog row──▶ Postgres
                                              │
                              ┌───────────────┘
                              ▼
                  Anonymizer Workers (GPU autoscale group; authoritative pass)
                  detect (bigger model) → second-pass blur on edge MISSES → verify recall
                              │
                              ├──▶ S3 Anon  (broad IAM, separate KMS)
                              ├──▶ catalog: status=anonymized, edgeRecall, cloudRecall, attestation
                              └──▶ Kafka: anon-segments  (downstream ML/analytics)

  S3 Quarantine ──30-day lifecycle──▶ expired  (gated: DOES NOT fire if status != anonymized)
  Privileged raw access ──▶ explicit on-demand pull from the vehicle into a tightly-locked
                            raw bucket; short-lived STS; every byte logged in the audit log
```
- **Edge tier:** quantized model, in-place irreversible blur, minimizes raw PII crossing the
  upload boundary (GDPR Art. 5(1)(c) minimization) and saves bandwidth.
- **Quarantine tier:** edge-blurred but not yet authoritative — no downstream ML access.
- **Anonymized tier:** doubly-blurred (edge + cloud); broad downstream access is safe.
- **Raw** never crosses the upload link by default; it stays on the car (encrypted, short TTL)
  and is pulled only for crash investigation / labeling, into a separate locked bucket.

**Anonymization service (cloud pass):** detect with the authoritative model (catches edge
misses) → redact (prefer solid mask / heavy irreversible blur) → **verify recall** (no model is
100%; bias toward **over-redaction**, treat recall as a tracked **safety metric**, human spot-audit).

#### Sequence — one segment, end to end
1. Vehicle runs the **on-vehicle detector** and blurs faces/plates **in-place** in the
   pre-blurred buffer; raw stays local, encrypted, short TTL.
2. Vehicle resumable-uploads the **pre-blurred** segment to **S3 Quarantine** via Ingest (mTLS);
   on completion gets a `segmentId`.
3. Ingest writes catalog row `status=edge_anonymized` (carrying `edgeModelVersion`, `edgeRecall`)
   and publishes to Kafka `edge-blurred-segments`.
4. Anonymizer consumer claims the segment (consumer-group dedupe), pulls the pre-blurred blob
   using a short-lived data key from KMS, runs the **authoritative cloud detector** to catch
   edge misses, and applies a second-pass blur.
5. Verify recall on a per-frame sample; if `cloudRecall < SLI`, **fall back to whole-frame blur
   for that segment** and flag for human spot-audit.
6. Write the doubly-anonymized blob to S3 Anon, update catalog `status=anonymized` with the full
   attestation `{edgeModelVersion, edgeRecall, cloudModelVersion, cloudRecall}`, publish to
   `anon-segments`.
7. Quarantine entries expire at T+30d **iff** `status=anonymized` (a deny-by-default S3 tag-based
   policy keeps un-anonymized data from being deleted accidentally).
8. **Raw access** is a separate path: explicit pull from the vehicle's local raw buffer into a
   tightly-locked cloud raw bucket, time-boxed STS, every read audited. Raw defaults to never
   crossing the upload boundary.

#### Scaling, SLOs, failure modes
- **GPU is the bottleneck.** Autoscale the cloud pass on Kafka `edge-blurred-segments` lag;
  pre-warm during peak fleet hours. SLO: P95 anonymization latency < 6 hours from upload
  (most consumers are batch).
- **KMS quota** is 5500 ops/sec per region; envelope encryption with cached data keys keeps
  actual KMS calls ≪ that even at 4 PB/day.
- **Egress cost** is real. Edge pre-blur already reduces what crosses the upload link (only
  pre-blurred segments leave; raw stays on the car). Second lever: regional pipelines so the
  cloud pass doesn't cross regions.
- **Edge/cloud version skew** — a better edge OR cloud model can mean older segments under-blurred.
  Per-segment attestation (`edgeModelVersion`, `cloudModelVersion`) lets you find + re-process them.
- **Failure of cloud detection** → over-redaction fallback, *never* promote the segment to anon tier.
- **Failure of upload mid-segment** → resumable; partial files are GC'd by lifecycle.
- **Worker crash** → Kafka consumer-group rebalance + idempotent write (segmentId-keyed).

**Hard parts:**
- **Can't delete raw immediately** — needed for crash investigations/legal defense → keep raw
  on the vehicle (encrypted, short TTL) and, when pulled, in a tightly-locked bucket that
  auto-expires; logged + time-boxed unlock to access.
- **Re-identification** — blurring a face leaves gait, clothing, location+timestamp →
  **k-anonymity** mental model; coarsen/aggregate location & time.
- **LiDAR is biometric too** (3D shape/gait) — privacy isn't just the camera; blur/voxel-coarsen
  point clouds at both stages.
- **Edge vs cloud is "both", not "either"** — edge blur minimizes raw leaving the car (weaker,
  compute-limited model); the cloud pass is the authoritative higher-recall net. Defense in
  depth; the upload link is the trust boundary.

**Principles:** privacy by design (default output is anonymized), minimization, defense in depth.

**Signal sentence:**
> "Detect-and-blur faces, plates, and other PII at the edge (on-vehicle, minimize what leaves
> the car) AND in the cloud (authoritative, higher-recall) before it enters training sets;
> anonymize stored data, not the live driving feed; over-redact and treat detection recall as a
> safety metric."

### 4B — Right to be Forgotten / deletion

**Clarify:** True erasure vs. account closure? SLA (~30 days → **async**)? **Legal holds**
(data we must keep — crash footage under litigation, tax records)? Scope = account-linked data
or also bystander footage?

**Frame:** **subject rights** (7) on top of **inventory** (1) and **retention/deletion** (6),
with a **lawful-basis** carve-out (2) for legally retained data.

#### Requirements
**Functional**
- User-facing API to submit a DSAR (delete / export / restrict).
- Fan out a delete to **every service** that holds the user's data, including stores you'd
  forget (logs, search indexes, analytics warehouse, ML training sets, backups).
- Honor **legal holds** as per-data-type vetoes with recorded reason.
- Produce a **proof of deletion** that survives the user's account being gone.

**Non-functional**
- SLA: regulatory ceiling 30 days; internal SLO < 14 days for full ACK.
- **Idempotent** per-service delete handlers — retries must be safe.
- **Durable** orchestration — survives orchestrator crashes / region failures.
- **Synchronous, durable audit log** for every step; the audit log is itself classified data
  but **exempt from deletion** by lawful-basis carve-out (regulator may demand proof).

#### Capacity (back-of-envelope)
- DSAR rate: baseline ~100/day; bursts to 10K/day under regulator action or media event.
- Services in fan-out: ~50 (microservices + warehouses + caches + indexes + ML).
- Per DSAR: ~50 service calls × idempotent retries → orchestrator handles ~500 ops per request.
- Daily ops at burst: 10K × 500 = **5M workflow steps/day** — well within Temporal/Step-Functions
  scale. Actual data deletion volume is small per service (one user); the heavy bit is **scan
  cost** in petabyte stores (footage, logs).

#### API surface
```
# User-facing
POST /v1/privacy/dsar { userId, type ENUM(delete|export|restrict), region }
  → { requestId, expectedCompletionTs }   # synchronous ack only; work is async

GET  /v1/privacy/dsar/{requestId}/status
  → { state ENUM(received|in_progress|completed|partial), perSystem[], legalHolds[] }

# Internal — every system implements this contract (registered in the catalog)
POST /internal/privacy/delete { userId, requestId }
  Idempotency-Key: requestId          # must be safe to call N times
  → { state ENUM(deleted|held|not_present), heldReason?, evidenceRef }

# Audit log (read-only by user, write-only by orchestrator)
GET  /v1/privacy/dsar/{requestId}/proof
  → signed audit chain: who deleted what, when, with what evidence ref
```

#### Data model
- `dsar_requests` (Postgres): `requestId PK, userId, type, region, state, openedAt, closedAt`.
- `data_inventory` (the **catalog** — the kingmaker): one row per (system, data class)
  ```
  systemId, dataClass, ownerTeam, deleteEndpoint, supportsCryptoShred,
  legalHoldRules[], retentionDays, regionsHeld[]
  ```
- `dsar_steps` (one row per request × system): `requestId, systemId, attempts, lastError,
  state, evidenceRef, completedAt`. This is what the orchestrator drives.
- `audit_log` (append-only, hash-chained, KMS-encrypted, **separate** account/realm so the
  user's deletion can't reach in): `seq, prevHash, ts, requestId, systemId, action, actor, evidenceRef`.

**Why these choices.** Postgres for orchestrator state (transactions matter). A **separate
audit-log store** (could be a hash-chained table in a vault account, or QLDB / a Merkle log)
because the audit must outlive the deletion and resist tampering — putting it in the same DB
the user can delete from would be self-defeating.

#### Architecture — fan-out orchestration (NOT one query)
```
Rider ──HTTPS──▶ Privacy API ──▶ Orchestrator (Temporal / SFN)
                                       │  durable workflow, idempotent activities
                                       │  signal-driven; resumes on crash
                  ┌────────────────────┼─────────────────────┬──────────────┐
                  ▼                    ▼                     ▼              ▼
        Accounts svc           Trips/GPS svc          Support svc      ML/Analytics
        DELETE handler          DELETE handler         DELETE handler   handler
        (Postgres rows)         (S3 + Postgres)        (ZenDesk hooks)  (Iceberg+ML set)
                  │                    │                     │              │
                  ▼                    ▼                     ▼              ▼
               ACK + evidenceRef ──▶ Orchestrator records each step in `dsar_steps`
                                       │
                                       ▼
                            Audit Log (append-only, hash-chained, KMS, separate realm)
                                       │
                                       ▼
                            close request, signed proof to user

  Backups   ◀── crypto-shred per-user data key (one KMS API call) ──── orchestrator
  Logs/ES   ◀── tombstone + reindex job
  Caches    ◀── cache.purge(userId) on every node
```
- Central **Deletion Orchestrator** owns the request lifecycle.
- Every service registers in a **catalog** and exposes `deleteUserData(userId)`.
- **Durable workflow** (saga/Temporal style); **idempotent** steps so retries are safe;
  only close when everyone ACKs.

#### Sequence — one DSAR
1. User submits → API persists `dsar_requests row` and starts the workflow; user gets `requestId`.
2. Orchestrator reads `data_inventory`, schedules one activity per (system, dataClass).
3. Each activity calls `POST /internal/privacy/delete` with `Idempotency-Key=requestId`. The
   service deletes (or marks held) and returns `evidenceRef` (e.g. an S3 manifest hash).
4. Per-system policies kick in:
   - **Backups** — orchestrator calls KMS `ScheduleKeyDeletion` on the user's data key (no
     scan needed; ciphertext becomes garbage on key destroy).
   - **ML/Analytics** — service tags the user for exclusion in the next retrain; current models
     untouched (be honest about unlearning).
   - **Search index** — emit a tombstone, schedule a segment-merge to physically purge.
   - **Logs** — issue `userId` to the redaction job; for already-archived logs, crypto-shred.
5. Each ACK goes into `dsar_steps` and `audit_log` (hash-chained).
6. When all ACKs are in (or holds are recorded with reason), orchestrator transitions to
   `completed` and signs a proof bundle visible to the user.

#### Scaling, SLOs, failure modes
- **Orchestrator scale** — Temporal handles millions of workflows; sharded by `requestId`.
- **Slow service** — per-activity timeout + exponential backoff; alert at 24h, escalate at 7d.
- **Permanent failure of a system** — workflow parks the request as `partial`; on-call notified;
  retry on system recovery (workflow is durable).
- **Region failure** — workflow persistence is multi-region; each per-region handler retries
  in-region only (residency).
- **Restore from backup** that pre-dates a deletion — restore process must replay the
  **deletion ledger** (dsar_steps with `state=deleted`) before the restored data is exposed.
- **Burst (10K/day under regulator)** — orchestrator and DBs scale fine; the bottleneck is
  **scan in petabyte stores** (footage). Mitigate with reverse indexes (`userId → segmentIds`)
  built at ingest time so deletion is a lookup, not a scan.

**Hard parts:**
- **Backups** — can't surgically edit/delete backup media → **crypto-shredding** (encrypt each
  user with a per-user key; destroy the key → ciphertext becomes garbage). Fallback: track
  deletion and re-apply on restore.
- **ML models** — can't delete a gradient → anonymize/aggregate **before** training; exclude
  from next retrain; "machine unlearning" is an open problem (be honest).
- **Legal holds override RTBF** — per-data-type veto with recorded reason → **selective deletion
  with documented exceptions**, not all-or-nothing.
- **Proof of deletion** — immutable audit log of what was deleted / held / when ACKed; it must
  survive the deletion.
- **Forgotten copies** — caches, search indexes, analytics warehouse, **log files** (most-missed).

**Principles:** inventory is king, privacy by design (deletion is a first-class API), defense in depth.

**Signal sentence:**
> "Deletion in a distributed system isn't a query — it's an orchestrated, idempotent fan-out
> with documented legal exceptions; for backups I'd crypto-shred rather than edit backup media."

### 4C — Consent management

**Clarify:** Granularity (per-purpose: marketing vs. ML vs. analytics — GDPR requires consent be
*specific*)? Must we **prove** consent historically? Withdrawal anytime (must be as easy as
granting)? Central enforcement or per-service?

**Frame:** **lawful basis & consent** (2) as a first-class system, strong auditability, feeding
**purpose limitation** (3).

**Key insight — consent is an event log, not a boolean column.**
A mutable `marketing_consent = true` can't answer "what did I agree to on March 3rd, and did you
have consent when you emailed me in April?" — you overwrote history.
Model it as **append-only, versioned events** (event sourcing):
```
ConsentEvent { userId, purpose, granted/withdrawn, timestamp, policyVersion, source }
```
- **Current state** = derived view (latest event per purpose), cached for fast reads.
- **History/proof** = replay the log (same append-only-truth + derived-view idea as a bank ledger).

#### Requirements
**Functional**
- Grant / withdraw consent per (userId, purpose) at any time.
- Decide("may I use this user for purpose P right now?") at hot-path read latency.
- Replay the **full consent history** for a user — for the user's own DSAR, regulator audits,
  and disputes ("did you have consent when you emailed me on March 3?").
- Tie each event to the **policy version** the user actually saw.

**Non-functional**
- Decide P99 < **10 ms** (it's on the email/ad/ML hot path).
- Withdrawal propagation < **a few seconds** for cached PDP nodes; **point-of-use** check for
  truly sensitive purposes (no cache shortcut).
- Read-your-writes for the granting user (UI must reflect the change immediately).
- Residency: an EU user's consent log lives in EU and is never replicated cross-region.
- Fail-closed: if the PDP is unreachable, deny by default (purpose-specific override allowed
  only with explicit fallback policy).

#### Capacity (back-of-envelope)
- 10M users × ~10 events/lifetime ≈ **100M events**, ~10–20 GB compressed. Trivial.
- Reads vs writes: ~**10K decide/sec peak** (every email send, ad serve, ML pipeline run);
  ~**100 grants/withdrawals per sec** (UI-driven). Read-heavy by 100×.
- Per-user cache hit rate ~99% → underlying derived view sees ~100 QPS, easy.

#### API surface
```
# Mutations (UI-driven; userId derived from session)
POST /v1/consent/grant     { purpose, policyVersion, source }
POST /v1/consent/withdraw  { purpose, source }

# Hot path — the PDP query (read-mostly, cached, used by EVERY service before acting)
GET  /v1/consent/decide?userId=&purpose=    → { allowed, policyVersion, asOf }
                                              # 10ms P99; cached aggressively

# Current view (UI / settings page)
GET  /v1/consent/current/{userId}           → { purpose: { allowed, since, policyVersion } }

# History (proof / DSAR / audit) — uncached, paginated
GET  /v1/consent/history/{userId}           → ConsentEvent[]

# Internal — services check for sensitive actions
GET  /v1/consent/decide-strict?userId=&purpose=
                                            # bypass cache; reads from derived view directly
```

#### Data model
- **Event log** — append-only, partition by `userId`:
  ```
  Kafka topic `consent-events`,  retention = forever (or compact + archived to S3 per-region).
  ConsentEvent { userId, purpose, action ENUM(grant|withdraw), ts, policyVersion, source, ip }
  ```
- **Derived current view** — fast read (Postgres or DynamoDB), one row per (userId, purpose):
  ```
  consent_current(userId, purpose, allowed BOOL, since, policyVersion, lastEventOffset)
  ```
  Updated by a CDC consumer of the Kafka log. Read-after-write is achieved by also writing the
  derived view in the same transaction as the Kafka append (transactional outbox pattern), so
  the UI's next read sees its own write.
- **PDP cache** — Redis (per-region) or in-process near services that call decide(); TTL ~30s
  for normal purposes, **0 for sensitive** (always re-fetch). Invalidation push on every event
  via Kafka consumer in the PDP fleet.

**Why these choices.** Kafka for the log: native append-only + cheap replay + the same topic
is the invalidation bus for caches. Postgres or DynamoDB for the derived view: pick one based
on residency story (DynamoDB global tables vs. per-region Postgres). Redis cache near the PDP
because the hot path is read-mostly with very high QPS — but the cache is the source of every
withdrawal-not-propagated bug, so we keep TTLs short and always invalidate via the event bus.

#### Architecture
```
User UI ──grant/withdraw──▶ Consent API ──▶ append to Kafka `consent-events`
                                           │   (transactional outbox →
                                           │    consent_current updated atomically)
                                           ▼
                                  consent_current  (derived view, region-pinned)
                                           ▲
                                           │
                       ┌───────────────────┴────────────────────┐
                       ▼                                        ▼
                CDC → cache invalidation              Kafka downstream consumers
                via Kafka consumer in PDP fleet       (Email svc, Ad svc, ML pipeline)
                                                       drop user from next batch
                       ▲
                       │
   Service before acting ──decide()──▶ PDP gRPC  ──▶ Redis cache  ──miss──▶ consent_current
       (PEP, point-of-use check)
```
- **PDP (Policy Decision Point)** = Consent Service answers "is this allowed?"
- **PEP (Policy Enforcement Point)** = consuming service asks before acting and obeys.
- One source of truth instead of consent logic copy-pasted (and drifting) across services.

#### Sequence — withdrawal that must propagate
1. User clicks "Withdraw marketing" in the settings UI.
2. Consent API writes `ConsentEvent(action=withdraw)` in a transactional outbox: the row goes
   to `consent_current` AND the event is dispatched to Kafka in the same transaction.
3. UI re-reads `current` and shows the new state (read-after-write satisfied).
4. PDP fleet's Kafka consumers receive the event within ~hundreds of ms and **invalidate** the
   relevant Redis keys + local in-process cache.
5. Email service is mid-send when the event lands: it called `decide()` 5s ago and got `true`.
   For **sensitive purposes** the policy is to call `decide-strict()` immediately before each
   send — that bypasses the cache and reads `consent_current` directly. The send is dropped.
6. ML pipeline that reads `consent-events` stream excludes the user from the **next retrain**
   batch. (Doesn't unwind already-trained models — see hard parts.)

#### Scaling, SLOs, failure modes
- **PDP** is read-heavy + cacheable → horizontal scale of stateless gRPC pods + Redis read
  replicas. Easy.
- **Cache invalidation lag** is the main SLI: time from "withdraw written" to "all PDP caches
  invalidated". SLO P95 < 2s; alarm if > 30s.
- **PDP unreachable** → fail-closed (deny). Strict purposes never had a cache shortcut anyway.
- **Region partition** → per-region PDP + per-region derived view + per-region log; no
  cross-region calls on the hot path. Cross-region replication only for **non-residency-bound
  global purposes** if any (rare).
- **Event ordering** → partition key = `userId` so all events for one user land on one Kafka
  partition; consumer processes them in order; idempotent applies (lastEventOffset gate).
- **Schema evolution** of policyVersion → events carry the version, decisions are
  policy-version-aware; changing the policy can require **re-consent** before grants are
  considered valid for the new version.

**Hard parts:**
- **Withdrawal must propagate fast**, even to jobs already in motion → **check at point of use**
  for sensitive actions; pushing events alone isn't enough if a job read a stale cached value.
- **Withdrawal ≠ deletion** — withdrawing ML consent doesn't auto-remove you from trained models
  (same unlearning problem); stop going forward + exclude from next retrain.
- **Tie consent to the policy version** seen → changing the policy may require **re-consent**.
- **Dark-pattern/granularity rules** — consent must be freely given, specific, informed,
  unambiguous; no pre-ticked boxes; withdrawal as easy as granting.
- **Proof comes free** from the immutable, versioned log.

**Principles:** consent defines purpose limitation; one queryable source of truth.

**Signal sentence:**
> "Model consent as an immutable, versioned event log — current state for enforcement, full
> history for proof — enforced via a central decision point services query at point of use."

### The design spine across all three
**Append-only truth + derived views**, **central orchestration/decision point**, and an honest
**"this part is genuinely hard"** (ML unlearning / 100% recall). That trio is your reusable spine.

### 4D — System-design fundamentals through a privacy lens

A privacy SD round is **still a system design round**. Replicas, DBs, caching, latency,
consistency — all of it is fair game. The difference is *weighting* and *framing*: every
architectural choice gets justified by a privacy concern, not just by performance.

**Rough weighting (vs. a generic SD round):**

| Topic                                       | Generic SD | Privacy SD          |
|---------------------------------------------|-----------:|--------------------:|
| Privacy framing (basis, minimization)       |        ~0% |              25–30% |
| Storage / DB / encryption / **residency**   |       ~25% |                ~20% |
| Replication / consistency / regions         |       ~15% | ~10% (residency-led)|
| Latency / throughput / caching              |       ~20% | ~10% (consent-only) |
| Async pipelines / orchestration / idempotency |    ~10% |                ~15% |
| Subject-rights pipeline / audit / proof     |        ~5% |                ~15% |
| Honest hard problems (ML, crypto-shred)     |        ~0% |                ~10% |

**The mapping you must be ready with — standard SD topic ➜ privacy-lens reframe:**

| Standard topic                  | Reframe (what you actually say)                                        |
|---------------------------------|------------------------------------------------------------------------|
| Sharding by `user_id`           | Clean per-user delete locality; cross-shard analytics is the leak.    |
| Read replicas / multi-region    | Topology follows **data residency** before perf; cross-region is itself a transfer needing a basis (SCCs / adequacy). |
| Caching consent decisions       | The hard part is **invalidation on withdrawal**; sensitive actions check at point of use, not from cache. |
| Eventual consistency            | Fine for analytics, **not for consent** — withdrawal must propagate before next use. |
| Kafka / WAL / event log         | Logs are personal data too — set retention, encrypt, include in deletion fan-out. The "forgotten copy in the log" is the most-missed item. |
| Object storage for footage      | Bucket-level KMS encryption; lifecycle for retention; **per-user keys** to make crypto-shred work; versioning means deletion must nuke versions. |
| Search / ES indexes             | Indexes are forgotten copies — in the deletion catalog. Geo-temporal indexes over footage are themselves a surveillance risk. |
| CDN / edge cache                | Edge inherits residency from origin; purge API is part of the deletion workflow. |
| Backups (Glacier-class)         | **Crypto-shred** with per-user keys, OR a deletion ledger re-applied on restore. |
| Audit log                       | Append-only / hash-chained / tamper-evident; **survives** the user's own deletion — that's how you prove deletion happened. |
| Rate-limiting / abuse signals   | Differentially-private counters or hashed identifiers; don't reach for raw IP/userId by default. |
| Service mesh / IAM              | Bucket #5 — least privilege, JIT elevation, every raw-data read logged with a reason code. |
| Observability / metrics         | Watch metric labels for PII; high-cardinality `userId` is a privacy bug, not just a perf bug. |
| Dual-write / outbox / saga      | The right primitive for deletion fan-out: durable, idempotent, per-step ACK, audit only after all ACKs. |
| CAP choice                      | Trade availability for consistency on the consent path; trade latency for durability on the audit log; deletion is **eventually consistent + provable**. |

**How to weave it in (not as a separate "scale" section at the end — inline):**

> "This is mostly a **lawful-basis + deletion** question. Storage = object store with
> **per-user KMS keys** so I can crypto-shred later. Consent service = **append-only event
> log** (Kafka + CDC into a derived view) — durable, cheap to replay for proof. One
> consistency choice I'd make explicit: consent reads must **not** come from a stale cache
> for sensitive actions; everywhere else eventual is fine. Multi-region replication topology
> follows **data residency** before latency."

That sentence touches replication, consistency, caching, DB choice, and Kafka — but every
one is justified by a privacy concern. That's the senior signal.

**Pre-canned answers for the standard probes interviewers will throw:**

| Probe                                  | Default answer                                            |
|----------------------------------------|-----------------------------------------------------------|
| "How do you scale this to 10x?"        | Privacy doesn't change at scale; the **catalog and audit log** scale with services. Worry about **cross-shard analytics** — that's where personal data leaks back. |
| "Pick a database."                     | Append-only / event-log workloads → Kafka + warehouse for analytics; consent decisions → Postgres derived view; raw footage → S3 + per-user KMS. Pick by **deletion model**, not just access pattern. |
| "How fast must this be?"               | **Withdrawal propagation < seconds** for sensitive actions; **deletion** can be ~30 days async; **audit writes** are synchronous and durable. |
| "What if a region goes down?"          | Audit log replicated; deletion workflow is durable + idempotent so it resumes. Consent reads fail closed (deny by default), not open. |
| "Where would this break under load?"   | The deletion fan-out: 100k RTBF requests × N services × per-user keys. Saga/queue depth and KMS quotas are the real bottlenecks. |

**Signal sentence:**
> "Standard SD topics absolutely apply, but each one needs a privacy justification —
> sharding chosen for delete locality, replication topology bounded by residency, caching
> bounded by consent invalidation, and consistency picked synchronously where withdrawal
> matters and eventually-consistent everywhere else."

---

## Module 5 — Behavioral / "tell me about a time"

For a privacy engineer this round is secretly the most important — the job is largely
**influencing other teams to do the inconvenient right thing**.

**Use STAR every time:** Situation (1–2 sentences) → Task (what *you* owned) →
**Action (60% of the answer, say "I")** → Result (quantified + lesson learned).

**The ~5 competencies every question probes:**

| Competency               | Hidden question                                  |
|--------------------------|--------------------------------------------------|
| Privacy backbone         | Will you push back when business cuts a corner?  |
| Influence w/o authority  | Can you get other teams to do privacy work?      |
| Pragmatism / tradeoffs   | Zealot, or can you balance privacy vs. shipping? |
| Incident handling        | Calm + own it when data leaks?                   |
| Ambiguity                | Can you act when rules aren't written yet?       |

**Model — "pushed back on the business for privacy reasons":**
- S: Product wanted to log full GPS traces indefinitely for a recommendation feature.
- T: I owned privacy sign-off on the data model.
- A: Instead of blocking, I found what they *actually* needed (coarse, aggregated location, not
  precise indefinite traces), proposed truncated precision + 90-day retention, and framed it in
  *their* language — less data = lower breach liability + cleaner regulatory story. Brought an
  alternative, not just "no."
- R: Shipped the privacy-preserving version; PM reused the pattern twice more. **Lesson:**
  pushback lands when you bring a workable alternative and frame privacy as risk reduction.

**Model — "a privacy/security incident you handled" (note the order):**
- A: **Contain first** (lock down access before root-causing — stop the bleed) → **assess scope**
  via audit logs → **escalate** to legal/privacy for notification duties → **remediate** root cause
  (overly broad permissions) + add an alert → **blameless post-mortem**.
- R: No external exposure, contained < 1 hour; rolled out least-privilege defaults broadly after.

**Avoid the zealot trap:** have **one story where you *allowed* a calculated risk** (shipped with
a known minor gap, low exposure, fix scheduled, decision documented) — shows judgment.

**Prep mechanics:** write **5–6 real STAR stories** (~150 words each), **tag** each with the 2–3
competencies it covers, map question→story live, always end with the lesson. No literal privacy
story? Reframe a data-handling / security / access-control / hard-tradeoff story through a privacy lens.

---

## Module 6 — Cheat sheet + day-before checklist

### Reusable patterns (say these phrases)
- **Append-only event log + derived current view** → consent, audit, balances.
- **Fan-out orchestration, idempotent, durable workflow** → deletion, DSAR.
- **Two tiers: locked raw (short retention) + broad anonymized** → sensor data.
- **PDP / PEP split** → consent/policy enforcement in one place.
- **Crypto-shredding** (destroy per-user key) → deletion from immutable backups.
- **k-anonymity** → "anonymized" only if you blend into a crowd of k.

### Standard SD topics, privacy-lens one-liners (Module 4D)
- **Shard by user_id** → delete locality; cross-shard analytics is the leak.
- **Replication topology** → bounded by **residency**, not latency.
- **Cache consent** → the hard part is **invalidation on withdrawal**; check at point of use for sensitive actions.
- **Consistency** → consent = synchronous; deletion = eventually consistent + provable; audit = synchronous + durable.
- **Logs / Kafka / indexes / CDN edge** → **forgotten copies**; in the deletion catalog.
- **Backups** → crypto-shred with per-user keys.

### The 4 "honest hard problems" (naming them = seniority)
- **ML unlearning** — can't delete data baked into weights → anonymize *before* training.
- **Backups** — can't edit them → crypto-shred.
- **Detection recall** — blur model misses faces → over-redact, track recall as safety metric.
- **Re-identification** — blurred ≠ anonymous (gait, location+time, LiDAR) → coarsen, aggregate.

### Waymo-specific reflexes
- The car is a **360° surveillance platform** → bystanders are data subjects who never consented
  (→ legitimate interest + minimization).
- **You can't blur the live perception feed** — the car must see to drive. Anonymization = stored data.
- **LiDAR is biometric** too, not just cameras.
- **Safety/legal retention** (crash investigations) constantly tensions with privacy → selective,
  documented exceptions.

### Day-before / day-of checklist
- [ ] Reread this Module 6 + the three signal sentences.
- [ ] Rehearse the 3 flagship designs out loud, 60-second version each.
- [ ] Have 5–6 STAR stories written and tagged (incl. one "allowed a calculated risk").
- [ ] First move in *every* design question: **clarify, then name the 2–3 buckets.**
- [ ] When asked SD-fundamentals (DB / cache / replicas / latency): **answer through a
      privacy lens** — every choice gets a privacy justification (Module 4D).
- [ ] First move in *every* behavioral: **pick a story, STAR, end with the lesson.**
- [ ] Mantra: **minimize, justify (lawful basis), protect (encrypt + least privilege), be honest
      about the hard parts.**

---

## Appendix — Quiz questions & model answers

### Q1. A pedestrian is filmed by a Waymo car — allowed? What must Waymo do?
**A.** Lawful basis = **legitimate interest** (safe AV operation; pedestrian can't consent).
Must run a **balancing test / DPIA**, **minimize** (blur faces & plates, no private interiors),
and avoid identifying beyond what's needed.

### Q2. Why is RTBF ("delete me") hard for Waymo specifically?
**A.** Data is **scattered across many independent systems**; needs **fan-out orchestration**,
**proof** of deletion, handling of **immutable backups** (crypto-shred) and **trained ML models**
(unlearning is hard), plus **legal exceptions** for data that must be retained.

### Q3 (bucket reflex). "A regulator asks for all footage from one intersection over the last year."
Which buckets, and the one-sentence framing?
**A.** Buckets: **2 (lawful basis to disclose)**, **1 (inventory — can we even retrieve by
location+time?)**, **3 (minimization / third-party rights of bystanders)**, secondary **5 (access
logging)**. Key insight: **two parties with opposite interests** are in that footage.
- Verify the **legal instrument** (subpoena/court order) — a casual ask is *not* a lawful basis;
  disclosing without one would itself breach bystanders' privacy.
- Check **retrievability** (geo-temporal index? — but that index is itself a surveillance risk).
- **Scope + redact** uninvolved third parties unless the order requires them.
- **Retention can be a feature:** if raw frames expire after ~30 days, "last year" may legitimately
  no longer exist.
**One-liner:** "Verify the legal basis, retrieve only the scoped footage, redact uninvolved third
parties, log the access — and flag that our retention policy may mean most of that footage no longer exists."

### Q4 (mock design). "A rider taps 'Delete my account and all my data' — design the system."
**A.** See **Module 4B**. Rubric to self-grade (aim ≥6/8 unprompted):
clarified before designing • spotted "not a query, distributed fan-out" • concrete architecture
(orchestrator + catalog + durable workflow + idempotency) • **crypto-shredding** for backups •
honesty about ML unlearning • legal holds override RTBF • proof/audit log • remembered logs/caches.

### Q5 (behavioral). "Tell me about a time you pushed back on the business for privacy reasons."
**A.** See **Module 5** model. Key signals: **redirect to data minimization** (not just "no"),
**speak the business's risk/liability language**, bring a **concrete alternative**, end with the
**lesson learned**.
