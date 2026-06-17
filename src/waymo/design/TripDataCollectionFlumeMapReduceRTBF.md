# Trip Data Collection Pipeline: Flume -> MapReduce Analytics -> RTBF

## Prompt

Design a system that collects user/trip data for every Waymo ride, ships it
through Flume into a data lake, runs MapReduce analytics, and later supports a
rider's Right to be Forgotten request.

The interview is testing whether you can connect old-school big-data ingestion
with privacy requirements:

- Flume reliably collects and ships trip logs/events.
- MapReduce builds batch analytics from raw trip data.
- RTBF requires lineage from raw events to every derived analytics output.

---

## Concrete Example

Rider `u123` takes trip `t789` in Munich.

The system collects events like:

```json
{
  "eventType": "TripCompleted",
  "tripId": "t789",
  "userId": "u123",
  "vehicleId": "v456",
  "region": "EU",
  "pickupGeoHash": "u281z7",
  "dropoffGeoHash": "u281xk",
  "startTs": "2026-06-15T08:05:00Z",
  "endTs": "2026-06-15T08:28:00Z",
  "fareCents": 1850,
  "etaErrorSeconds": 42
}
```

Analytics wants to answer:

- Average ETA error by city and hour.
- Pickup hotspot counts.
- Trip duration percentiles.
- Revenue by region.

Privacy later needs to answer:

- Where did `u123`'s raw trip events land?
- Which MapReduce outputs included `u123`?
- Can we delete or rebuild those outputs when `u123` asks for RTBF?

---

## High-Level Architecture

```text
Trip service / vehicle gateway / billing service
   |
   v
Local Flume agent
   source: application log files / HTTP event source
   channel: encrypted durable file channel
   sink: regional HDFS or object-store raw zone
   |
   v
Raw data lake
   /raw/trips/region=EU/date=2026-06-15/hour=08/part-000.avro
   |
   v
MapReduce analytics jobs
   - aggregate by region/hour/geohash
   - produce analytics tables
   - produce lineage tables
   |
   v
Analytics warehouse / dashboard tables
   |
   v
RTBF orchestrator uses lineage to delete raw data and rebuild derived outputs
```

---

## Step 1: Collection At Trip Time

When a trip completes, the trip service writes an event to a local append-only
log file:

```text
/var/log/waymo/trips/trip-events.log
```

Example line:

```json
{"eventType":"TripCompleted","tripId":"t789","userId":"u123","region":"EU","pickupGeoHash":"u281z7","dropoffGeoHash":"u281xk","startTs":"2026-06-15T08:05:00Z","endTs":"2026-06-15T08:28:00Z","etaErrorSeconds":42}
```

The local Flume agent tails that file.

Flume config, conceptually:

```text
source  = tail /var/log/waymo/trips/trip-events.log
channel = durable encrypted file channel
sink    = HDFS/S3 raw zone in the same region
```

Flume's job is not analytics. Flume's job is reliable shipping:

- If the network is down, the file channel buffers events locally.
- If the sink is slow, Flume applies backpressure.
- If the process restarts, events in the channel are not lost.
- If delivery succeeds, the event is written to the raw zone.

For EU trips, the sink must be EU-pinned:

```text
/raw/trips/region=EU/date=2026-06-15/hour=08/part-000.avro
```

Privacy controls at this stage:

- Encrypt the local Flume channel.
- Use mTLS from agent to sink.
- Tag every event with `region`, `purpose`, `retention`, and schema version.
- Avoid writing extra personal data into logs.

---

## Step 2: Minimize And Tokenize

Do not let every analytics job read direct identifiers like `userId`.

At ingest, split the data:

```text
Restricted identity table:
  userId -> subjectToken
  u123   -> st_abc

Raw trip event:
  tripId=t789
  subjectToken=st_abc
  pickupGeoHash=u281z7
  etaErrorSeconds=42
```

The direct `userId` mapping is restricted. Most analytics jobs only see
`subjectToken`, and many jobs should not even need that.

This is still personal data because a token can be linked back to a user, but it
reduces exposure and makes deletion easier.

---

## Step 3: Store Raw Events

Raw trip events are stored in a partitioned data lake:

```text
/raw/trips/region=EU/date=2026-06-15/hour=08/part-000.avro
/raw/trips/region=EU/date=2026-06-15/hour=09/part-000.avro
/raw/trips/region=US/date=2026-06-15/hour=08/part-000.avro
```

Each file has metadata:

```json
{
  "filePath": "/raw/trips/region=EU/date=2026-06-15/hour=08/part-000.avro",
  "schemaVersion": 3,
  "region": "EU",
  "retentionDays": 30,
  "purpose": "analytics",
  "containsPersonalData": true
}
```

Important design choice:

Store a reverse index at ingest time:

```text
subjectToken -> raw file paths / row groups / tripIds
st_abc       -> /raw/trips/region=EU/date=2026-06-15/hour=08/part-000.avro, t789
```

Without this index, RTBF becomes a petabyte scan.

---

## Step 4: Run MapReduce Analytics

Example analytics job: average ETA error by pickup area and hour.

Input:

```text
/raw/trips/region=EU/date=2026-06-15/hour=08/*
```

Mapper:

```text
TripCompleted(tripId=t789, subjectToken=st_abc, pickupGeoHash=u281z7, etaErrorSeconds=42)
  -> emit key=(region=EU, hour=08, pickupGeoHash=u281z7), value=(etaErrorSeconds=42, count=1)
```

Reducer:

```text
key=(EU, 08, u281z7), values=[42, 30, 60, ...]
  -> output avgEtaErrorSeconds, tripCount
```

Output:

```text
/analytics/eta_error/date=2026-06-15/region=EU/part-000.parquet
```

Example row:

```json
{
  "region": "EU",
  "hour": "08",
  "pickupGeoHash": "u281z7",
  "tripCount": 186,
  "avgEtaErrorSeconds": 37
}
```

Privacy rule:

Only publish aggregate rows if the group is large enough.

```text
if tripCount < 50:
  suppress the row
```

This avoids publishing analytics that identify one rider's routine.

---

## Step 5: Write Lineage During MapReduce

The most important privacy feature is lineage.

For every output partition, record which input partitions contributed:

```json
{
  "jobId": "eta-error-2026-06-15-08",
  "outputPath": "/analytics/eta_error/date=2026-06-15/region=EU/part-000.parquet",
  "inputPaths": [
    "/raw/trips/region=EU/date=2026-06-15/hour=08/part-000.avro"
  ],
  "inputSnapshot": "hdfs-snapshot-9912",
  "codeVersion": "git_sha_abc123"
}
```

Also record subject-level lineage for RTBF:

```json
{
  "subjectToken": "st_abc",
  "tripId": "t789",
  "rawPath": "/raw/trips/region=EU/date=2026-06-15/hour=08/part-000.avro",
  "derivedOutputs": [
    "/analytics/eta_error/date=2026-06-15/region=EU/part-000.parquet",
    "/analytics/pickup_hotspots/date=2026-06-15/region=EU/part-000.parquet"
  ]
}
```

This lets the deletion system avoid guessing.

---

## Step 6: A Rider Requests RTBF

Later, rider `u123` says:

> Delete my account and all trip data.

The RTBF flow:

```text
Privacy API
   |
   v
Identity resolver
   userId=u123 -> subjectToken=st_abc
   |
   v
RTBF orchestrator
   |
   +--> restricted identity table: delete userId -> subjectToken mapping
   +--> raw trip lake: delete or crypto-shred files/row groups containing st_abc
   +--> analytics lineage: find derived outputs that included st_abc
   +--> MapReduce rebuild jobs: rebuild affected partitions without st_abc
   +--> tombstone registry: remember st_abc must not reappear
   +--> audit log: record proof and legal holds
```

The orchestrator consults the lineage index:

```text
st_abc -> rawPath=/raw/trips/region=EU/date=2026-06-15/hour=08/part-000.avro
st_abc -> derivedOutput=/analytics/eta_error/date=2026-06-15/region=EU/part-000.parquet
```

Then it performs deletion actions.

For raw data:

- If data is stored in per-subject encrypted chunks, destroy `st_abc`'s key.
- If data is stored in shared files, rewrite the affected file/row group without
  `st_abc`.
- If data is already expired by retention, record `not_present`.

For derived analytics:

- If the output is truly aggregate and cannot identify the user, it may be kept
  depending on policy.
- If the output is still personal or too small a group, rebuild the partition
  without `st_abc`.
- If the metric changes after removal, atomically publish the rebuilt partition.

For backups:

- Record a tombstone.
- If a backup is restored, replay tombstones before exposing restored data.

For legal holds:

- Crash or safety-investigation records may be retained.
- The proof must say exactly what was held and why.

---

## Step 7: Prevent Reintroduction

RTBF is not complete if a future backfill can bring the user back.

Every MapReduce job must read the tombstone registry:

```text
deleted_subject_tokens:
  st_abc
```

During future jobs:

```text
if event.subjectToken in deleted_subject_tokens:
  skip event
```

This protects against:

- Reprocessing old Flume raw logs.
- Restoring old HDFS/object-store backups.
- Running a historical analytics backfill.
- Rebuilding ML or analytics datasets from stale snapshots.

---

## End-To-End Walkthrough

1. `u123` completes trip `t789`.
2. Trip service writes a `TripCompleted` event locally.
3. Flume tails the local log and buffers it in an encrypted file channel.
4. Flume ships the event to the EU raw data lake.
5. Ingest tokenizes `userId=u123` into `subjectToken=st_abc`.
6. Raw event lands in `/raw/trips/region=EU/date=2026-06-15/hour=08/`.
7. A reverse index records `st_abc -> raw file + tripId`.
8. Nightly MapReduce reads the raw trip partition.
9. Mapper emits `(region, hour, pickupGeoHash) -> etaErrorSeconds`.
10. Reducer writes aggregate ETA analytics.
11. Job writes lineage: output partition was derived from raw files containing
    `st_abc`.
12. Dashboard reads only the aggregate analytics table.
13. Later, `u123` requests RTBF.
14. RTBF orchestrator resolves `u123 -> st_abc`.
15. Orchestrator deletes or crypto-shreds raw trip data.
16. Orchestrator finds derived analytics partitions through lineage.
17. A rebuild job recomputes affected partitions without `st_abc`.
18. The tombstone registry records `st_abc` so future backfills skip it.
19. Audit log records every action and any legal hold.
20. User receives completion proof.

---

## Why Flume Instead Of Kafka Here?

Flume is a collector and shipper. It is useful when the source is logs on many
hosts and the destination is HDFS/object storage.

Kafka is a durable event log. It is better when many consumers need to read the
same stream independently and at different speeds.

For this prompt, an older Hadoop-style answer is:

```text
Trip logs -> Flume -> HDFS -> MapReduce -> Analytics
```

A more modern answer is:

```text
Trip events -> Kafka -> stream processors / object store -> warehouse
```

If the interviewer explicitly says Flume and MapReduce, use the first pipeline.
If they ask for a modern event platform, mention Kafka or a managed log pipeline.

---

## What To Say In The Interview

Use this compact version:

> "I would put Flume agents near the trip-producing services to reliably ship
> trip events into a regional raw data lake. At ingest I would minimize and
> tokenize direct identifiers, partition raw events by region/date/hour, and
> build a reverse lineage index from subject token to raw files and derived
> outputs. MapReduce jobs would produce aggregated analytics with k-anonymity
> thresholds and record input snapshots. For RTBF, the deletion orchestrator
> resolves the rider to all subject tokens, deletes or crypto-shreds raw data,
> rebuilds affected analytics partitions, writes tombstones to prevent future
> backfills, and produces an audit proof with any legal holds."

The senior signal is:

> "The hard part is not collecting the logs. The hard part is proving where a
> user's data flowed after MapReduce and preventing it from reappearing during a
> backfill or restore."
