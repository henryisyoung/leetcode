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

**Architecture — two tiers (shrink the blast radius):**
```
Vehicle ──raw──▶ Ingest ──▶ Raw Store (encrypted, locked, short retention, audited)
                                  │
                                  ▼
                        Anonymization Service  (detect faces+plates → blur)
                                  │
                    ┌─────────────┴─────────────┐
                    ▼                            ▼
            Anonymized Store              raw auto-expires
       (broad access, ML/analytics)       after short window
```
- **Raw tier:** full fidelity, encrypted, very few people, every access logged, short retention.
- **Anonymized tier:** blurred faces/plates; broad downstream access is safe.

**Anonymization service:** detect (reuse perception models) → redact (prefer solid mask / heavy
irreversible blur) → **verify recall** (no model is 100%; bias toward **over-redaction**, treat
recall as a tracked **safety metric**, human spot-audit).

**Hard parts:**
- **Can't delete raw immediately** — needed for crash investigations/legal defense → keep raw
  briefly under tight lock, auto-expire, logged + time-boxed unlock to access.
- **Re-identification** — blurring a face leaves gait, clothing, location+timestamp →
  **k-anonymity** mental model; coarsen/aggregate location & time.
- **LiDAR is biometric too** (3D shape/gait) — privacy isn't just the camera.
- **Throughput/cost** — scalable batch/stream pipeline; **on-vehicle blur** (less raw leaves the
  car) vs. **cloud blur** (more compute); treat the upload link as a trust boundary.

**Principles:** privacy by design (default output is anonymized), minimization, defense in depth.

**Signal sentence:**
> "Anonymize stored/exported data, not the live driving feed; two tiers; over-redact and treat
> detection recall as a safety metric."

### 4B — Right to be Forgotten / deletion

**Clarify:** True erasure vs. account closure? SLA (~30 days → **async**)? **Legal holds**
(data we must keep — crash footage under litigation, tax records)? Scope = account-linked data
or also bystander footage?

**Frame:** **subject rights** (7) on top of **inventory** (1) and **retention/deletion** (6),
with a **lawful-basis** carve-out (2) for legally retained data.

**Architecture — fan-out orchestration (NOT one query):**
```
Rider request ──▶ Deletion Orchestrator ──▶ record request, return "in progress"
                        │  (durable workflow, ~30-day SLA, idempotent steps)
        ┌───────────────┼────────────────┬───────────────┐
        ▼               ▼                 ▼               ▼
  Accounts        Trips/GPS         Support         ML/Analytics
  deleteUserData  deleteUserData    deleteUserData  delete or anonymize
        └──────── each ACKs ──────────────┴───────────────┘
                        ▼
              Verify + close + audit log
```
- Central **Deletion Orchestrator** owns the request lifecycle.
- Every service registers in a **catalog** and exposes `deleteUserData(userId)`.
- **Durable workflow** (saga/Temporal style); **idempotent** steps so retries are safe;
  only close when everyone ACKs.

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

**Architecture:**
```
User UI ──grant/withdraw──▶ Consent Service ──▶ append ConsentEvent (immutable log)
                                  │
                                  ├──▶ derive + cache current consent per user/purpose
                                  └──publish event──▶ (Kafka) downstream services react
                                                      (stop emails, drop from next ML set)
Any service before acting ──query──▶ Consent Service: "may I use X for purpose P?"
```
- **PDP (Policy Decision Point)** = Consent Service answers "is this allowed?"
- **PEP (Policy Enforcement Point)** = consuming service asks before acting and obeys.
- One source of truth instead of consent logic copy-pasted (and drifting) across services.

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
