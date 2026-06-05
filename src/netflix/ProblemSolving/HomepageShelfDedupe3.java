package netflix.ProblemSolving;

import java.util.*;

/*
================================================================================
  Netflix homepage dedupe — interview script + minimal impl (no insert).
================================================================================

  Problem (rough): a Netflix homepage is a list of "shelves" (rows of titleIds).
  Each shelf has a horizontal viewport of size X. Dedupe the page so the user
  doesn't see the same title in more than one place.

  The TPS failure mode for this question is:
    "ask a few questions, get a verbal OK, start coding, realize 5 minutes
     later the requirement was different."
  The fix is example-driven clarification BEFORE writing any code.


  --------------------------------------------------------------------------
  STEP 1 — Clarify the problem (questions to ASK, not assume)
  --------------------------------------------------------------------------
  Ask these in roughly this order. Each one has a hidden trap.

  Q1. "Each shelf is an ordered list of titleIds, right? Are titleIds
       integers, and is the order significant?"
       → Confirm input shape and that we must preserve order.

  Q2. "Is X the same for all shelves, or per-shelf?"
       → Most likely same. Confirm before deciding parameter shape.

  Q3. "What's the dedupe SCOPE?
        (a) within each shelf only?
        (b) across the whole page?
        (c) something mixed — e.g., first X cross-shelf, rest within-shelf?"
       → The interview answer is (c). Don't assume.

  Q4. "When I encounter a duplicate, do I SKIP it and continue, or STOP
       the shelf there?"
       → Skip and continue is the standard. Confirm.

  Q5. "Does X count OUTPUT positions (kept items) or INPUT positions
       (titles examined)?"
       → Output positions. This is the v2 vs v3 trap from your TPS reflection.

  Q6. "If a shelf can't reach X global-unique picks, does it switch to
       within-shelf mode, or stay in cross-shelf mode for the rest of
       the shelf?"
       → Stay in cross-shelf mode. (Algorithm consequence of the if/else.)

  Q7. "Is this batch (one-shot) or stateful (append/insert later)?"
       → Affects API shape: pure function vs class.


  --------------------------------------------------------------------------
  STEP 2 — Lock down with EXAMPLES (counterexamples drive alignment)
  --------------------------------------------------------------------------
  Put 2–3 tiny examples on the whiteboard and get the interviewer to
  confirm the expected output. This is what "collaborate more" looks like.

  X = 2 in all examples below.

  Ex1 (cross-shelf dedup in viewport):
        shelves = [[1, 2, 3], [1, 4, 5]]
        expected = [[1, 2, 3], [4, 5]]
        — shelf 1 viewport [1,2], tail [3]
        — shelf 2 skips 1 (already globally seen), viewport [4,5]

  Ex2 (within-shelf dedup in tail):
        shelves = [[1, 2, 3, 1, 4]]
        expected = [[1, 2, 3, 4]]
        — viewport [1,2], tail drops second 1 (local dup), keeps 4

  Ex3 (skipped items don't count toward X — the v2 trap):
        shelves = [[1, 1, 2, 3]], with X = 2
        expected = [[1, 2, 3]]
        — first 1 kept, second 1 skipped (doesn't fill viewport slot),
          2 fills viewport, 3 goes to tail.

  Ex4 (shelf can't fill viewport — rule 4):
        shelves = [[1, 2], [1, 2, 3, 4]], X = 5
        expected = [[1, 2], [3, 4]]
        — shelf 2 stays in viewport mode the whole shelf because it
          never reaches 5 picks; 1 and 2 are rejected (globally seen)
          even though they'd be locally fine.


  --------------------------------------------------------------------------
  STEP 3 — Propose the INTERFACE (then let the interviewer steer)
  --------------------------------------------------------------------------
  Lay out two API options and ask which one fits:

    Option A — pure function (simpler, batch only)
      List<List<Integer>> dedupe(int[][] shelves, int x)

    Option B — stateful class (supports incremental updates)
      class Homepage {
          Homepage(int x);
          void initial(int[][] shelves);   // batch build (resets state)
          void append(int[] shelf);        // add one shelf
          List<List<Integer>> view();      // read-only snapshot
      }

  Recommendation in the room:
    "I'll go with Option B since it makes the followups (insert / append)
     trivial later. If you only need batch, you can call initial() once
     and view() — same shape as Option A in two lines."


  --------------------------------------------------------------------------
  STEP 4 — State the ALGORITHM in one sentence
  --------------------------------------------------------------------------
  Two-mode per-shelf walk:
    while kept_in_this_shelf < X → filter against the GLOBAL seen set
    once it reaches X           → filter against the SHELF-LOCAL seen set
  Each kept item goes into both sets while in viewport mode, so the tail
  cannot repeat a viewport pick of the same shelf.

  Per-shelf invariant:
      rowOut.size() <  x → "viewport mode": dedupe on globalSeen
      rowOut.size() == x → "tail mode":     dedupe on localSeen


  --------------------------------------------------------------------------
  STEP 5 — Call out EDGE CASES before coding
  --------------------------------------------------------------------------
    - empty shelves array       → []
    - empty individual shelf    → []
    - x = 0                     → tail mode always; per-shelf-unique only
    - shelf entirely duplicate  → produces fewer than X items, stays in
                                  viewport mode forever (Rule 4)
    - same shelf appended twice → second one contributes nothing
                                  (all globally seen)

  Mention them out loud, don't write defensive code for each — that
  signals you've thought about them without burning time.


  --------------------------------------------------------------------------
  STEP 6 — Implementation (below)
  --------------------------------------------------------------------------
  Tiny:
    - constructor with x validation
    - initial() resets state then loops append()
    - append() calls dedupeShelf() and stores the result
    - dedupeShelf() is the two-mode loop above
    - view() returns an unmodifiable snapshot

  Complexity:
    Time:  O(L) where L = total titles across all shelves
    Space: O(G + max-shelf-size) for the seen sets


  --------------------------------------------------------------------------
  STEP 7 — Followups to volunteer (don't wait to be asked)
  --------------------------------------------------------------------------
    F1. "What if a new shelf is INSERTED at position k?"
        → cascade only into shelves[k..]; rebuild globalSeen from the
          viewport portions of kept[0..k-1] (no input replay).

    F2. "What if X varies per shelf?"
        → pass x to append(shelf, x); store it alongside if needed.

    F3. "What if the dedupe key is FRANCHISE, not titleId?"
        → take a Function<Integer, Integer> keyExtractor; same algorithm,
          extract key before checking sets. Single line change.

    F4. "What about THREAD safety / concurrent appends?"
        → wrap with synchronized append/view, or move to a
          ConcurrentHashMap-backed seen set. Out of scope unless asked.
================================================================================
*/
public class HomepageShelfDedupe3 {
    Set<Integer> globalSeen;
    List<List<Integer>> kept;
    List<List<Integer>> input;
    int x;

    public HomepageShelfDedupe3(int x) {
        this.x = x;
        this.globalSeen = new HashSet<>();
        this.kept = new ArrayList<>();
        this.input = new ArrayList<>();
    }

    public void initial(List<List<Integer>> shells) {
        globalSeen.clear();
        kept.clear();
        this.input = new ArrayList<>();
        for (List<Integer> shell : shells) {
            append(shell);
        }
    }

    public void append(List<Integer> shell) {
        input.add(shell);
        kept.add(dedupShell(shell));
    }

    private List<Integer> dedupShell(List<Integer> shell) {
        Set<Integer> localSeen = new HashSet<>();
        List<Integer> shellOut = new ArrayList<>();
        for (int curVal : shell) {
            if (shellOut.size() < x) {
                if (globalSeen.add(curVal)) {
                    localSeen.add(curVal);
                    shellOut.add(curVal);
                }
            } else if (localSeen.add(curVal)) {
                shellOut.add(curVal);
            }
        }

        return shellOut;
    }

    public void insert(int index, List<Integer> shell) {
        input.add(index, shell);
        while (kept.size() > index) kept.removeLast();
        recal();

        for (int i = index; i < input.size(); i++) {
            kept.add(dedupShell(input.get(i)));
        }
    }

    private void recal() {
        globalSeen.clear();
        for (int i = 0; i < kept.size(); i++) {
            List<Integer> list = kept.get(i);
            globalSeen.addAll(list.subList(0, Math.min(list.size(), x)));
        }
    }

    public List<List<Integer>> pageView() {
        return kept;
    }
}
