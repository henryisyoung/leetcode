# In-Experience Creation: Redesigning Orchestration for Low Latency

**Behavioral story (STAR)** — themes: disagree & commit, technical leadership, cross-team influence, adapting legacy infra.

---

## Situation

At Roblox I worked on **In-Experience Creation**, where users could create avatar assets directly inside a live game experience.

This was a major shift from the existing async UGC pipeline: users were now waiting in-game for an asset that had to render immediately, while we still had to preserve moderation and asset integrity.

## Task

I was responsible for defining how to support this new experience on infrastructure that had been **designed around asynchronous workflows**.

The tension: many teams initially wanted to **keep the existing architecture** because it was proven and stable. I disagreed — simply reusing it would create a poor UX because the latency was far too high for an in-game workflow.

> **Core question:** How do we redesign the orchestration layer to achieve low latency *without* compromising reliability and moderation coverage?

## Action

I reviewed the existing creation pipeline with engineering and found that dependency creation and moderation were processed **sequentially**, which was too slow for an in-game experience.

I proposed parallelizing dependency creation and moderation, but the team raised valid concerns around **race conditions, partial failures, and state consistency**. **We worked through those risks together** and designed an orchestration workflow that tracked each dependency independently, maintained per-stage status, supported retries, and aggregated state into a **single source of truth**.

That allowed us to reduce latency through parallel processing while preserving correctness, moderation coverage, and asset integrity. **Once we aligned on the design, I drove execution across multiple teams.**

## Result

- Enabled a **much lower-latency** creation experience while preserving moderation and asset-integrity requirements.
- **Adapted batch/async infrastructure into a near real-time experience** — without a full platform rewrite.
- Established a **reusable orchestration model** for complex dependency graphs and parallel processing, becoming the **foundation for future real-time creation experiences**.

---

## Talking points / likely follow-ups

- **Disagree & commit:** I pushed back on reusing async infra, but once the team co-designed the mitigations I committed fully and drove it.
- **Correctness under parallelism:** independent per-asset state tracking + single source of truth + retries/recovery is what made parallel moderation safe.
- **The hard part:** moderation can't be skipped for speed — parallelizing it (rather than gating sequentially) is the key insight.
- **Waymo bridge:** same shape as orchestrating **parallel, independently-failing pipeline stages** (perception/labeling/validation) with a defensible single source of truth and recovery — trading naive sequential safety for parallel throughput without losing correctness guarantees.
