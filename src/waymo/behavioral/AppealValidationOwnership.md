# Appeal Validation: Closing High-Impact Safety Gaps

**Behavioral story (STAR)** — themes: ownership, high-ROI safety investment, technical leadership, defense-in-depth, cross-team execution.

> **Maps to questions like:** "Tell me about a time you took ownership of a high-impact problem," "a problem where you closed a major gap," "a time you improved a process," "driving a project end-to-end," "earn trust."

---

## Situation

At Roblox I was the **technical lead for the Avatar Moderation platform**, including the creator appeal workflow.

We saw a recurring gap: some items restored after successful appeals later turned out to contain exploits or policy violations. Human review, ML signals, and platform checks each had partial information, so false-positive appeal approvals could let problematic content back onto the platform.

## Task

As the moderation tech lead, I saw this as a **high-ROI safety and trust investment**. My goal was to close that gap by making appeal outcomes more reliable without slowing down every appeal review or replacing human judgment.

The core technical challenge: **how do you combine multiple independent, imperfect signals into a more reliable final decision?**

## Action

I **initiated and drove a new decisioning framework end-to-end.** Instead of relying solely on a moderator's appeal decision, I designed a system that **aggregated signals from multiple sources**:

1. **Better moderator inputs** — surfaced **ML-generated attributes directly in the moderation UI**, so moderators focused on specific risk areas instead of reviewing fully manually.
2. **A final validation stage** — after all moderation decisions completed, an automated stage ran **deeper technical checks** that neither humans nor ML easily catch:
   - hidden dependencies
   - invisible textures
   - malformed asset structures
   - exploit-related configuration issues

   If validation detected a **severe** issue, it could **override the appeal approval** and block restoration.

I worked across **moderation, ML, and platform teams** to design, implement, and deploy it.

## Result

- **Significantly reduced false-positive appeal approvals** and prevented exploit content from being restored.
- **Solved a long-standing moderator frustration** — they previously approved content only to find hidden issues later.
- By combining **human judgment + ML signals + automated validation** into one framework, we **raised confidence in moderation outcomes and improved platform safety**.

---

## Why this is an "ownership" story (say this)

> "No one asked me to fix this. I saw a systemic gap — imperfect checkers letting bad content slip through appeals — and as the tech lead I took it on, scoped it, and drove it across three teams to production."

## Variant: "Earn trust" question

Same facts, but redirect the spotlight from *"I owned it"* to *"people couldn't trust the moderation outcomes, and I changed that."* Be explicit about **whose** trust. There are two angles — pick based on how it's phrased.

**Angle 1 — Trust in the system (creators / safety partners / business):**

> "Appeal approvals were silently eroding trust in moderation — restored items kept turning out to contain exploits, so safety partners and creators couldn't rely on what 'approved' meant. I owned closing that gap; the validation framework made 'approved' something people could actually trust, and the measurable drop in false-positive restorations is what rebuilt that confidence."

**Angle 2 — Trust with the moderators (the stronger, more human fit):**

> "Moderators had quietly lost confidence in their own decisions — they'd approve something, then learn it still had hidden issues, which was demoralizing. I earned their trust by surfacing ML attributes in their UI and adding an automated backstop for the exploit-class problems they *couldn't* see. I wasn't second-guessing them — I was covering their blind spots. They went from frustrated to confident."

**Why this lands for "earn trust":** the principle is about listening, being self-critical, and not assuming you're right. The moderator angle shows you **listened to a frustrated stakeholder and built something that respected their judgment rather than replacing it** ("complement, don't override the human").

**Delivery cautions:**
- Lead with the **trust gap**, end with **how confidence was restored** (ideally a metric).
- Don't just retell the architecture — keep the relationship/credibility front and center.

## Talking points / likely follow-ups

- **Why not just 'make the ML better'?** Single checkers are fundamentally imperfect; the win was **defense-in-depth** — layering independent signals so the *system* is more reliable than any one component.
- **Why a final override stage?** Validation catches a *different class* of issues (structural/exploit) than humans or ML, so it's complementary, not redundant.
- **False negatives / over-blocking risk:** override only fires on **severe, high-confidence** technical issues to avoid wrongly blocking legitimate restorations; everything else still trusts the human decision.
- **Influence:** required aligning moderation (workflow), ML (attributes), and platform (validation infra) — driven without formal authority over those teams.
- **Waymo bridge:** same pattern as **safety validation gates** on an autonomy stack — don't trust a single model or human label; layer independent verifiers and let a final automated check veto unsafe outcomes.
