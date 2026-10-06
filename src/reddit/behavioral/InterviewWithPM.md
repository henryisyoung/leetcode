# Reddit Interview Round with a PM

**Focus areas for this round:** cross-functional collaboration, conflict resolution with PMs, driving alignment, and responding to constructive feedback. **Prepare at least one strong example of meaningful feedback received and acted upon.**

The stories already exist in [`waymo/behavioral`](../../waymo/behavioral/README.md). This doc picks which one to use for each question, gives the spoken version for a PM listener, and lists the PM-specific questions they usually ask.

> **All quoted parts are written to be spoken.** Short sentences. Read them out loud once.

---

## What a PM interviewer is really checking

A PM asks themselves one question: *"Would I want this person as my engineering partner?"* That breaks down into:

1. **Do you care about the user and the business, not only the system?** Talk about outcomes, not only architecture.
2. **Can you disagree with me without making it a fight?** They want to see data, options, and a decision — not "engineering vs. product".
3. **Do you say no in a useful way?** "No, but here is what we can do by that date" — never just "no".
4. **Can you explain technical trade-offs in plain words?** If they need to ask what something means, you went too deep.
5. **Are you coachable?** The feedback question.

**Rule for this round:** keep technical depth to one or two sentences per story. Spend the time on the people, the trade-off, and the outcome.

---

## Opening (30 seconds)

> "I am the tech lead for Marketplace Safety at Roblox. I have 7 to 8 engineers, and I work very closely with a PM and an engineering manager — the three of us plan the roadmap together. The PM owns what we build and why. I own how we build it, and I tell the PM early what is expensive, what is risky, and what is cheap. Most of our work touches several orgs — Safety, Marketplace, Moderation Ops — so a big part of my job is getting people with different goals to agree."

---

## Which story for which question (no repeats)

| Focus area | Use | Why this one |
| --- | --- | --- |
| **Feedback received and acted on** | [MarketplaceRemediation — feedback variant](../../waymo/behavioral/MarketplaceModerationRemediation.md) | The feedback came from a PM. Real arc: defensive → reflect → change. The change is still how I work |
| **Conflict with a PM** | [LatencyRequirementFailure](../../waymo/behavioral/LatencyRequirementFailure.md), told as a disagreement | I challenged a requirement we had both agreed on, with data, and we fixed it together |
| **Disagree and commit** | In-Experience Creation (section 2b below) | Product wanted parallel moderation in seconds. I disagreed, then committed and found a design that kept the moderation bar |
| **Cross-functional collaboration** | [BlackImageIncident](../../waymo/behavioral/BlackImageIncidentCrossFunctional.md) | Four orgs, four different goals |
| **Driving alignment** | [AvatarAssetVersioning](../../waymo/behavioral/AvatarAssetVersioning.md) | Got PM and two EMs to fund it and lend engineers; aligned on safety policy |

If they ask two questions that want the same story, use the second choice from the README cheat sheet. **Never tell the same story twice in one round.**

---

## 1. Feedback received and acted upon (the must-have)

> **The feedback:** "My instinct as a safety tech lead was safety first. If content broke policy, take it down and refund. **[PM NAME]**, the Marketplace PM, told me directly: *you are optimizing for safety alone, and you are ignoring what this does to creators and to the business. You treat it like there are only two options.*"
>
> **How I took it:** "Honestly, my first reaction was defensive. These were real policy violations. But I thought about it for a day, and I saw the PM was not asking me to lower the safety bar. She was saying I had not looked for other options. That part was on me."
>
> **What I did:** "I went back and designed a third option: a remediation flow. The creator can fix the item and submit a corrected version. It goes through moderation again, and if it passes, it replaces the original. No takedown, no refund cycle. I brought it back to the PM and to Safety before I locked the design."
>
> **Result:** "Safety bar stayed the same, and disruption for creators and buyers went down. The bigger change was in me: now I bring the people who will disagree with me into the design **before** I commit to it, not after. That feedback made the solution much better."

**Why it works:** one named person, one honest moment of defensiveness, a concrete change, and a habit that lasted. **Fill in the PM's name** and one sentence of exactly what they said — that's what makes it real.

**Likely follow-ups:**
- *"How do you ask for feedback now?"* → "I share the design doc early with the people most likely to disagree, and I ask them directly: what would make you say no to this?"
- *"Feedback you disagreed with?"* → Have one short example ready where you listened, explained your reasons, and kept your position — and how you closed the loop with that person.

---

## 2. Conflict with a PM

Use LatencyRequirementFailure, told as a disagreement. **Only say the parts that are true** — the bracketed parts are where you need to check your memory.

> **Situation:** "We launched a moderation feature with a latency target the PM and I had agreed on. We hit the target. But users still complained it felt slow."
>
> **The conflict:** "[The PM's view was that we met the goal and should move on to the next item on the roadmap.] My view was that if users are unhappy, we did not really succeed — maybe the target was wrong. We had a real tension: I was asking to spend more time on something that, on paper, was done."
>
> **How I resolved it:** "I did not argue opinions. I asked for two weeks and brought data: complaint patterns, a few user sessions, and where in the flow the wait happened. It showed the delay came at the worst moment in the workflow, so it felt much longer than it was. Once we looked at it together, the PM agreed quickly — it became our shared problem, not my complaint."
>
> **Result:** "We redefined the goal around user-visible interruption, fixed the flow, and complaints went down. Then, together, we built a launch-readiness checklist so every moderation launch checks whether the metric really matches what users feel. It's now part of design and launch reviews."
>
> **What I learned about working with PMs:** "Disagreements go well when we argue about the user, with data, not about who is right. And it helps to offer a small, time-boxed next step instead of asking for a big re-plan."

---

## 2b. Disagree and commit — In-Experience Creation

> **Situation:** "Roblox was building In-Experience Creation: players create an avatar item inside a live game and wear it right away. Our normal UGC flow is async — moderation takes hours. The [PRODUCT TEAM NAME] product team, not my own PM, wanted it in seconds. To get there, they asked us to moderate the item and all its dependencies in parallel. One avatar item has many dependency items — [meshes, textures, accessories]."
>
> **Why I disagreed:** "My first answer was no. Today it's sequential, so we never worry about how one dependency's decision affects the others. In parallel, an item could go live before one of its dependencies is reviewed. And if we get one shared dependency wrong, that mistake spreads to every avatar item that uses it. That's a big moderation risk, and our infra was built for one-at-a-time, async work. So I said: let's keep the current flow."
>
> **The turn:** "Then I thought about it again. The product goal was right — nobody will wait hours inside a game. If I only push back, the feature just doesn't happen, or someone builds it without the safety part. As tech lead, my job is not to block. It's to close the gap. So I accepted their decision: parallel, seconds-level. Then I focused on how to do it without lowering the moderation bar."
>
> **What I built:** "I kept the reviews parallel, but added an orchestration layer on top. It tracks every dependency in the tree on its own, with its own status and retries, and the item becomes available **only when every dependency is approved**. Not when the first one passes — when the last one does. If any one is rejected, the whole item is rejected. [If a review times out, the item stays pending and goes back to the normal async path — it is never released early.]"
>
> **The trade-off I said out loud:** "Latency now depends on the slowest review, so p99 is driven by the longest tail. I told the product team that clearly. It's still seconds, not hours."
>
> **Result:** "Moderation went from hours to seconds — [p50 X s, p99 Y s]. We did not rewrite the infrastructure; we only added one orchestration layer that waits for the whole dependency tree. And we did not lower the moderation bar at all. [The same orchestration model was later reused for other real-time creation flows.]"
>
> **What I learned:** "Disagreeing is easy. As a lead, after I disagree, I owe the team an option. Now I commit to the goal first, then argue about *how* — and I don't give up on the bar I'm responsible for."

**Why it works:** you lost the main decision (parallel, seconds-level) and committed to it fully. What you didn't give up was the moderation bar, which is yours to own. The orchestration layer is how you kept both.

**Likely follow-ups:**
- *"So did you really commit, or did you just change their design?"* → "Their decision stood: parallel, seconds-level. I didn't bring back sequential. I added a gate that waits for everything, so parallel became safe."
- *"What if they had wanted to show the item before moderation finished?"* → "That's lowering the safety bar, not a trade-off on how we build it. I would escalate that, not commit to it."
- *"How did you handle the slow tail?"* → "[Timeouts per review, retries, and fall back to async if one stage is stuck.] Then we tracked p99 per dependency type to see which reviewer was slow."
- *"How did your team react?"* → "[Some engineers were worried about race conditions and partial failures.] Per-dependency status and one source of truth for the item answered that."

**One line:** "I disagreed with parallel moderation because of dependency risk, then committed to the seconds-level goal and built an orchestration layer that waits for every dependency — hours to seconds, no rewrite, same moderation bar."

---

**The pattern to show in any PM conflict:**
1. Understand their goal first (roadmap, date, metric).
2. Bring data, not opinion.
3. Offer options with costs, including a small time-boxed one.
4. Let the PM make the priority call — it's their call. Commit once it's made.
5. Close the loop: what changed in how you work together.

---

## 3. Cross-functional collaboration (short version)

> "Our moderation propagation kept causing a big incident: when one image was moderated, every avatar item using it got taken down too. Bad actors abused it. Safety wanted less risk, Moderation wanted fast enforcement, Dev Money cared about refunds, Marketplace cared about creators. Each team only saw its own part. I put the full cost in one place, met each org one by one, and we agreed on one definition of success: stay safe *and* protect creators and buyers. We kept propagation, because it's needed for safety, and built safe restoration and guardrails on top. My engineers built the four parts; I owned the design and the cross-org agreement."

---

## 4. Driving alignment (short version)

> "Creators could not update an item after publishing — only take it down and recreate it, which refunded every buyer. Nobody owned the problem. I put a number on it: on-call hours on my team, and support time on the Avatar Engine team. I took that to our PM and both engineering managers. The key was showing the other team their own cost. We agreed to move four of my engineers for a quarter, and they lent three. The hard part after that was aligning Safety on the policy — when a new version must be re-moderated, and how different it can be from the original. It launched and our CEO announced it at RDC."

---

## PM-round questions to prepare (one or two lines each)

| Question | Short answer |
| --- | --- |
| *How do you work with your PM day to day?* | Weekly sync on priorities; I review the PRD early and add cost and risk for each item; we decide trade-offs together, PM makes the priority call |
| *PM wants it by date X, engineering says it's not possible.* | Never just "no". Give options: smaller scope by X, full scope by Y, or X with a known risk. Make the cost of each clear and let the PM choose |
| *How do you say no?* | "No to this version, yes to the goal." Explain the cost in user or business terms, offer the cheapest path to the same goal |
| *Tech debt vs. features?* | Translate debt into PM language — incidents, on-call hours, slower future features. Ask for a fixed share of capacity, not a one-time fight |
| *A time the PM's requirement was wrong?* | LatencyRequirementFailure — but frame it as *both of us* missing it, not the PM being wrong |
| *Scope creep mid-project?* | Write down what changes and what it costs in time; agree on what drops out to make room |
| *How do you explain a technical trade-off?* | One sentence each: what the user gets, what it costs, what the risk is. No jargon |
| *How do you use data in decisions?* | Pick the metric that matches what users feel, not only what's easy to measure. Check it before launch, not after |
| *A time you influenced the roadmap?* | AvatarAssetVersioning — found an unowned cost, made the trade explicit, got it funded |

---

## Reddit angle

Reddit Safety has the same shape as your Roblox work: user-generated content, community moderators (like creators), and a constant pull between safety and community health. Use one sentence to connect, not more:

> "At Roblox, safety and creators often wanted different things. At Reddit it's safety and communities. I'm used to finding the option that keeps the safety bar and doesn't break the community."

---

## Questions to ask the PM

- "How do the PM and the tech lead split decisions on this team today?"
- "What's a recent disagreement between product and engineering here, and how did it get resolved?"
- "What does success look like for this team in the next year — and how is it measured?"
- "Where does safety work get traded off against growth features, and who decides?"
- "What would you want from the engineering lead in the first three months?"

---

## Avoid

- **Making the PM the villain.** Every conflict story should end with "we" solving it.
- **Too much architecture.** One technical sentence per story is enough for this round.
- **"I convinced them I was right."** Better: "we looked at the data together and the answer became clear."
- **Feedback with no real change.** End the feedback story on what you do differently now.
- **Answering "no" without options** in any date or scope question.
