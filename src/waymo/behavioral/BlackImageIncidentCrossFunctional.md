# Black Image Incident: Leading a Cross-Functional Ecosystem Fix

**Behavioral story (STAR)** — themes: cross-functional leadership, reframing scope, ownership of end-to-end outcome, aligning orgs with conflicting goals, incident reliability.

> **Maps to questions like:** "Lead a complex cross-functional initiative to solve a major customer problem," "align teams with competing priorities," "drive a solution outside your direct control," "turn a recurring fire into a systemic fix," "deliver impact across org boundaries."

---

## Situation

At Roblox we repeatedly hit a high-severity incident internally called the **"Black Image Incident."**

It came from how **moderation propagation** worked: when an image was moderated, every avatar asset depending on that image was **automatically moderated too**. That behavior was correct and necessary for safety — but **bad actors learned to abuse it.** By intentionally triggering moderation on **widely-used images**, they could indirectly take down **large numbers of innocent avatar assets**.

The cascade:

- large-scale **creator impact**
- poor **user experience**
- significant **refund volume**
- measurable hit to **marketplace bookings**

Prior remediations didn't stick, because there were **many independent moderation entry points** — copyright, abuse reports, safety reviews, automated detection — each able to trigger the same cascade.

## Task

As the **Marketplace Safety technical lead**, I realized this wasn't just a moderation bug — it was an **ecosystem problem**. Different teams optimized for different goals:

- **Safety** → reduce platform risk
- **Moderation** → enforce policy fast
- **Dev Money** → refunds
- **Marketplace** → creator & user experience

Each team saw its own slice but not the **downstream economic and user impact**. I took ownership of driving a broader solution across all of them.

## Action

I **reframed it from isolated moderation events into an ecosystem-wide reliability problem**, then aligned Safety, Marketplace, Moderation Operations, and Dev Money on a shared, **end-to-end** understanding of impact.

I drove several initiatives:

- designed **safer restoration workflows** to recover affected assets quickly
- **reduced manual intervention** during incident recovery
- built **automated remediation paths** where possible
- created **end-to-end validation/testing** for the restoration and refund pipelines
- established **guardrails** to minimize unintended refunds caused by moderation propagation

Most importantly, I shifted the definition of success: not "remove risk as fast as possible," but **balance platform safety with creator trust, purchaser experience, and marketplace stability**.

## Result

- Significantly improved our ability to **respond to propagation incidents** while cutting operational overhead and customer impact.
- Shifted the org mindset from **reacting to individual moderation events → understanding full end-to-end ecosystem impact.**
- Delivered a **safer, more resilient moderation system** while minimizing disruption to creators and users — across multiple orgs with different priorities.

---

## The one-line leadership summary (say this)

> "The technical issue wasn't the hardest part. The hardest part was aligning multiple organizations with different goals around a **shared definition of success**. I took ownership of the end-to-end outcome — not just the moderation system — and drove a solution balancing safety, user experience, and business impact."

## What leadership did I demonstrate?

- **Reframing scope:** turned "fix this moderation bug" into "this is an ecosystem reliability problem."
- **Ownership beyond my box:** owned the end-to-end outcome (refunds, bookings, creator trust), not just the safety system I led.
- **Influence without authority:** aligned four orgs with conflicting incentives on one definition of success.
- **Systemic vs. reactive:** prior fixes were point fixes; I attacked the shared failure mode and the recovery pipeline.

## Talking points / likely follow-ups

- **Why did prior fixes fail?** Many independent moderation entry points — fixing one path didn't stop the cascade; needed a shared recovery/guardrail layer.
- **Why not just stop propagation?** Propagation is necessary for safety — you can't remove it; the fix is fast/safe **restoration + guardrails**, not disabling the mechanism.
- **Hardest alignment moment:** getting Safety (minimize risk) and Marketplace/Dev Money (minimize disruption/refunds) to agree that "success" includes both.
- **How measured success:** incident response time, manual-intervention volume, unintended refund volume, bookings impact.
- **Waymo bridge:** same shape as a **safety mechanism with abusable/over-broad side effects** — you can't weaken the safety trigger, so you invest in fast, automated, validated recovery and cross-team guardrails, and you align orgs on a success metric that isn't just "maximize safety in isolation."
