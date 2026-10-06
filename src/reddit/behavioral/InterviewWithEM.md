# Reddit Interview Round with the Engineering Manager (Safety ML Serving)

**Focus areas for this round:** why this team and this role, how I lead and work with an EM, how I handle incidents and on-call, and whether I can step into ML serving without having run a model fleet before.

> **Check first.** I could not find public information about this team. What follows is what a Safety ML Serving team usually owns: online and batch inference for safety models, rollout safety, reliability, and cost. Before the call, replace the guesses with the recruiter's job description, and ask the recruiter two things: what the team owns, and what the EM said they want in this hire.

The stories already exist in [`waymo/behavioral`](../../waymo/behavioral/README.md). Use [`TechLeadFraming`](../../waymo/behavioral/TechLeadFraming.md) as the frame. The PM round is in [`InterviewWithPM.md`](./InterviewWithPM.md). Do not reuse the same story in both rounds if the interviewers compare notes.

> **All quoted parts are written to be spoken.** Short sentences. Read them out loud once.

---

## What an EM is really checking

An EM asks one thing: *"If I hire this person, does my team get better, and do I sleep better?"* That breaks down into:

1. **Will the team run well with you?** Do you grow people, split work sensibly, and keep on-call fair?
2. **Do you handle incidents calmly?** The EM carries the page too. They want to hear what you did in the first hour and what you changed afterward.
3. **Can you work with an EM and not around one?** They want a clear split of who decides what.
4. **Do you pick the right work?** Reliability, cost, and new models all compete for the same engineers.
5. **Are you honest about what you do not know?** ML serving is a specialty. Hiding the gap is worse than naming it.

**Rule for this round:** more about people, trade-offs, and ownership than about architecture. Go technical only when they ask, and stop after two or three sentences unless they keep digging.

---

## Opening (30 seconds)

> "I am the tech lead for Marketplace Safety at Roblox. I have 7 to 8 engineers, and I plan the roadmap with an engineering manager and a PM. The EM owns people and delivery. I own the technical direction and the design bar. A lot of my work is where safety decisions meet a live product: a wrong verdict has a real cost, and a slow one does too. That is why this team interests me. You sit at the point where a model's output becomes a decision on real content."

Then stop and let them ask.

---

## Be honest about the ML serving gap

Say this early, in your own words, before they find it:

> "My background is the moderation platform around the models, not the serving layer for them. I built where ML signals reach moderators, the validation stage that can overrule a decision, and the recovery path when a decision is wrong. I have not run GPU fleets or tuned batching. What I do bring is the other side of the contract: what a calling team needs from a model service, and what breaks when a verdict is wrong or late."

**Check before saying it:** this is only true for the parts you actually did. If you have done more with model hosting, change it.

**What to say you would do in the first 90 days:** learn the top model's traffic and latency budget, read the last three incidents, shadow on-call, then pick one reliability or rollout problem and fix it. Do not promise architecture changes before you have seen the system.

---

## Which story for which question (no repeats)

| Question type | Use | Why this one |
| --- | --- | --- |
| **Technical leadership, a big call** | [BlackImageIncident](../../waymo/behavioral/BlackImageIncidentCrossFunctional.md) | Kept the safety mechanism, built recovery and guardrails. Close to what a serving team does when a bad verdict causes a cascade |
| **Combining model signals with other checks** | [AppealValidation](../../waymo/behavioral/AppealValidationOwnership.md) | ML attributes shown to moderators, plus a final validation stage that can override. The ML-adjacent story |
| **Latency and correctness under load** | [InExperienceCreation](../../waymo/behavioral/InExperienceCreationOrchestration.md) | Sequential to parallel with per-stage state and retries. The in-team disagreement that changed my design |
| **A miss, and what you built after** | [LatencyRequirementFailure](../../waymo/behavioral/LatencyRequirementFailure.md) | Metric was green, users were unhappy. Strongest "I own the miss" story |
| **Getting work funded, trading away other work** | [AvatarAssetVersioning](../../waymo/behavioral/AvatarAssetVersioning.md) | The one real portfolio trade. Good for "how do you pick between reliability and new features" |
| **Feedback, changing how you work** | [MarketplaceRemediation, feedback variant](../../waymo/behavioral/MarketplaceModerationRemediation.md) | Used in the PM round. Prefer a different feedback example here if you have one |

---

## EM-round questions to prepare

| Question | Short answer |
| --- | --- |
| *How do you split work with your EM?* | EM owns people, performance, headcount, and delivery process. I own technical direction, design quality, and the technical bar. We disagree most on timing, so we talk before planning, not during it. Have one real example ready |
| *How do you decide who works on what?* | Match how unclear the work is to the engineer's level. Clear work to mid-level engineers. Unclear or cross-org work to seniors or me. Once a quarter, give one person a stretch task and stay behind them. Rotate on-call and maintenance so it does not always land on the same person |
| *How do you grow engineers?* | Pick a person, give them work slightly above their level, review the design early, then step back. Say one name and what they own now |
| *Tell me about a disagreement with someone on your team.* | InExperienceCreation. Say it as an in-team disagreement: two engineers pushed back on parallelizing because of partial failure, they were right, and the design changed |
| *An incident you led.* | BlackImageIncident. What I did in the first hour, how I split recovery, and what we built so the same cascade does not happen again |
| *How do you feel about on-call?* | It is where the truth about the system shows up. Fair rotation, a runbook that is current, and every page either gets a fix or gets deleted. I count pages per week as a team health number |
| *Reliability or new features?* | Put the cost of reliability in the EM's and PM's terms: incidents, on-call hours, delayed launches. Ask for a fixed share of capacity each quarter, not a one-time fight |
| *A time you were wrong.* | LatencyRequirementFailure. Own it plainly, then what you built so it does not repeat |
| *How do you handle an underperforming engineer?* | Early, specific, and private. Say what the gap is with an example, agree on what good looks like, and check in weekly. If it is a performance process, that is the EM's call and I give them the technical evidence |
| *Why leave your current team?* | Say what you are moving toward, not what you are leaving. "I want to work closer to the models and the serving path, where my moderation and recovery experience applies directly" |
| *Why Reddit, why safety?* | Same shape as my Roblox work: user content, volunteer moderators instead of creators, and constant tension between safety and community health. I know what a wrong verdict costs |

---

## Technical topics to be ready for (two or three sentences each)

The EM may probe a little to confirm you speak the language. They are not usually looking for an architecture exam.

- **Fail open or fail closed?** It depends on the surface and the harm. High-severity categories fail closed or fall back to a human queue. Low-severity ones can fail open with a flag for re-scoring later. I would want that set per surface and written down, not decided at 3 a.m.
- **Rolling out a new model.** Shadow first, then a small canary, compare against the old model on the same traffic, and have a one-step rollback. Watch score distribution, not only latency and errors.
- **Latency vs. accuracy.** A bigger model is slower and costs more. Use a cheap model first and send only uncertain cases to the larger one, if the product allows it.
- **Batch vs. online.** Online for decisions on the request path. Batch for re-scoring after a model or policy change and for backfills. Keep them from fighting for the same GPUs.
- **Drift and quality in production.** Labels arrive late. Watch input and score distributions as an early signal, and keep a labeled sample to measure precision and recall after the fact.
- **Wrong verdicts at scale.** A bad model version can mis-enforce a lot of content fast. That is the Black Image lesson: limit the blast radius, and have a fast, safe way to undo.

If you do not know a detail, say so and say how you would find out. That is better than guessing.

---

## Questions to ask the EM

Pick four or five. Do not read them off a list.

**The system**
- "What are the latency budget and traffic shape for the main online models, and which one is closest to its limit today?"
- "When a model call fails or times out, who decides fail open versus fail closed, and is it set per surface?"
- "How do you roll out a new model version today? Shadow, canary, then cutover?"

**The team**
- "What takes most of the team's time right now: onboarding new models, cost, on-call, or platform work?"
- "What was the hardest incident recently, and what changed because of it?"
- "How is on-call set up, and how many pages a week does the team get?"

**Working with ML and policy**
- "How is ownership split between the ML engineers who own model quality and this team? Who owns a precision drop in production?"
- "When a policy changes, what has to happen before serving reflects it, and how long does that take?"

**The role**
- "What would success look like for this role at six and twelve months?"
- "What kind of engineer has done best on this team, and what has made someone struggle?"
- "What is the one thing you would change about the team if you had a quarter with no other constraints?"

---

## Reddit angle

> "At Roblox, safety and creators often wanted different things. At Reddit it is safety and communities. I am used to finding the option that keeps the safety bar and does not break the community."

---

## Avoid

- **Pretending to have run a model fleet.** Name the gap, then show what you bring.
- **Making your current EM or PM the villain.** Every conflict story should end with "we".
- **Too much architecture.** Two or three sentences, then stop.
- **Claiming the tech lead title with no evidence.** Say who built what under your direction, and what you chose not to do.
- **Questions you could have read on the careers page.** Ask about incidents, trade-offs, and decisions.
