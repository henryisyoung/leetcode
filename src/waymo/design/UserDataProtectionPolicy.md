# System Design — User Data Protection (GDPR / CCPA / AV-Specific Policy)

> AV interview prompt: policy-flavored system-design rounds at Waymo ask how
> you'd protect user (and bystander) data against GDPR, CCPA/CPRA, and the
> NHTSA / state-DMV reporting obligations that come with operating an
> autonomous fleet. This note is the framework to drill before the round.

Policy-flavored system design is its own discipline. The good news: there's a
small framework that handles ~80% of the questions, and you can rehearse it the
same way you rehearse caching or sharding.

---

## Phase 0: The Mental Framework — drill this until it's automatic

Whenever a privacy/policy question shows up, walk through these **eight
buckets** in the same order. Make this your "scaling 101" for policy:

1. **Data inventory** — *what* data, *where* it lives, *who* it's about,
   *who* generated it.
2. **Lawful basis & consent** — under what theory are we collecting it
   (consent, contract, legitimate interest, legal obligation)? How is
   consent captured, withdrawn, and proven?
3. **Purpose limitation & minimization** — only collect what you need for the
   stated purpose. Different purpose ⇒ new basis ⇒ usually new consent.
4. **Storage architecture** — encryption at rest, key management (KMS,
   envelope encryption), data classification tiers, region pinning for
   residency.
5. **Access control** — RBAC/ABAC, least privilege, just-in-time elevation,
   audit logging on access. "Who can see PII and how do we prove it later?"
6. **Retention & deletion** — schedule per data category; hard-delete vs
   tombstone; how it propagates through caches, backups, logs, ML training
   sets, third-party processors.
7. **Subject rights pipeline** — DSAR / right-to-know, right-to-delete
   (GDPR Art. 17), right-to-portability (Art. 20), right-to-correct. Who
   fans out the request? What's the SLA?
8. **Incident response** — breach detection, 72-hour GDPR notification,
   customer notification, post-mortem.

Any policy question in the room ⇒ pick the 2–3 buckets it's really asking
about and dive in. The framework keeps you from forgetting the obvious ones.

---

## Phase 1: Regulations — learn *what the system must do*, not the legal text

You don't need to argue case law. You need a single sentence per regime
explaining what it forces the architecture to do.

| Regime | Pithy "what it forces" |
| --- | --- |
| **GDPR** (EU/UK) | Lawful basis, DSARs within 30 days, breach notice in 72 hrs, DPIA for high-risk processing, no transfer to non-adequate countries without SCCs, *right to be forgotten*. |
| **CCPA / CPRA** (California — Waymo's home) | Right to know, delete, correct, opt out of sale/share, limit use of "sensitive PI" (precise geolocation is in this bucket — extremely relevant for AV). |
| **State patchwork** (VA / CO / CT / UT / TX) | Mostly CCPA-shaped; design for the strictest and call it done. |
| **COPPA** | Children under 13 → no data collection without verifiable parental consent. |
| **GLBA / PCI-DSS** | Payment data has its own encryption, tokenization, audit, and scope-reduction rules. |
| **HIPAA** | Only if you touch health data; AV usually doesn't directly. |
| **NHTSA Standing General Order** (US AV-specific) | You *must* report safety incidents within 24h / 5-day windows. Privacy ↔ reporting tension. |
| **CA DMV AV permits** | Disengagement reports, crash reports, OPDMP requirements. |

> Read a primer on each (an hour total). You don't need a CIPP cert.

---

## Phase 2: The Waymo-Specific Scenarios to Pre-Rehearse

These are the scenarios where AV policy questions actually land. Walk through
each before the interview, naming which of the 8 buckets each one stresses.

### 1. Bystander / pedestrian sensor data

Cars record everyone within ~100 m — faces, license plates, gait, sometimes
through windows. None of them consented.

- **Buckets:** lawful basis, purpose limitation, storage.
- **Talking points:** edge-side face/plate blurring, on-device retention
  windows, *purpose limitation* (safety vs training vs marketing), aggregation
  vs raw frames, "legitimate interest" basis under GDPR, balancing test
  documented in the DPIA.

### 2. Rider location data

Pickup/dropoff are by definition precise geolocation — **CPRA "sensitive PI"**
and GDPR special-category-adjacent (a synagogue, an abortion clinic, a divorce
attorney's office).

- **Buckets:** storage, access control, retention.
- **Talking points:** pseudonymous storage with a separated re-identification
  table, automatic fuzzing of trip endpoints in analytics, retention 90 / 365
  days for safety vs forever for ML, blocklisting of sensitive POIs in derived
  data.

### 3. Right-to-be-forgotten on a rider

A user demands deletion. Where does their data live?

The honest answer is **everywhere**: operational DB, OLAP warehouse, S3
archives, model training corpora, vendor pipelines, payment processor, support
ticket attachments, ML feature stores, sensor logs from their actual rides.

- **Design:** a deletion *request bus* that fans out to every downstream
  system; tombstones with proof-of-deletion timestamps; documented exceptions
  (legal hold, safety reporting under NHTSA — explicit GDPR Art. 17(3)(b)
  carve-out).

### 4. Cross-border data flow

Waymo expanding into EU / UK / Japan / Singapore.

- **Buckets:** storage, lawful basis.
- **Talking points:** SCCs, regional data planes, encryption with regional
  KMS, residency tagging at the row level, no cross-region replication of raw
  data.

### 5. NHTSA crash reporting vs GDPR deletion

A user demands deletion of a ride that's also part of a federally-mandated
crash report.

- This is the *legal obligation* lawful basis overriding deletion. Document
  the carve-out, retain the minimum necessary, expire when the obligation
  ends.

### 6. ML training set leakage

Faces / plates / interior conversations in fleet learning.

- **Buckets:** purpose limitation, storage, access control.
- **Talking points:** differential privacy, federated learning, sanitization
  pipeline before data hits the ML lake, audit trail per training run for
  which raw frames went in.

### 7. Internal access by operators / safety teams

Remote-assistance operators see live video.

- **Buckets:** access control, incident response.
- **Talking points:** JIT access, recorded sessions, four-eyes for sensitive
  actions, watermarking, access logs reviewed by security.

### 8. Vendor / sub-processor

Mapping data, third-party annotation services labeling sensor frames,
insurance, payments.

- **Buckets:** lawful basis, storage, incident response.
- **Talking points:** DPA in place, vendor list published, breach-notification
  contracts, no transfer to non-adequate jurisdictions, audit rights.

> If you can speak fluently to any 3 of these, you'll handle whatever they
> throw at you.

---

## Phase 3: Answer Toolbox — Standard Architectural Moves

Have these ready as **named patterns**. Mention them by name and the
interviewer relaxes.

- **Envelope encryption with a KMS** — data keys per dataset, master keys in
  HSM, rotation policy.
- **Tokenization** for things you must reference but rarely need raw (payment,
  sometimes user id).
- **Pseudonymization with a one-way break-glass** — separate re-id table
  accessible only via audited request.
- **Field-level vs row-level vs file-level encryption** — pick by access
  pattern.
- **Tiered data classification** (Public / Internal / Confidential /
  Restricted-PII / Restricted-SPI). Every dataset gets one tag at ingest.
- **Retention enforcement via TTL + scheduled jobs**, with proofs (delete
  audit log).
- **DSAR fan-out service** — central inbox, per-system adapters, SLA
  tracking, attestation logs.
- **Consent management service** — versioned consents (`v3 of
  marketing-tracking consent`), event-sourced so you can prove what was true
  at time T.
- **Data lineage** (OpenLineage, Marquez, internal). When deletion runs you
  can prove the downstream blast radius.
- **Audit logging** that's append-only, off-system, retained per regulation
  (often 1–7 years).
- **Privacy by design** — "shift left": data minimization in the schema
  review, DPIA in the design review.
- **Edge sanitization** — blur faces / plates before frames leave the
  vehicle when possible.
- **Differential privacy / k-anonymity** for analytics surfaces.

---

## Phase 4: Reference Architecture for a Waymo-Shaped Data Plane

A skeletal layout you can sketch on the whiteboard in <2 minutes.

```
┌─────────────────────────────────────────────────────────────────┐
│ Vehicle (edge)                                                  │
│   sensors → on-device blur/anonymize → tiered local buffer      │
│             (raw 30 min, derived 24 h)                          │
│   ──────  upload over mTLS  ──────────────────────────────────► │
└─────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│ Ingest gateway  (regional)                                      │
│   - residency tag on every event                                │
│   - classification tag (Public / .. / Restricted-SPI)           │
│   - envelope-encrypted; data key in regional KMS                │
└─────────────────────────────────────────────────────────────────┘
                                  │
              ┌───────────────────┼─────────────────────┐
              ▼                   ▼                     ▼
        Hot store          Cold archive          Sanitized ML lake
        (rides, accts)     (raw sensor)          (blurred frames)
        per-region         per-region            cross-region OK
              │
              ▼
        DSAR / deletion bus  ──►  per-system adapter ──► tombstone + proof
              │
              ▼
        Audit log (append-only, off-system, immutable)

  Consent service ────────────► policy decision point ──► access control
  Lineage service ────────────► deletion blast-radius   ──► fan-out planner
```

Key invariants:

- **Every datum is classified at ingest**; downstream decisions read the tag.
- **No raw sensor data ever leaves region without sanitization** (face/plate
  blur, audio strip, geo-fuzz).
- **Deletion is a fan-out service, not a single SQL DELETE.** It writes a
  tombstone + an attestation that says "we deleted from system X at time T
  with proof Y".
- **Audit logs are immutable and off-system** — your own ops people can't
  rewrite them.
- **Legal-hold flags** override deletion; the DPIA explains why.

---

## Phase 5: Study Plan (~8–12 hours total)

1. **Read primers (3 hrs).**
   - GDPR: the IAPP one-pager + the actual Articles 5, 6, 13–22, 25, 32, 33,
     34. They're short.
   - CCPA / CPRA: the California AG's "FAQs for businesses".
   - NHTSA Standing General Order on AV crash reporting.
   - California DMV AV regulations summary.

2. **Read real privacy practices (2 hrs).**
   - Waymo's actual privacy policy (it's on waymo.com; read it twice).
   - Their published Safety Report and Safety Framework — sets the tone for
     how Waymo talks about data.
   - Compare with Cruise's, Aurora's, Tesla's policies — see what differs.
   - Skim Google's transparency reports for vibe.

3. **Build your "8-bucket cheat-sheet" (1 hr).** A single page you can
   mentally pull up. The eight buckets above plus a few key acronyms.

4. **Mock-rehearse the 8 Waymo scenarios (3–4 hrs).** Whiteboard-style. Talk
   it through out loud. Time yourself ~20 minutes per scenario.

5. **Drill answer-toolbox naming (1 hr).** Make sure you can say "envelope
   encryption with regional KMS and column-level keys" without stumbling.

---

## Phase 6: Practice Questions to Drill

Throw these at yourself or a study partner. None has a single right answer;
the point is practicing the framework and trade-off discussion.

1. A rider asks for all data about them. What pipeline serves the request
   and how do you make it auditable?
2. Design the retention policy for raw sensor data. How long do you keep
   camera frames? LiDAR point clouds? Why?
3. A regulator subpoenas a year of rides through a specific intersection.
   How do you respond?
4. How would you train a perception model on fleet data without violating
   user / bystander privacy?
5. A bug exposed 10k riders' phone numbers to an internal Looker dashboard.
   Walk me through the next 72 hours.
6. How would you do A/B testing on the routing algorithm without storing
   personal location traces?
7. Operator collusion / insider threat: a remote-assist operator
   screenshots a ride. Detection and prevention?
8. Waymo wants to launch in Munich. What changes in the data architecture?
9. Right-to-be-forgotten on a rider whose ride is part of a
   federally-mandated safety report. How do you reconcile?
10. Sensitive POIs (clinics, places of worship) — how do you prevent them
    from leaking into derived analytics?

---

## Phase 7: What Good Answers Look Like in the Room

Three habits that signal seniority:

- **Lead with the bucket name.** *"This is mostly a purpose-limitation
  question, with a retention angle."* Then design.
- **Always name the trade-off explicitly.** *"Edge-blurring saves us a
  re-identification problem but costs vehicle compute and reduces our
  model-training options — here's how I'd split the difference."*
- **Cite the regulation by name, briefly.** *"GDPR Art. 17 gives the right
  but 17(3)(b) carves out legal obligations, so the NHTSA-mandated
  retention wins. We document the carve-out and minimize what's
  retained."*

You don't need to know article numbers cold, but knowing the **exception
structure** signals real fluency.

---

## TL;DR

- Memorize the 8 buckets.
- Memorize the one-sentence "what it forces" for each regulation.
- Pre-rehearse the 8 Waymo scenarios.
- Have the 10-pattern answer toolbox at your fingertips.
- Always lead with which bucket the question is asking about, and always
  name the trade-off.
