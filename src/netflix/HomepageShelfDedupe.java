package netflix;
/*
Netflix homepage dedupe.

A homepage is a list of "shelves" (rows of titleIds). Each shelf has a
horizontal viewport of size X — only the first X items show on load,
the rest are revealed by horizontal scroll. Vertical scroll is unbounded.

Rules
  - First X kept items per shelf: globally unique vs. the first-X
    picks of all PRIOR shelves and vs. earlier picks in this shelf.
  - Beyond X: locally unique within the shelf only.
  - Skipped items don't count toward X (X is a cap on OUTPUT positions).
  - A shelf that can't reach X global-unique picks stays in viewport
    mode for its entire length.

Per-shelf invariant
  out.size() < x → "viewport mode": filter on globalSeen
  out.size() == x → "tail mode":    filter on localSeen
  localSeen always contains every emitted title for this shelf, so the
  tail can never repeat a viewport pick.

Followup (asked in interview): insert a new shelf at position k.
  Cascade is bounded to shelves[k..]; rebuild globalSeen from the
  PRIOR shelves' viewports (which are just the first min(x, |kept[i]|)
  items of each kept row) — no input replay.
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomepageShelfDedupe {

    /** Pure batch: one canonical pass across all shelves. */
    public static List<List<Integer>> dedupe(int[][] shelves, int x) {
        Homepage h = new Homepage(x);
        for (int[] s : shelves) h.append(s);
        return h.view();
    }

    /** Stateful homepage supporting append + insert. */
    public static class Homepage {
        private final int x;
        private final List<int[]> input = new ArrayList<>();
        private final List<List<Integer>> kept = new ArrayList<>();
        // Maintained incrementally: append mutates in place; insert clears + rebuilds.
        private final Set<Integer> globalSeen = new HashSet<>();

        public Homepage(int x) {
            if (x < 0) throw new IllegalArgumentException("x must be >= 0");
            this.x = x;
        }

        public void append(int[] shelf) {
            input.add(shelf);
            kept.add(dedupeShelf(shelf, globalSeen));   // O(|shelf|), no rebuild
        }

        /** Insert at idx; cascades only into shelves[idx..]. */
        public void insert(int idx, int[] shelf) {
            input.add(idx, shelf);
            kept.subList(idx, kept.size()).clear();
            rebuildGlobalSeenUpTo(idx);                 // reset to the snapshot at idx
            for (int i = idx; i < input.size(); i++) {
                kept.add(dedupeShelf(input.get(i), globalSeen));
            }
        }

        public List<List<Integer>> view() {
            return Collections.unmodifiableList(kept);
        }

        // --- core: single-shelf dedupe; mutates `g` with this shelf's viewport picks ---
        private List<Integer> dedupeShelf(int[] shelf, Set<Integer> g) {
            List<Integer> out = new ArrayList<>();
            Set<Integer> local = new HashSet<>();
            for (int t : shelf) {
                if (out.size() < x) {
                    // Viewport: must be globally new. Set.add returns false if already present.
                    if (g.add(t)) { out.add(t); local.add(t); }
                } else if (local.add(t)) {
                    // Tail: only locally unique. globalSeen is irrelevant past X.
                    out.add(t);
                }
            }
            return out;
        }

        /** Reset globalSeen to the state it would have after processing kept[0..end).
         *  Viewport picks are exactly the first min(x, |kept[i]|) items — no input replay. */
        private void rebuildGlobalSeenUpTo(int end) {
            globalSeen.clear();
            for (int i = 0; i < end; i++) {
                List<Integer> k = kept.get(i);
                for (int j = 0, n = Math.min(x, k.size()); j < n; j++) globalSeen.add(k.get(j));
            }
        }
    }

    public static void main(String[] args) {
        // 1. Batch — the original problem.
        System.out.println(dedupe(
                new int[][]{{1, 2, 3, 1, 4}, {2, 3, 5, 5, 6}, {1, 7, 8}}, 2));
        // → [[1, 2, 3, 4], [3, 5, 6], [7, 8]]

        // 2. Insert mid-stream. The cascade reaches later shelves only when
        //    the new shelf consumes globals that those shelves had relied on.
        Homepage h = new Homepage(2);
        h.append(new int[]{1, 2, 3, 1, 4});       // [1,2,3,4]
        h.append(new int[]{2, 3, 5, 5, 6});       // [3,5,6]
        h.append(new int[]{1, 7, 8});             // [7,8]
        System.out.println("before: " + h.view());

        h.insert(1, new int[]{3, 5});             // new shelf grabs 3 and 5 globally
        System.out.println("after:  " + h.view());
        // → [[1, 2, 3, 4], [3, 5], [6], [7, 8]]
        //   - new row at idx 1 = [3, 5]
        //   - old row 1 (was [3, 5, 6]) now collides on 3 & 5 → [6]
        //   - old row 2 ([7, 8]) is unchanged: its viewport picks didn't collide
    }
}
