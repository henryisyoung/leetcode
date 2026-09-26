# Avatar Asset Versioning: Letting Creators Update Published Items

**Behavioral story (STAR)** — themes: finding an unassigned problem, getting an initiative funded, trading away other work, cross-org resourcing, designing for another team's adoption, long-term technical direction.

> **Maps to questions like:** "Tell me about your biggest impact," "a problem nobody asked you to solve," "how did you get an initiative prioritized," "a time you had to give something up to do something else," "how did you get another team to adopt your work," "a decision with long-term consequences."

> **This is my strongest story for scope and ownership.** Use it when the question is about impact, prioritization, or level. Use [LatencyRequirementFailure](./LatencyRequirementFailure.md) when the question is about failure or learning. Those two cover most Staff-level questions between them.

---

## Scope (say this first, then stop)

> "I am the tech lead for Marketplace Safety at Roblox. I have 7 to 8 engineers, and I work with an engineering manager and a PM. At that time my team had [three or four] initiatives running. This one I took myself, because the design was not clear at the start, and because it needed another org to give up engineers. Only the tech lead could do that part."

---

## Situation

At Roblox, once a creator publishes an avatar item, they cannot change it. If the item has a problem, the only option is to take it down and create a new one.

That is expensive for everyone. Taking it down reverses and refunds the sales that already happened. The asset ID disappears from the platform. So a popular legacy item that only needs a small fix cannot be maintained at all — the creator has to kill it and start again, and every buyer loses what they bought.

Nobody owned this. It showed up as a steady stream of one-off pain: my operations and on-call engineers spent **[N] on-call hours per quarter** doing ad hoc manual fixes, and the Avatar Engine team spent their time on back-and-forth communication with external clients, walking them through workarounds. We were spending real money on it every quarter and the creators were still unhappy at the end. That is the worst kind of cost — high, recurring, and it buys you nothing.

## Task

I found this gap, so as the tech lead I decided whether it was worth a quarter of my team.

Two things made me say yes. First, the cost was not only ours. The Avatar Engine team was paying for it too, in a different currency — client communication instead of on-call hours. Second, a fix here was not a patch. It was **[500,000] legacy assets** that would become maintainable, and **[$X in refunds](#numbers-to-verify)** that would stop happening.

But I could not do it with the people I had. I needed **four engineers pulled off other projects on my team**, and **three engineers from the Avatar Engine team** — a squad of seven for one quarter.

So I built the case and took it to the PM and to both engineering managers. I listed the full effort, what it would cost us, what we would drop to pay for it, and what it would save. **The part that made it land was not the savings. It was showing the other team their own cost in their own terms** — this was not me asking them for a favor, it was me removing work they already hated doing. Once they saw that, giving me three engineers was an easy decision for them.

The technical task was then: build a self-serve versioning service that solves this for the next several years, not one that we rebuild in 18 months.

## Action

As the tech lead, I had to design for three things at once, and only the first one was mine.

**Our use case** — let a creator publish a new version of an existing item, with the asset ID and its purchase history intact.

**The dependency team's roadmap** — the service had to fit where Avatar Engine was going, not only where they were. If I designed only for today's integration, they would adopt it once and then diverge from it. I worked through their future roadmap with them before locking the interface.

**Safety** — this was the hardest part and the reason the design was not obvious. Changing an already-published item that people have already bought is a large risk surface. A creator could publish something clean, get it approved, and then swap in something harmful afterward. So the real work was not the versioning mechanics, it was the policy: what moderation decision applies to a new version, what happens to the old version and to the people who bought it, what the consequence is when a new version fails review, and how much a new version is allowed to differ from the original at all before it stops being an update and becomes a different item.

That last one — similarity gating — was where most of the meetings went. We had to agree with Safety and Moderation Operations on where the line sits, because there is no correct answer to it, only an agreed one.

## Result

- Removed a large recurring manual cost for both teams: fewer on-call hours on ad hoc remediation for my team, and much less client back-and-forth for Avatar Engine.
- **[500,000] legacy assets** became maintainable instead of stranded.
- **[$X]** in refunds avoided from this failure mode.
- The Avatar Engine team adopted it into their own flow, because it was built against their roadmap rather than bolted onto it.
- **Our CEO announced it at RDC**, Roblox's developer conference. It is the first time in roughly ten years that creators can update content they have already published.

---

## Why I drove this one myself (say this)

> "I did not take it because it was the most interesting engineering. I took it because two parts of it could not be delegated. The design was unclear at the start — we did not know what the safety policy should be until we argued it out. And it needed another org to give up three engineers, which is a conversation only the tech lead can have. The parts that were clear, I scoped and gave to my engineers."

## The one-line summary (say this)

> "The hard part was not building versioning. The hard part was that we were quietly paying for this every quarter and nobody owned it, so nobody fixed it. I put a number on the cost, showed the other team their share of it, and traded four of my own engineers off other work to pay for it."

## How the adoption actually happened

This story is the clean example of the five steps in [TechLeadFraming §4](./TechLeadFraming.md). Walk it in this order if they ask how you got it done:

1. **Found the cost, not the idea.** On-call hours plus client communication time, both recurring, both buying nothing.
2. **Found the sponsor.** The Avatar Engine team was paying for it too. They became the ally, not the obstacle.
3. **Named the trade.** Four engineers off other projects for one quarter. I did not ask for extra headcount — I said what we would stop doing.
4. **Designed for their adoption from day one.** Built against their future roadmap, so adopting it was cheaper for them than keeping their workaround.
5. **It stuck because it became the default path,** not an optional tool — self-serve versioning replaced the takedown-and-recreate flow rather than sitting beside it.

## Talking points / likely follow-ups

- **"Why had nobody fixed this before?"** Because the cost was spread across two teams as background noise — a few on-call hours here, some client emails there. No single person ever saw the total. Naming the total is what changed it.
- **"How did you decide it was worth four engineers?"** Compared recurring quarterly cost against one quarter of build. Recurring cost wins that argument quickly, and it keeps winning every quarter after.
- **"What did you drop?"** `[NAME THE INITIATIVE YOU DEPRIORITIZED]` — prepare this. They will ask, and "we absorbed it" is a weak answer.
- **"What is the abuse risk?"** A creator publishing a clean version, getting approval, then swapping in something harmful. Handled by re-moderating every version, similarity gating so an update cannot become a different item, and defined consequences for a failed version.
- **"What happens to people who already bought the old version?"** This is the question that makes the whole design hard, and it is worth saying that out loud — it is why the policy work was bigger than the engineering work.
- **"How do you know it will last?"** Designed against the dependency team's roadmap, not just today's integration, and it replaced the old flow instead of sitting alongside it. A parallel path would have decayed.
- **Waymo bridge:** same shape as changing something already deployed into the field. You cannot just ship a new version — you need versioning with provenance, a validation gate on every new version, a bounded definition of how much change is still "the same thing," and a defined consequence path when a version fails review. The engineering is the easy half; agreeing the policy across orgs is the real work.

---

## Numbers to verify

Fill these in before the interview, and **check the refund figure carefully**. In the spoken draft the number was "billion US dollars." Roblox's total annual revenue is a few billion, so a billion in refunds from one failure mode will not survive an interviewer doing arithmetic — and losing credibility on a number costs more than the number gains you. If it is actually millions, say millions. A defensible smaller number beats an impressive one you have to walk back.

| Number | Status |
|---|---|
| On-call hours per quarter on ad hoc remediation | `[N]` — needed |
| Legacy assets unblocked | `[500,000]` — confirm |
| Refunds avoided | `[$X]` — **confirm the unit: million or billion** |
| Squad size and duration | 4 from my team + 3 from Avatar Engine, 1 quarter — confirmed |
| Initiative deprioritized to fund it | `[NAME IT]` — needed |
