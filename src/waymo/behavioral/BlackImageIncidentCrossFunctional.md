# Black Image Incident: Leading a Cross-Functional Ecosystem Fix

**Behavioral story (STAR)** — themes: cross-functional leadership, reframing scope, owning the end-to-end outcome, aligning orgs with conflicting goals, turning a recurring fire into a systemic fix, splitting work across my team.

> **Maps to questions like:** "Lead a complex cross-functional initiative to solve a major customer problem," "align teams with competing priorities," "drive a solution outside your direct control," "turn a recurring fire into a systemic fix," "deliver impact across org boundaries."

> **Use this one for "align teams with competing goals."** [AvatarAssetVersioning](./AvatarAssetVersioning.md) is stronger for impact and prioritization. This one is stronger for influence without authority: four orgs, four different definitions of success.

---

## Scope (say this first, then stop)

> "I am the tech lead for Marketplace Safety at Roblox. I have 7 to 8 engineers, and I work with an engineering manager and a PM. At that time my team had [three or four] initiatives running. This one I took myself, because the fix was not inside my team. It needed four orgs to agree on what success means, and only the tech lead could have that conversation. The build work I split across [N] engineers."

---

## Situation

At Roblox we kept hitting a high-severity incident we called the **"Black Image Incident."** It came from a safety mechanism we need: moderation propagation. When an image is moderated, every avatar asset that depends on that image is moderated too.

Bad actors learned to abuse this. They targeted images that many assets use. One moderation action then took down a large number of items at once. That meant a lot of creator impact, bad user experience, refunds, and lost marketplace bookings.

Earlier fixes did not stick. There were many independent moderation entry points, and each one could start the same cascade. Fixing one path did not stop the others.

## Task

As the tech lead, I saw this was not a moderation bug. It was an **ecosystem problem**. Each team was optimizing for a different goal:

- **Safety** — reduce platform risk
- **Moderation** — enforce policy fast
- **Dev Money** — refunds
- **Marketplace** — creator and user experience

Each team saw its own slice. Nobody saw the full downstream cost, economic and user-facing together. So nobody owned the whole thing.

I decided my team would own it. That was a real trade. To staff it I moved **[N] engineers off [OTHER INITIATIVE] for [one quarter]**. I made that case to the PM and the EM with the incident cost: **[N] incidents per quarter, [N] on-call hours, [$X] in unintended refunds**. Recurring incident cost against one quarter of build — that trade was easy to defend.

## Action

**First, I changed how the problem was described.** I reframed it from separate moderation events into one ecosystem reliability problem. Then I met Safety, Marketplace, Moderation Operations, and Dev Money one by one, before any group meeting, and got each of them to agree on the full end-to-end picture of impact.

**Second, I changed what success means.** Not "remove risk as fast as possible." Instead: **keep the platform safe while protecting creator trust, buyer experience, and marketplace stability.** This was the hardest alignment. Safety wanted to minimize risk. Marketplace and Dev Money wanted to minimize disruption and refunds. We had to agree that success includes both.

**Third, I made the key technical decision.** We would **not** weaken propagation. It is a safety mechanism, and removing it is the wrong fix. We would invest in fast, safe, automated recovery plus guardrails instead. I also decided this had to be **one shared recovery and guardrail layer**, not a fix in each moderation entry point, because per-entry-point fixes are exactly what had failed before.

**Then I split the work across my team:**

- **[Engineer A / two engineers]** — safer restoration workflows, to recover affected assets quickly
- **[Engineer B]** — automated remediation paths, to cut manual work during recovery
- **[Engineer C]** — end-to-end validation and testing for the restoration and refund pipelines
- **[Engineer D]** — guardrails to stop unintended refunds caused by propagation

I owned the architecture, the interfaces between these four pieces, and the cross-org contract. I reviewed each design before building started. [If true: I gave the guardrail piece to a mid-level engineer as a stretch project and stayed close as support.]

## Result

- Much better ability to **respond to propagation incidents**: [incident recovery time from X to Y], [manual interventions per incident from X to Y].
- Lower operational overhead and customer impact: [unintended refunds down by X].
- The org changed how it thinks — from reacting to single moderation events to looking at the **full end-to-end ecosystem impact**.
- A safer and more resilient moderation system, with less disruption for creators and users, across four orgs with different priorities.
- [If true: the shared recovery layer now has an owner on my team who is not me, and other teams call it when they add a new moderation entry point.]

---

## Why I drove this one myself (say this)

> "I did not take it because it was the hardest code. The code I split across my engineers. I took it because the real problem was between four orgs, and each org was right about its own goal. Somebody had to own the whole outcome, and the tech lead is the person who can talk to all four with technical credibility."

## The one-line summary (say this)

> "The technical issue was not the hardest part. The hardest part was getting four orgs with different goals to agree on one definition of success. I owned the end-to-end outcome, not only the moderation system. I made the call not to weaken propagation, put my engineers on one shared recovery layer, and traded [OTHER INITIATIVE] to pay for it."

## What tech lead signals this shows

- **Reframing scope:** turned "fix this moderation bug" into "this is an ecosystem reliability problem."
- **Owning beyond my team:** owned refunds, bookings, and creator trust, not only the safety system.
- **Influence without authority:** aligned four orgs with conflicting incentives on one definition of success.
- **The trade:** moved [N] engineers off [OTHER INITIATIVE] to staff it.
- **Allocation:** split four workstreams across my engineers; I kept the architecture and the cross-org contract.
- **Systemic, not reactive:** earlier fixes were point fixes. This attacked the shared failure mode and the recovery pipeline.

## How the alignment actually happened

Same five steps as [TechLeadFraming §4](./TechLeadFraming.md):

1. **Found the cost.** [N] incidents per quarter, [N] on-call hours, [$X] unintended refunds — one number that every org could see.
2. **Found the sponsors.** Dev Money (refunds) and Marketplace (bookings) were the ones hurt most. They became allies, not blockers. I talked to each org one-on-one before the group meeting.
3. **Named the trade.** [N] engineers off [OTHER INITIATIVE] for [one quarter].
4. **Designed for adoption.** One shared recovery layer, so a new moderation entry point gets protection without writing its own recovery.
5. **Made it stick.** [If true: recovery and guardrail checks became part of the review for any new moderation entry point.]

## Talking points / likely follow-ups

- **"Why did earlier fixes fail?"** Many independent moderation entry points. Fixing one path did not stop the cascade. We needed one shared recovery and guardrail layer.
- **"Why not just stop propagation?"** Propagation is needed for safety. You cannot remove it. The fix is fast, safe restoration plus guardrails, not turning off the mechanism.
- **"Hardest alignment moment?"** Getting Safety (minimize risk) and Marketplace / Dev Money (minimize disruption and refunds) to agree that success includes both.
- **"Who did what on your team?"** Walk through the four workstreams by name. Do not say "we built" without saying who.
- **"What did you give up?"** [OTHER INITIATIVE], pushed by [one quarter]. Prepare this — they will ask.
- **"How did you measure success?"** Incident recovery time, manual interventions per incident, unintended refund volume, bookings impact.
- **Waymo bridge:** same shape as a **safety mechanism with over-broad side effects that people can abuse**. You cannot weaken the safety trigger. So you invest in fast, automated, validated recovery and shared guardrails, and you get the orgs to agree on a success metric that is not just "maximize safety alone."

---

## Numbers and names to fill in

| Item | Status |
|---|---|
| Initiative deprioritized, and for how long | `[OTHER INITIATIVE]`, `[one quarter]` — needed |
| Engineers on it, and who owned each workstream | `[N]`, `[Engineer A–D]` — needed |
| Incidents / on-call hours / unintended refunds per quarter, before | `[N]` / `[N]` / `[$X]` — needed |
| Recovery time and manual interventions, before and after | `[X → Y]` — needed |
| Does the shared layer have a non-me owner, and is it in a review gate? | `[yes / no]` — keep those lines only if true |
