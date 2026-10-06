# Explain How You Use AI in Engineering Work

Source: [PracHub — Reddit, Software Engineer, Technical Screen](https://prachub.com/interview-questions/explain-how-you-use-ai-in-engineering-work)

**The prompt:** *"How do you use AI-assisted tools in your day-to-day engineering work? Give one concrete example: what you delegated, how you verified it, what you deliberately withheld, and whether it improved the outcome."*

They also want: one case where you **rejected or heavily changed** the output, and a clear line between "AI drafted it" and "I decided it".

> **All quoted parts are written to be spoken.** Short sentences. **[BRACKETS] = fill in with your real case.** Don't invent numbers — use real ones or say "roughly".

---

## What the interviewer is really checking

1. **Judgment, not enthusiasm.** Not "AI is amazing", not "I don't trust it". A measured middle.
2. **Ownership.** The tool drafts. You decide, you review, you're on the hook if it breaks.
3. **Input boundaries.** You know what must never go into an external model.
4. **Verification scaled to risk.** A throwaway script gets a quick check. Production code on a money or safety path gets an independent test.
5. **Honesty about failure.** You've seen it be wrong, and you caught it.

---

## Opening (30 seconds) — "How do you use AI day to day?"

> "I use it as a fast first draft, not as a decision maker. Three main uses: reading unfamiliar code — 'where is this flag set, what calls this'; drafting boilerplate and tests; and a first pass on a bug, where I ask it for hypotheses, then I check them myself. I don't use it for the decisions I'm accountable for — the design trade-off, the policy, what we ship. And I never paste anything that's confidential, user data, or credentials. It's made me faster on the boring parts, so I spend more time on the parts that need judgment."

---

## The concrete example

Pick a **bounded** task with a **clear way to check the result**. The best shape: *AI wrote it fast, I verified it with something AI did not write.*

> **Task + why AI fit:** "We had to [one-off backfill / migration / bulk fix — e.g. re-process N items after a moderation rule change]. The logic was simple, but there were many edge cases and a lot of boilerplate — paging, retries, logging. Good fit for a first draft."
>
> **What I delegated:** "I described the schema shape and the rules in my own words, and asked for the script plus unit tests for the edge cases I listed."
>
> **What I withheld:** "No real data, no user IDs, no internal service names or credentials. I used a made-up schema with the same shape. Internal API details stayed out — I wired those in myself."
>
> **How I verified:** "I didn't trust its tests alone — it wrote the code *and* the tests, so they can share the same mistake. I wrote a small independent check: run the old path and the new script on [a sample / a staging copy] and compare outputs row by row. Then a dry-run mode in staging, then a small batch in prod with a kill switch."
>
> **What I rejected / changed:** "The draft [read a value, then wrote it back in a second step — a race if two workers ran at once / paged with OFFSET, which skips rows while data changes / swallowed errors and kept going]. The tests passed, because they only had one worker and static data. I replaced it with [an atomic conditional update / keyset paging / fail-fast with a retry queue]."
>
> **Outcome:** "Roughly [X hours instead of Y days]. The bigger win: I spent my time on the dangerous edge cases instead of boilerplate. But the bug it introduced would have been real damage, which is exactly why the independent check matters."

**Why it works:** bounded task, clear input boundary, verification that doesn't depend on the tool, a real catch, a measured result.

---

## Follow-up questions (from PracHub)

**1. "What work would you never send to an external model?"**
> "User data and PII. Credentials and secrets. Unreleased product or security details — for a Trust & Safety team, that includes how our detection works, because leaking it helps abusers. And anything the company's policy says stays internal — I follow our approved-tools list, not my own judgment, on that."

**2. "How do you review AI code differently from a teammate's code?"**
> "A teammate's code comes with intent — I can ask why. AI code looks confident even when it's wrong, and it has no memory of our system. So I review it like code from a smart new hire on day one: check every API it calls actually exists and does what it thinks; look for the classic gaps — concurrency, retries, error handling, edge cases at boundaries; and check it fits our patterns instead of inventing a new one. And I don't let it write both the code and the only test."

**3. "How would you tell AI is making the team faster but the codebase worse?"**
> "Watch quality, not just speed. Signals: more code per PR but more reverts and follow-up fixes; bugs found later — in staging or prod — instead of in review; duplicated helpers because each prompt reinvents one; review time going up or reviews getting shallower; and on-call pages from code nobody on the team can explain. If PR count goes up but change-failure rate also goes up, we're just moving work to later."

**4. "What should a team record about generated changes?"**
> "The same as any change — why it exists, what was tested, what the risks are. The extra bit: say in the PR when a large part was generated and what was verified independently, so a reviewer knows where to look harder. Not a label for blame — a signal for review depth. And the PR author still owns it."

---

## Licensing / security points (have one line ready)

- **Licensing:** "Large verbatim-looking blocks get checked — if it looks like it came from a specific library, I use the library instead of pasted code."
- **Security:** "Generated code that touches auth, input parsing or SQL gets the same security review as anything else — no shortcut because a tool wrote it."

---

## Things to avoid

- Naming tools as the answer ("I use Cursor and Copilot") — say *how* you use them.
- "I review everything carefully" with no example of a catch.
- Overclaiming impact ("10x faster"). Use a real, modest number.
- Revealing confidential details in the example itself. Keep it generic enough to say out loud.
- Saying AI made the decision. You made it.
