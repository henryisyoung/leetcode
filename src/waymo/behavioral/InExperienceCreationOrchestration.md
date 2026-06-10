# In-Experience Creation: Redesigning Orchestration for Low Latency

**Behavioral story (STAR)** — themes: disagree & commit, technical leadership, cross-team influence, adapting legacy infra.

---

## Situation

At Roblox I worked on **In-Experience Creation**, letting users create avatar assets directly inside a live experience.

The **existing UGC pipeline** was **async**: creators uploaded a model via Studio, and creation, moderation, and publishing ran as **background jobs** — users waited minutes.

**In-Experience Creation** flipped this: the user stayed in the game waiting, and the asset had to **render immediately**. So we had to **drastically cut latency** while preserving **moderation** and **asset integrity**.

## Task

I was responsible for defining how to support this new experience on infrastructure that had been **designed around asynchronous workflows**.

The tension: many teams initially wanted to **keep the existing architecture** because it was proven and stable. I disagreed — simply reusing it would create a poor UX because the latency was far too high for an in-game workflow.

> **Core question:** How do we redesign the orchestration layer to achieve low latency *without* compromising reliability and moderation coverage?

## Action

I partnered closely with engineering and reviewed **each step** of the existing creation pipeline.

Key observation: the traditional flow processed dependency assets **sequentially**:

```
create image → create mesh → create texture → run moderation → build final asset
```

Each step waited for the previous one to finish.

I proposed that for In-Experience Creation we treat **dependency creation and moderation as independent tasks** and execute them **in parallel**.

This raised real concerns from engineers — **race conditions, partial failures, state consistency**. Instead of pushing my solution unilaterally, I worked with the team to surface the risks and design mitigations.

We landed on an **orchestration workflow** that:

- tracked **every dependency asset independently**
- maintained **per-stage status**
- supported **retries and recovery**
- aggregated all intermediate states into a **single source of truth**

This let us parallelize creation and moderation while still guaranteeing correctness. Once we aligned on the design, I **committed fully** to the engineering approach and drove execution across multiple teams.

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
