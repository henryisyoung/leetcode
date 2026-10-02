# Tech Lead Framing: How to Not Sound Like a Senior IC

Read this before the other stories. It is not a story. It is the frame that every story sits inside.

> **The problem this fixes:** in my five stories, almost every verb is *"I designed / I drove / I built"*. The only other people in them are **other orgs**. My own 7 to 8 engineers never appear. That sounds like a strong senior IC with good cross-team influence. A tech lead is judged differently: **what did the people you direct build, and what did you choose not to do.**

> **All quoted parts are written to be spoken.** Short sentences, simple words. Read them out loud. If a sentence is hard to say, cut it.

---

## 1. The scope frame (30 seconds, say it once, early)

Use this the first time they ask about my role, or at the start of my first behavioral answer. After that, never repeat the whole thing.

> "I am the tech lead for [TEAM / DOMAIN] at Roblox. Our team has three leads: me as tech lead, an engineering manager, and a PM. The three of us plan the roadmap together. I have **7 to 8 engineers**. I own the technical direction. I review and approve the designs and the important technical decisions. And I decide how we split the work across the team. The EM owns the people and the delivery. The PM owns what we build and why. I own how we build it and the technical quality.
>
> Our team matters a lot in the org because most of our work is cross-org. [MARKETPLACE SAFETY / MODERATION] touches Marketplace, Safety, Moderation Operations, and Dev Money. So almost nothing we do stays inside one team."

**Fill in before the interview:**
- `[TEAM / DOMAIN]` — one short phrase, not a sentence.
- One real number for why the team matters: revenue it touches, users it touches, or the kind of incident it owns. If there is no real number, leave it out.

---

## 2. The opener for each story (2 sentences, before every STAR)

Every story should sound like **one initiative out of several I was running**, not like the only thing I did. This is the sentence that separates tech lead from IC:

> "At that time my team had [three or four] initiatives running. I took this one myself, because [REASON]. The others I scoped and gave to [a senior engineer / two engineers], and I reviewed their designs at checkpoints."

**Good reasons to take it myself** (pick the true one):
- The problem was the most unclear one. We could not know the design at the start.
- It needed cross-org alignment, and only the tech lead could do that.
- It was the highest risk item, and failure would be expensive.
- It was new architecture, and it would become a pattern the team reuses.

**Bad reason**, and interviewers listen for it: *"because I am the strongest engineer."* That sounds like someone who keeps all the interesting work and does not grow his team.

---

## 3. Word swaps

Same facts, different verbs. This is most of the change.

| Sounds like senior IC | Sounds like tech lead |
|---|---|
| "I designed the validation framework." | "I decided the architecture and the interfaces. Then I split the work across three engineers: ML attributes, the validation stage, and the override path. I reviewed each design before they started building." |
| "I built automated remediation paths." | "I made the call to invest in automated remediation instead of more manual runbooks. Then I put two engineers on it for a quarter." |
| "I worked across moderation, ML, and platform teams." | "I owned the technical contract between my team and ML and platform. My engineers owned our side of each integration." |
| "I saw a gap and took it on." | "I put it on our roadmap for the next quarter. I traded it against [OTHER INITIATIVE], and I got the PM and the EM to agree on that trade." |
| "The team raised concerns about race conditions." | "Two of my engineers pushed back on the parallel design. Their concern about partial failure state was correct, and it changed the design." |
| "I drove it end to end." | "I drove the design and the cross-org alignment. Three engineers built it over two quarters, and I ran the design reviews." |

**The rule:** if a sentence describes work one person could do alone, it is an IC sentence. Fix it by adding either *(a)* who else did it under my direction, or *(b)* what I chose **not** to do in order to do this.

---

## 4. The three questions that expose a fake tech lead

Prepare these now. This is the most common way the claim gets tested.

**"How do you decide who works on what?"**

Give a real method, not a feeling. For example: I match how unclear the work is to the level of the engineer. Clear, well-defined work goes to mid-level engineers so they can move fast. Unclear or cross-org work goes to seniors or to me. Each quarter I also pick one person and give them something above their level, and I stay behind them as support. And I rotate the on-call and maintenance load so it does not always land on the same person. Then give **one concrete example**.

**"Tell me about a disagreement with an engineer on your team."**

My current stories cannot answer this at all. Every disagreement in them is with another *org*. I need one that happened **inside my team**, where either (a) I overruled someone and owned the result, or (b) they changed my mind. Usually (b) is the stronger answer.

> The material is already there. In **InExperienceCreation**, my engineers pushed back on parallelizing moderation because of race conditions and partial failures. Right now it is written as "the team raised valid concerns". If I rewrite it as a real disagreement with my own engineers that **changed my design**, this question is covered.

**"You had an idea. How did it become a real project, and how did other teams start using it?"**

This is the question `LatencyRequirementFailure` sets up and then does not answer. That story says I "drove adoption across Product, Safety, Moderation Ops, Data, and Engineering", but it never says how. A Staff interviewer will ask. The five steps below are the answer.

*Step 1 — find the cost, not the idea.* An idea does not get prioritized because it is good. It gets prioritized because someone is already paying a cost and I gave that cost a name and a number. Not "moderation propagation is fragile". Instead: "last quarter this incident type cost us [N] on-call hours and [M] in unintended refunds". A rough number is fine. A number I cannot defend is not.

*Step 2 — find the sponsor, and talk to people before the meeting.* The sponsor is the person whose metric is hurt by that cost. Usually this is **not** my own PM. For Black Image it was Dev Money (refunds) and Marketplace (bookings). Talk to each person one-on-one first. A group meeting should never be the first time someone hears the idea. New ideas introduced cold in a big room get debated until they die.

*Step 3 — get it on the roadmap by naming the trade.* The PM owns priority, but the PM can only prioritize what the PM can see. Reliability and platform work is invisible until I translate it into PM language: user impact, revenue, risk. And I must say what we drop. "I need two engineers" gets ignored. "I want to move two engineers off [X] for one quarter, and here is why that trade is worth it" gets a decision. The EM is my ally here, because reliability work helps delivery predictability.

*Step 4 — design for adoption from day one.* Adoption is a design constraint, not a later phase. If I build it first and look for users after, it is already dead. What gets adopted: it solves the other team's problem, not mine. Integration is close to zero cost, like one config line or on by default. The other team does not have to change their process. And I ship with **one** friendly team already using it, so they are my reference. Five teams at the same time means five teams each find the rough edges alone and all decide it is not ready.

*Step 5 — attach it to a gate that already exists.* This is the strongest step and it is the one I actually did. Telling people it exists almost never works. Making it easier than the alternative is the biggest lever. But the thing that makes it last is putting it inside a review that people already attend. I brought the launch readiness framework into design and launch reviews. That is why it survived. A framework in a document slowly dies. A framework that is a step in a meeting people already join becomes the default. Mandating it from leadership is the last option, because people will find a way around it.

*Last: make it live without me.* Name an owner who is not me. Have a metric that shows real usage, not just sign-up. If it only works because I chase people, it dies when I change teams. Interviewers do ask "what happened to it after you left the team".

> **The 60-second version:** the cost that already existed and who paid it → the trade I made to get people → the one pilot team → attaching it to the existing review gate → the adoption number.
>
> The weak answer spends all 60 seconds on what the framework contained. They are not asking what I built. They are asking how I made other people change their behavior.

Two more to prepare one sentence for: *"How do you grow the engineers on your team?"* and *"What is the split between you and the EM?"* They ask the second one to check if the three-lead setup is real. Answer: the EM owns people, performance, headcount, and process. I own technical direction, design quality, and the technical bar. The PM owns priority. The interesting part is what happens when we disagree, so have one example ready.

---

## 5. Things to avoid

- **Too much implementation detail.** Depth is good, but if most of the answer is how the system works, then I answered as the implementer. A rough target: 20% context, 20% the decision and the options I rejected, 20% how I aligned people, 20% who built it, 20% result and what I would change.
- **No portfolio view.** A tech lead always has more work than people. If no story mentions something I deprioritized, it sounds like I only ever had one thing to do.
- **Everything succeeded.** `LatencyRequirementFailure` is my strongest tech lead story *because* it has a real miss and then a mechanism. Do not clean up the rough parts of the other stories.
- **"We" with no clear meaning.** The interviewer cannot give me credit for an unclear "we". Say who: "my two engineers", "the PM and I", "the three orgs".
- **Claiming the title without evidence.** Say "as the tech lead" only once per story. After that, show it: how I split work, what I traded away, and what other people did.

---

## 6. What each story needs

Details are inside each file, under **Scope** and **Why I drove this one**.

| Story | Already good for tech lead | Still needs |
|---|---|---|
| [AvatarAssetVersioning](./AvatarAssetVersioning.md) | **Written to this framework already.** The only story with a real portfolio trade and cross-org resourcing. Best answer for impact and prioritization | Name the initiative I dropped. Verify the refund number before saying it |
| [BlackImageIncident](./BlackImageIncidentCrossFunctional.md) | **Rewritten to this framework.** Four-org alignment. Redefining success. The "don't weaken propagation" decision | Fill the placeholders: which engineers owned the four workstreams, what I deprioritized, before/after numbers |
| [AppealValidation](./AppealValidationOwnership.md) | Took an unassigned problem | Reads like a solo design. Split the three parts across engineers. Add the roadmap trade that got it staffed |
| [InExperienceCreation](./InExperienceCreationOrchestration.md) | Disagree and commit. The in-team pushback is already there | Name it as an **in-team** disagreement that changed my design. This is my best answer for that question |
| [MarketplaceRemediation](./MarketplaceModerationRemediation.md) | Reframing. The feedback variant is good | Almost no execution. Who built remediation? How long, how many people? |
| [LatencyRequirementFailure](./LatencyRequirementFailure.md) | **My strongest tech lead story.** Owns a miss, then builds a mechanism other teams use | Start with this one for open leadership questions. Add how I got other teams to adopt the framework without authority |

---

## 7. Housekeeping

`README.md` lists **NeutralOutcomeProjectLearning.md**, but that file does not exist in this folder. Either write it, or remove the two rows that point to it. It is bad to find this gap during an interview. It is also the slot for *"a project that failed"*, which is a very common question. Right now only `LatencyRequirementFailure` covers it.
