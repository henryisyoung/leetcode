# Waymo Privacy Engineer — System-Design Deep Dives (EU / GDPR)

> Companion to `UserDataProtectionPolicy.md`. That doc is the **framework**
> (8 buckets, reg cheat-sheet, scenario talking points). This one is the
> **whiteboard-ready deep dives** for the specific role: GDPR architecture,
> RTBF automation, consent, sensor anonymization, cross-border. Drill these
> until you can sketch each in ~15 minutes with components, data model, APIs,
> failure modes, and the GDPR article each move maps to.

The role is *Privacy Engineer* — they want you to **build the engines**, not
manage compliance. So in the room: design real components (services, schemas,
APIs, queues), give scale numbers, and always name the privacy/eng trade-off.

---

## 0. How to open ANY privacy system-design prompt

A 60-second script before you draw anything:

1. **Restate + scope.** "Protect user data" → which data, which subjects
   (riders vs bystanders vs employees), which jurisdiction (EU), which use
   (operate the service vs train ML)?
2. **Name the data subjects** — this is the privacy-specific move most
   candidates miss:
   - **Riders** → contract + consent basis, full data-subject rights.
   - **Bystanders** (pedestrians, other cars) → *no consent possible* →
     **legitimate interest** basis (GDPR Art. 6(1)(f)) + documented balancing
     test in the DPIA. They still have rights (objection, erasure).
   - **Employees / operators** → internal, RBAC + monitoring.
3. **Classify the data** (tier it at ingest): Public / Internal / Confidential
   / Restricted-PII / Restricted-SPI (precise geo, biometrics, audio).
4. **Pick the 2–3 buckets** it's really about and go deep.
5. **State non-functionals:** scale (fleet of N cars × M sensors × Hz),
   latency budget at the edge, residency, auditability, retention.

---

## 1. DEEP DIVE — Sensor Data Anonymization Pipeline (the flagship)

> JD: "detect and blur faces, license plates, and other PII from camera/LiDAR
> at the edge (on-vehicle) and in the cloud before it enters training sets."

### Requirements
- **Functional:** before any frame is used for ML or human review, faces +
  plates (and other direct PII) must be irreversibly removed/blurred.
- **Two stages:** edge (best-effort, low-latency, save bandwidth & limit raw
  PII leaving the car) + cloud (authoritative, higher-recall models).
- **Non-functional:** ~N cars × 5–8 cameras × ~10–30 fps + LiDAR; edge has a
  tight power/compute budget; must hit very high **recall** (a missed face is
  a privacy incident); reversibility must be *impossible* once published.

### Architecture
```
ON-VEHICLE (edge)
  raw frame ─► detector (faces+plates, quantized CNN on the AV SoC)
            ─► blur/pixelate in-place (Gaussian/mosaic, irreversible)
            ─► tiered local buffer: raw(short TTL, encrypted) + redacted
            ─► upload redacted over mTLS; raw stays unless explicitly pulled
                                  │
                                  ▼
CLOUD INGEST (regional, EU)
  redacted stream ─► QUARANTINE bucket (Restricted, no ML access yet)
                  ─► authoritative detector (bigger model, higher recall)
                  ─► second-pass blur on anything edge missed
                  ─► PII scan (audio strip, OCR for text PII on signage)
                  ─► lineage tag + "anonymization attestation" record
                  ─► PROMOTE to sanitized ML lake (cross-region OK)
```

### Key design decisions / talking points
- **Irreversibility:** blur must be a destructive transform on the *stored*
  pixels, not an overlay. Never keep a raw→redacted mapping for published
  frames. (If you keep raw for a short window for safety/debug, it lives in a
  separate Restricted store with its own short TTL + access audit, and never
  feeds ML.)
- **Recall over precision:** tune the detector to over-blur. A false positive
  (blurred a tree) costs model quality; a false negative (un-blurred face) is
  a reportable privacy breach. Quantify: "I'd target >99.x% recall and accept
  precision loss, measured on a held-out red-team set."
- **Edge vs cloud split (the trade-off to name):** edge blurring minimizes raw
  PII leaving the vehicle (data minimization, Art. 5(1)(c)) and saves
  bandwidth, but costs on-vehicle compute/power and uses a smaller, weaker
  model. Cloud pass is the authoritative net. **Both**, defense in depth.
- **Model-improvement loop problem:** when you ship a better detector, frames
  blurred by the old one may have missed PII. Need: re-scan of retained raw
  (if any) and a **versioned attestation** (`anonymizer v7, recall X`) per
  frame so you can re-process or quarantine. This is a great "show depth"
  point.
- **LiDAR is PII too:** point clouds can re-identify (gait, face geometry,
  plate retroreflectivity). Don't assume "no pixels = no PII." Mention voxel
  down-sampling / removing high-res returns on people.
- **Audio:** in-cabin mics → strip/transcribe-then-discard, or never record;
  conversations are special-category-adjacent.
- **Metrics & monitoring:** sampled human audit of redacted output, red-team
  injection of known faces, per-model recall dashboards, alert on detector
  confidence drift.
- **GDPR mapping:** Art. 5 (minimization, purpose limitation), Art. 25
  (privacy by design/default), Art. 35 (DPIA — high-risk biometric-adjacent
  processing), Art. 6(1)(f) (legitimate interest for bystanders + balancing).

### Failure modes to volunteer
- Detector outage → fail **closed** (don't promote to ML lake; hold in
  quarantine). Never fail open.
- Backlog in cloud pass → backpressure, never bypass sanitization.
- Edge model poisoned/regressed → canary + recall floor gate before fleet
  rollout.

---

## 2. DEEP DIVE — Right to be Forgotten automation (GDPR Art. 17)

> JD: "Right to be Forgotten automation."

### The honest framing (say this first)
"Deletion is **not** a SQL `DELETE`. The data is *everywhere* — operational
DB, warehouse, S3 archives, ML feature stores & training corpora, sensor logs,
payments, support tickets, vendor pipelines, backups. So I'd build a
**deletion orchestration service driven by data lineage**, with tombstones,
per-system adapters, proofs, and a documented exception path."

### Architecture
```
DSAR/erasure request
   ─► Identity resolution (map request → all internal IDs/pseudonyms)
   ─► Erasure Orchestrator (workflow engine, durable, idempotent)
        │  consults LINEAGE catalog → blast radius (which systems hold it)
        ├─► adapter: OLTP        → hard delete / crypto-shred
        ├─► adapter: warehouse   → delete + re-materialize views
        ├─► adapter: object store→ delete objects + versions
        ├─► adapter: ML feature store / training sets → remove + flag retrain
        ├─► adapter: payments    → delete or retain-under-legal-obligation
        ├─► adapter: vendors      → API call per DPA, track ack
        └─► adapter: backups      → tombstone + delete-on-restore policy
   ─► each adapter writes an ATTESTATION (system, time, method, proof hash)
   ─► Tombstone registry (so re-ingestion/restores re-apply deletion)
   ─► Close request, emit completion certificate; SLA 30 days (GDPR)
```

### Key talking points
- **Crypto-shredding** for the hard stores (backups, immutable archives):
  encrypt each subject's data under a per-subject key; "delete" = destroy the
  key → ciphertext is unrecoverable. Solves the "can't rewrite an immutable
  backup" problem elegantly. (Great senior signal.)
- **Idempotent + durable orchestration** (e.g., a workflow/state-machine):
  partial failures must be resumable; deletion must survive a backup restore
  → the **tombstone registry** re-applies erasure to anything that comes back.
- **The ML model problem (name it):** you can delete training *rows*, but the
  trained **model weights** may have memorized data. Options to discuss:
  (a) exclude on next scheduled retrain (pragmatic, documented), (b) machine
  *unlearning* / influence removal (research-y), (c) DP training upfront so no
  single record is memorized (best long-term). Be honest about the trade-off.
- **Exceptions (this is where seniority shows):** Art. 17(3) carve-outs —
  legal obligation (NHTSA/EU crash reporting), legal claims, freedom of
  expression. Don't delete those; **minimize + isolate + expire** when the
  obligation ends, and record *why* in the attestation.
- **Bystander erasure** is harder than rider erasure (no account to key on) —
  realistically satisfied by the anonymization pipeline (their PII was already
  irreversibly removed) + objection handling.
- **Proof & audit:** completion certificate with per-system attestations; this
  is what you show the DPA.

### Scale / SLA
- Requests are low-QPS but high-fan-out and long-running (hours–days). Design
  for **throughput of fan-out**, not request QPS. SLA 30 days (GDPR), alert at
  20.

---

## 3. DEEP DIVE — Consent Management Framework (EU riders)

> JD: "consent management frameworks for European riders."

### Core idea
A consent decision must be (1) **granular per purpose**, (2) **versioned**,
(3) **provable at time T**, (4) **withdrawable as easily as given**, and (5)
**enforced at access time**, not just at collection.

### Architecture
```
Rider app ─► Consent Service (event-sourced ledger; append-only)
                 events: GRANT(purpose, version, ts), WITHDRAW(purpose, ts)
                 ─► current-state projection (fast read: "can we do X now?")
                 ─► immutable history (prove "what was true at time T")

Any data access / pipeline ─► Policy Decision Point (PDP)
                 input: (subject, purpose, data tier)
                 ─► checks consent ledger + lawful basis + retention + region
                 ─► ALLOW / DENY (logged)
Policy Enforcement Point (PEP) at each store/pipeline calls PDP.
```

### Talking points
- **Purpose taxonomy:** separate consents for *operate the ride* (contract
  basis, not really optional), *improve ML / training*, *marketing*,
  *analytics*. Different purpose ⇒ different basis ⇒ different toggle. Purpose
  limitation (Art. 5(1)(b)).
- **Event-sourced, not last-write-wins:** you must reconstruct consent state
  at the moment data was used (auditor asks "were you allowed to use the June
  ride for training?"). Event log + temporal queries.
- **Consent ≠ the only lawful basis.** Riders: contract for service, consent
  for ML/marketing. Bystanders: legitimate interest (can't get consent).
  Don't over-rely on consent — name the right basis per purpose.
- **Withdrawal propagation:** withdrawing ML consent must (a) stop future use
  immediately (PDP flips) and (b) trigger removal from training sets on next
  retrain (links into the RTBF pipeline). Easy-as-giving (Art. 7(3)).
- **Dark-pattern-free UX:** no pre-ticked boxes (Art. 4(11) — freely given,
  specific, informed). Reject/Accept symmetry. (EDPB guidance.)
- **GDPR mapping:** Art. 4(11), 6, 7, 13/14 (transparency at collection).

---

## 4. DEEP DIVE — Cross-Border Data Plane (EU launch)

> JD: "cross-border data transfer risks"; the EU expansion context.

### Architecture
```
        EU data plane (e.g., eu-central)          US data plane
   ┌──────────────────────────┐            ┌──────────────────────────┐
   │ EU ingest + storage       │   SCCs +   │ US storage                │
   │ EU KMS (keys never leave) │  TIA gate  │ US KMS                    │
   │ raw sensor stays in EU    │ ◄────────► │                           │
   └──────────────────────────┘            └──────────────────────────┘
   Residency tag on every row/object; replication policy enforces locality.
   Only SANITIZED, aggregated, or pseudonymized data may cross (if at all).
```

### Talking points
- **Data residency by tagging:** every datum tagged with origin region at
  ingest; replication/Access layer refuses to move Restricted data
  cross-region. Raw EU sensor data **stays in the EU**.
- **Transfer mechanism (post-Schrems II):** EU→US needs **SCCs** + a
  **Transfer Impact Assessment**, or rely on the **EU–US Data Privacy
  Framework** (adequacy) if the receiving entity is certified. Mention both;
  note adequacy can be challenged ("Schrems III risk") so SCCs + supplementary
  measures (encryption with EU-held keys) are the durable hedge.
- **Keys as a control:** if US can't decrypt (EU-held KMS keys, EU-only access
  proxy), a "transfer" of ciphertext is much lower risk. Encryption as a
  supplementary measure.
- **Process locally, ship insights:** run training/analytics in-region; export
  only model updates / aggregates (federated-style) instead of raw data.
- **GDPR mapping:** Chapter V (Arts. 44–49), Art. 5 residency-by-design.

---

## 5. SHORTER DESIGNS (have a 5-min version each)

### Retention & data-minimization engine
- Per-category retention schedule (config-as-code, reviewed by Legal).
- TTL on every object + scheduled sweeper; **crypto-shred** for archives.
- Minimization at the **schema review** (shift left): a new field needs a
  purpose + basis + retention or it doesn't ship.
- Proof: delete-audit log, append-only.

### PIA / threat modeling for a new feature
- Use **LINDDUN** (privacy threat modeling): Linkability, Identifiability,
  Non-repudiation, Detectability, Disclosure, Unawareness, Non-compliance.
- Or map data flows → for each, ask the 8 buckets.
- Output: DPIA doc (required for high-risk processing, Art. 35), residual-risk
  sign-off, mitigations tracked.

### Internal access to live video (remote assist)
- JIT access, time-boxed, reason-coded; sessions recorded + watermarked;
  four-eyes for sensitive actions; access logs reviewed; anomaly detection on
  operator behavior.

### Trip data collection pipeline: Flume -> MapReduce analytics -> RTBF

> Prompt: "For every Waymo trip, collect rider and trip telemetry using Flume,
> run MapReduce jobs for analytics, and later handle a rider's Right to be
> Forgotten request. Design the pipeline."
>
> Full walkthrough: `TripDataCollectionFlumeMapReduceRTBF.md`.

Architecture:
```
Trip services / vehicle gateways
   └─► Flume agents on app/backend hosts
          source: app logs, trip events, billing events, vehicle telemetry
          channel: durable file channel, encrypted local buffer
          sink: regional HDFS / object-store raw zone + event manifest
                       │
                       ▼
              Data lake raw zone (short retention, Restricted)
                       │
                       ▼
              MapReduce analytics jobs
                 - aggregate trip duration, ETA quality, pickup hotspots
                 - write anonymized / aggregated analytics tables
                 - write lineage: userId -> raw files, partitions, outputs
```

Key talking points:
- **Flume's role:** host-local collection and reliable shipping from logs/events
  into the data lake. It is not the long-term queue of record; it buffers and
  forwards.
- **Minimize at ingest:** split direct identifiers from telemetry. Store
  `userId`/payment/account data in a restricted subject table; store trip
  telemetry under a pseudonymous `subjectToken`.
- **Lineage is mandatory:** every raw file, HDFS partition, MapReduce output,
  and analytics table carries `{subjectToken, tripId, purpose, retention,
  region, sourceOffset}` so RTBF can find data without a petabyte scan.
- **Analytics output:** prefer aggregated tables with k-anonymity thresholds
  (for example, no pickup hotspot cell unless `k >= 50`) so the output is less
  likely to remain personal data.
- **RTBF flow:** resolve rider -> all `subjectToken`s -> consult lineage catalog
  -> delete or crypto-shred raw trip files -> remove rows from derived tables
  or rebuild affected partitions -> emit tombstones so restored backups and
  future backfills do not reintroduce the user.
- **Legal holds:** crash/safety records may be retained under legal obligation,
  but should be isolated, minimized, and recorded as a hold in the RTBF proof.
- **MapReduce correctness:** jobs write to new partitions, record input
  snapshots/offsets, and publish only after validation; this makes deletion
  verification and audit reproducible.

Good follow-up answer if asked "why not just delete from analytics?":
"Because analytics is derived data. I need lineage from raw Flume-collected
events through every MapReduce output, plus tombstones and partition rebuilds,
otherwise the user can reappear during a backfill or backup restore."

---

## 6. EU regulation cheat-sheet (deeper than the framework doc)

### GDPR — the articles that actually come up
| Article | What it forces (one line) |
| --- | --- |
| **Art. 5** | Principles: lawfulness, purpose limitation, **minimization**, accuracy, storage limitation, integrity, **accountability**. |
| **Art. 6** | Need a **lawful basis**: consent, contract, legal obligation, vital/public interest, **legitimate interest (6(1)(f))**. |
| **Art. 7** | Consent must be freely given, specific, informed, **withdrawable as easily as given**. |
| **Art. 9** | **Special-category** data (biometrics for ID, health, etc.) — extra bar. Faces *for identification* can fall here. |
| **Art. 12–22** | Data-subject rights: access, rectify, **erase (17)**, restrict, **portability (20)**, object, no solely-automated decisions (22). |
| **Art. 25** | **Privacy by design & by default.** |
| **Art. 30** | Records of processing activities (RoPA). |
| **Art. 32** | Security of processing (encryption, resilience, testing). |
| **Art. 33/34** | Breach notice: **72h to DPA**, notify subjects if high risk. |
| **Art. 35** | **DPIA** for high-risk (large-scale monitoring of public spaces = AV!). |
| **Ch. V (44–49)** | International transfers: adequacy / SCCs / BCRs. |

### Lawful basis for bystanders — rehearse this
AVs film non-consenting people. You **cannot** get consent → rely on
**legitimate interest (6(1)(f))**: legitimate purpose (road safety) +
necessity + **balancing test** against individuals' rights, documented in the
DPIA, mitigated by immediate anonymization. If faces are used for
*identification* you'd be in Art. 9 territory — so design so you **never
identify**, only detect-and-blur.

### EU AI Act (JD mentions it)
- Risk tiers: prohibited / **high-risk** / limited / minimal.
- **Real-time remote biometric identification in public spaces** is heavily
  restricted/prohibited — reinforce that Waymo's pipeline **detects to blur,
  never to identify**. Articulating that distinction is gold.
- High-risk AI → conformity assessment, logging, human oversight, data
  governance. Perception stacks may touch this.

### EU Data Act (JD mentions it)
- Governs access to and sharing of (esp. IoT/connected-device) data, B2B/B2G
  sharing, switching cloud providers. For a connected vehicle: who can access
  vehicle-generated data and under what terms. Know it exists and that it adds
  **data-sharing obligations** distinct from GDPR's protection obligations.

---

## 7. Reusable architectural "moves" specific to this role
- **Crypto-shredding** (per-subject keys) → solves deletion in immutable
  stores/backups.
- **Anonymization attestation record** (versioned model, recall) per sensor
  artifact → enables re-processing & proof.
- **PDP/PEP split** → enforce consent + basis + residency at access time,
  centrally decided.
- **Lineage-driven deletion fan-out** → provable blast radius.
- **Residency tagging + region-pinned KMS** → cross-border control.
- **DP / federated learning** → train without centralizing raw PII; also helps
  the "model memorized a deleted user" problem.
- **Fail-closed sanitization gate** → nothing reaches the ML lake un-redacted.

---

## 8. Trade-offs you should proactively name (seniority signals)
- **Edge blur vs cloud blur** — minimization & bandwidth vs vehicle compute &
  model strength → do both, defense in depth.
- **Recall vs precision** in detection — over-blur on purpose; FN is a breach.
- **Utility vs privacy in ML** — DP/federated reduce memorization but cost
  accuracy; quantify the budget (ε).
- **Deletion completeness vs immutability** — crypto-shred reconciles them.
- **Consent friction vs lawful coverage** — use contract/legitimate-interest
  where appropriate; don't gate the core ride on optional consent.
- **Reporting obligation vs erasure** — legal obligation wins; minimize &
  expire.

---

## 9. Mock prompts tailored to THIS role (drill out loud, ~15 min each)
1. Design the on-vehicle + cloud pipeline that guarantees no un-blurred face
   or plate ever reaches a training set. How do you prove it? What happens
   when you ship a better detector?
2. A Munich rider invokes Right to be Forgotten. Walk the full fan-out,
   including their sensor frames and any model trained on them.
3. Design consent management for EU riders with separate toggles for ride
   vs ML-training vs marketing, provable at any past timestamp.
4. Waymo wants EU sensor data to improve a globally-trained model. How do you
   move value across the Atlantic without moving raw PII? (federated /
   aggregates / SCCs / EU-held keys.)
5. PIA a new "remember my favorite destinations" feature. What threats
   (LINDDUN), what mitigations, is a DPIA required?
6. A vendor that labels sensor frames suffers a breach. Your 72 hours, plus
   what should have been in the DPA.
7. How do you enforce data residency so EU raw sensor data never lands in a
   US bucket — at storage, replication, and access layers?
8. Reconcile EU AI Act limits on biometric identification with a perception
   stack that must *detect* people. (detect-to-protect, never identify.)
9. Design a trip data collection pipeline using Flume agents, MapReduce
   analytics, and a later RTBF request. How do you avoid forgotten copies in
   raw logs, derived analytics tables, and backfills?

---

## TL;DR for this role
- Lead by **naming the data subject** (rider vs **bystander → legitimate
  interest + balancing**) and **classifying the data**.
- Be able to fully draw the **4 flagship designs**: anonymization pipeline,
  RTBF fan-out, consent (event-sourced + PDP/PEP), cross-border plane.
- Keep **crypto-shredding, attestations, fail-closed gates, residency tags,
  DP/federated** at your fingertips.
- For Waymo specifically: **detect-to-blur, never identify** (AI Act),
  **irreversible** anonymization, **fail closed**, and the **NHTSA/legal-
  obligation carve-out** to erasure.
```
