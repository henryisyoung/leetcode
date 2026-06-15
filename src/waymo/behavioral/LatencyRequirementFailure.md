# Latency Requirement Failure: From Metric Miss to Launch Framework

**Behavioral story (STAR)** — themes: learn from failure, staff-level product judgment, customer focus, operating mechanism, cross-functional leadership.

> **Maps to questions like:** "Tell me about a failure," "a time requirements were wrong," "a time engineering succeeded but users were unhappy," "what did you learn from customer feedback?"

---

## Situation

At Roblox I led a user-facing feature where **moderation SLA latency** was critical. The PM and I agreed on a target, and the team built a reliable system that met it.

From an engineering standpoint the launch looked successful, but users still complained that the experience felt **slow and disruptive**.

## Failure

The failure was not that engineering missed the requirement. The failure was that the requirement itself had not been validated against the real user experience.

I had focused on:

> "Can we hit this latency target?"

But the better TL question was:

> "Does this target actually represent what users experience as fast enough?"

We validated the system metric, but assumed it would translate to perceived speed. That assumption was wrong.

## Action

I partnered with the PM to understand the gap. We reviewed complaint patterns, talked to users, and studied how the feature interrupted their workflow.

We learned that users perceived delay differently than our metric captured. Even though measured latency was within target, the wait happened at the wrong moment in the flow, so it felt disruptive.

I helped redefine the latency goal around **user-visible interruption**, redesigned parts of the system, and prioritized changes that reduced the moments where users felt blocked.

## Result

The updated experience reduced complaints and better matched user expectations.

The bigger result was what I did afterward. I did not want this to stay as a one-off lesson, so I designed a **standard launch-readiness framework for moderation-related product launches** and drove adoption across Product, Safety, Moderation Ops, Data, and Engineering.

The framework made teams answer a few concrete questions before broad rollout:

- Which system metrics are only indirect signals, and which metrics reflect actual user experience?
- What moderation paths, review queues, appeals, or abuse cases could this launch trigger?
- If the first result is neutral or negative, what is the iteration path without a full rewrite?

As TL, I brought this into design and launch reviews, aligned stakeholders on the guardrails, and pushed engineering teams to include observability and fallback paths in the initial design.

## Lesson

> "The biggest lesson was that successful implementation of a requirement does not always mean solving the user problem. A senior TL needs to validate the chain from system metric to user perception to product outcome, and if that chain is weak, challenge the requirement before the team over-invests."

## Leadership Signal

- **Ownership beyond execution:** I did not hide behind "we met the requirement"; I owned the end-to-end outcome.
- **Staff-level learning:** I converted one failure into a reusable launch mechanism for future moderation-sensitive launches.
- **Cross-functional influence:** I onboarded Product, Safety, Moderation Ops, Data, and Engineering into a more rigorous launch flow.
- **Product judgment:** I learned to test whether technical metrics are real signals of user value.
- **Waymo bridge:** same pattern applies to autonomy: a system metric can be green while the human experience is still poor, so success criteria must be validated against real-world perception and safety outcomes.
