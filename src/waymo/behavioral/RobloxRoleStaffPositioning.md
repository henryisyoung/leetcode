# My Role at Roblox — and Why It Is a Staff-Level Role

The scope frame from [TechLeadFraming](./TechLeadFraming.md), written as **my actual role**, then matched against what Staff level is judged on. Use this for "tell me about your role", "walk me through your scope", and "why do you think you are Staff level".

> **Fill these in before the interview.** Everything in `[BRACKETS]` is something I do not know. Do not guess a number in the room. If you cannot say a real number, just remove that sentence.

> **All the quoted parts are written to be spoken.** Short sentences, simple words, one idea per sentence. Read them out loud once or twice. If a sentence is hard to say, cut it.

---

## 1. The role, in one paragraph (memorize this)

> "I am the tech lead for **[TEAM / DOMAIN]** at Roblox. This is Marketplace Safety and the avatar moderation platform. Our team has three leads: an engineering manager, a PM, and me. The EM owns the people and the delivery. The PM owns what we build and why. I own the technical direction and the technical quality. Under that we have **7 to 8 engineers**. I review and approve the designs and the important technical decisions. I also decide how we split the work across the team.
>
> This is more than a normal team lead role, because almost none of our work stays inside our team. Moderation connects four orgs: Marketplace, Safety, Moderation Operations, and Dev Money. Each org wants a different thing. Marketplace wants growth. Safety wants less risk. Moderation Operations wants fast enforcement. Dev Money cares about refunds.
>
> So most of my job is not designing the system. My job is to choose which two or three things are worth our quarter. Then I make these four orgs agree on the same definition of done. And I make sure we build something that we do not have to rebuild next year."

**The last three sentences are the Staff claim.** A senior engineer solves the problem in front of him very well. A Staff engineer chooses which problem, makes the org agree, and builds something that lasts.

---

## 2. Shorter versions

**15 seconds** (when they only want context and will move on):

> "I am the tech lead for Marketplace Safety at Roblox. I have 7 to 8 engineers, and I work with an engineering manager and a PM. I own the technical direction for the moderation platform. This platform touches four orgs, so a big part of my job is cross-org alignment, and deciding what we will not build."

**If they ask "are you a manager?"** Answer clearly. If this sounds unclear, they will not believe the rest.

> "No. The EM owns the people and the performance reviews. I own the technical direction, the design approval, and how we split the work. I am the most senior engineer on the team and I make the technical decisions. But people report to the EM, not to me."

---

## 3. Staff criteria, and my evidence for each

These are the six things Staff level is usually judged on. Choose the story by which one the question is testing, not by the topic.

| What Staff is judged on | My evidence | Story |
|---|---|---|
| **Scope is bigger than my own team** | Made Safety, Marketplace, Moderation Ops, and Dev Money agree on one definition of success for moderation propagation | [BlackImageIncident](./BlackImageIncidentCrossFunctional.md) |
| **Works with unclear problems — finds the problem, is not given it** | Nobody asked me to fix the appeal validation gap. I found it, scoped it, got it on the roadmap, and shipped it with three teams | [AppealValidation](./AppealValidationOwnership.md) |
| **Decides what the team does — and what it stops doing** | Put a number on a cost nobody owned, then traded four of my engineers off other work and got three more from another org to pay for it | [AvatarAssetVersioning](./AvatarAssetVersioning.md) |
| **Multiplier — impact through other people and through mechanisms** | Launch readiness framework for moderation launches. Product, Safety, Mod Ops, Data, and Engineering all use it now | [LatencyRequirementFailure](./LatencyRequirementFailure.md) |
| **Technical direction that lives longer than the project** | The orchestration model for parallel dependency graphs became the base for later real-time creation features | [InExperienceCreation](./InExperienceCreationOrchestration.md) |
| **Business judgment — refuses false choices, connects tech to outcome** | Changed "take down or leave up" into a remediation workflow. Kept the safety bar, reduced creator and refund impact | [MarketplaceRemediation](./MarketplaceModerationRemediation.md) |
| **Owns the outcome, not only the deliverable** | We met the latency spec but users were still unhappy. I did not hide behind "we met the requirement". I changed the metric | [LatencyRequirementFailure](./LatencyRequirementFailure.md) |

**My best Staff story is `LatencyRequirementFailure`.** It is the only one that covers three things at the same time: owning the outcome after the spec was met, admitting a real miss, and turning one failure into a mechanism that other teams use. Start with this one when the question is open, like "tell me about your leadership". It feels wrong to start with a failure story, but for a level question it is the right choice.

---

## 4. Where my evidence is weak now

Practice these before the interview, not during it. Four gaps, in the order they will probably be asked:

**Growing engineers.** I cover every axis above except this one. In all five stories, no engineer on my team is ever stretched, unblocked, or grown. Staff interviews ask this directly. I need one story, even a small one, where I gave someone work above their level, stayed behind them as support, and they grew. This probably happened. It is just not written down.

**Saying no.** ~~No story mentions anything I deprioritized.~~ **Now covered by [AvatarAssetVersioning](./AvatarAssetVersioning.md)** — I moved four engineers off other projects for a quarter to fund it. One thing still missing: I need to be able to **name the initiative I dropped**. "We traded against other work" is vague; "we pushed back [X] by a quarter" is a real answer, and they will ask.

**Numbers.** Most results are still directional: "significantly reduced", "much lower latency". At Staff level at least two should be real numbers. They do not need to be exact — a range sounds more honest than a suspiciously precise figure. `AvatarAssetVersioning` has the best ones (assets unblocked, squad size, refunds avoided), but **the refund figure needs checking before I say it** — see the verify table at the bottom of that file. Still to recover elsewhere: false positive appeal approval rate before and after, unintended refund volume during propagation incidents, latency before and after for in-experience creation, and how many teams adopted the launch framework.

**Disagreement inside my own team.** Every conflict in my five stories is with another org. Interviewers read this as someone who influences peers but maybe does not really run a team. `InExperienceCreation` already has the material. My engineers pushed back on parallelizing moderation because of race conditions and partial failures, and they were right. If I write it as an in-team disagreement that changed my design, this gap is closed.

---

## 5. "Why do you think you are Staff and not Senior?"

Answer the real difference, not the title. Say it like this:

> "For me the honest difference is not technical depth. Some senior engineers on my team go deeper than me in their own area. The difference is three things.
>
> First, the size of the decision. A senior engineer on my team owns whether the validation stage is correct and fast. I own a different question: should we build it at all this quarter, instead of the other three things that need the same people.
>
> Second, whose agreement I need. Most of my hard problems are not solved in code. They are solved by making Safety and Marketplace agree that success means both low risk and creator trust. Nobody does that unless one person makes it their job.
>
> Third, and this is the one I care about most. I try to make the fix last longer than the incident. We once shipped a feature that met its latency target, and users still did not like it. Fixing that one feature was not hard. The part I am proud of is the launch readiness framework that came after. Other teams run it now before their own launches. That is the change from solving one problem to changing how several teams work."

**Why this works:** it admits something real (depth is not the difference), it is specific, and it ends with a mechanism instead of a project. Listing hard projects is what a senior engineer does.

---

## 6. Questions that test this claim

| Question | Where to go |
|---|---|
| "What is the split between you and the EM?" | §2 — they are checking if the three-lead setup is real |
| "How do you decide who works on what?" | Need a method plus one example — see [TechLeadFraming §4](./TechLeadFraming.md) |
| "How did your idea become a project, and how did other teams adopt it?" | LatencyRequirementFailure, plus the five steps in [TechLeadFraming §4](./TechLeadFraming.md). This is the multiplier axis |
| "Tell me about a disagreement with an engineer on your team" | InExperienceCreation, rewritten as in-team. This is my weakest point now |
| "What did you decide **not** to do?" | Gap 2 above. Prepare one |
| "How did you grow someone?" | Gap 1 above. Prepare one |
| "What would you do differently?" | LatencyRequirementFailure already answers this honestly |
| "What is the most technically complex thing you did?" | InExperienceCreation (parallel moderation without losing correctness) or AppealValidation (combining imperfect signals) |

---

## 7. One note on delivery

In these documents I always start with the system and reach the leadership part late. The Black Image story spends three sections on moderation propagation before it says what I actually did. In the interview I should do the opposite. **Say the decision first. Then add only enough system detail to make the decision believable.** The interviewer is scoring my judgment. If they want more depth, they will ask.
