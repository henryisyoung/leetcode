package airbnb.New2026;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
================================================================================
  Split Stay Combos across 2 Listings  (Airbnb)
================================================================================

  Suggest a "split stay": pick TWO different listings (first, second) such that
  the first covers a non-empty prefix [start, k] of the requested date range
  and the second covers the remaining non-empty suffix [k+1, end] — with no
  gap and no overlap requirement on dates (overlap is fine; we just need each
  half to be fully covered by its listing).

  Return all valid ordered pairs (first, second). Listings are named by a
  short id; availability is a set of day numbers.

  Spec example
    listings = {A: {1,2,3,6,7,10,11},
                B: {3,4,5,6,8,9,10,13},
                C: {7,8,9,10,11}}
    range    = [3, 11]   →   [(B, C)]
      B covers [3..6]  (k = 6)
      C covers [7..11]

  Algorithm  (O(L·D + L²))
    For each listing L, compute exactly two values over the range [start,end]:
      prefixEnd[L]   = the largest d such that L covers EVERY day in [start, d];
                       sentinel start-1 if L lacks `start` entirely.
      suffixStart[L] = the smallest d such that L covers EVERY day in [d, end];
                       sentinel end+1   if L lacks `end`   entirely.

    A pair (P, Q) with P != Q is valid iff there exists a split point k with
                     start ≤ k ≤ end-1
                     k  ≤ prefixEnd[P]
                     k+1 ≥ suffixStart[Q]
    Such a k exists iff ALL of:
         (1)  start <  end                       — range has room for 2 segments
         (2)  prefixEnd[P]   ≥ start             — P actually starts the trip
         (3)  suffixStart[Q] ≤ end               — Q actually ends   the trip
         (4)  prefixEnd[P]   ≥ suffixStart[Q]-1  — no gap between P's end & Q's start

    Note: condition (4) allows overlap (e.g. both could cover the seam day);
    we just pick any valid k. Multiple (P, Q) pairs with different k's still
    count as ONE combo per pair, matching the spec's "[B, C]" output style.

  Complexity
    Let L = #listings, D = end - start + 1.
    Time:   O(L · D)  for the prefix/suffix scan  +  O(L²) for the pair check
    Memory: O(L)

  Followups worth mentioning
    F1. Return the valid split points (or the [start,k] / [k+1,end] segments)
        per pair, not just the names — trivial: emit (P, Q, k) tuples for
        every k in [max(start, ss-1), min(pe, end-1)].
    F2. K-way splits (stay at K houses) — interval DP:
        coverable[L, lo, hi] precomputed via prefix/suffix; then
        dp[d] = min splits to cover [start, d], transition via reachable
        intervals. O(K · L · D²) in the naive form.
    F3. Cost / preference scoring — weight pairs by total price, ranking, or
        switch-friendliness; return top-N instead of all.
    F4. Sparse availability via intervals (e.g. ranges) instead of day sets —
        replace HashSet lookup with binary search into a sorted interval list.
    F5. Concurrency: precompute prefix/suffix per listing in parallel (each
        listing independent) before the pair-check pass.
================================================================================
*/
public class SplitStayCombos {

    /**
     * @param listings  name -> set of available day numbers
     * @param start     inclusive start day of requested range
     * @param end       inclusive end   day of requested range
     * @return ordered pairs (first, second) sorted lexicographically by name
     */
    public static List<String[]> splitStays(
            Map<String, Set<Integer>> listings, int start, int end) {

        if (listings == null || listings.size() < 2 || start >= end) {
            return new ArrayList<>();
        }

        Map<String, Integer> prefixEnd   = new HashMap<>();
        Map<String, Integer> suffixStart = new HashMap<>();
        for (Map.Entry<String, Set<Integer>> e : listings.entrySet()) {
            Set<Integer> days = e.getValue();
            int pe = start - 1;
            for (int d = start; d <= end && days.contains(d); d++) pe = d;
            prefixEnd.put(e.getKey(), pe);

            int ss = end + 1;
            for (int d = end; d >= start && days.contains(d); d--) ss = d;
            suffixStart.put(e.getKey(), ss);
        }

        List<String> names = new ArrayList<>(listings.keySet());
        Collections.sort(names);                 // deterministic output

        List<String[]> result = new ArrayList<>();
        for (String p : names) {
            int pe = prefixEnd.get(p);
            if (pe < start) continue;            // P can't start the trip
            for (String q : names) {
                if (p.equals(q)) continue;
                int ss = suffixStart.get(q);
                if (ss <= end && pe >= ss - 1) { // Q ends the trip and no gap
                    result.add(new String[]{p, q});
                }
            }
        }
        return result;
    }

    /* --------------------------- Demos / tests --------------------------- */

    public static void main(String[] args) {
        // Spec example.
        check("spec",
              listings("A", days(1, 2, 3, 6, 7, 10, 11),
                       "B", days(3, 4, 5, 6, 8, 9, 10, 13),
                       "C", days(7, 8, 9, 10, 11)),
              3, 11,
              pairs("B", "C"));

        // Both fully cover the range → both orderings are valid.
        check("two full coverers",
              listings("A", days(1, 2, 3, 4, 5),
                       "B", days(1, 2, 3, 4, 5)),
              1, 5,
              pairs("A", "B",
                    "B", "A"));

        // Single-day range — can't split into 2 non-empty parts.
        check("single-day range",
              listings("A", days(5),
                       "B", days(5)),
              5, 5,
              pairs());

        // No listing covers `start` → empty.
        check("nobody owns start",
              listings("A", days(5, 6, 7),
                       "B", days(5, 6, 7)),
              3, 7,
              pairs());

        // No listing covers `end` → empty.
        check("nobody owns end",
              listings("A", days(3, 4, 5),
                       "B", days(3, 4, 5)),
              3, 7,
              pairs());

        // Three listings with multiple valid pairs.
        // A covers [1..3]; B starts at 3, covers [3..5]; C covers [2..5].
        // Pairs: (A,B) split k∈{2,3};  (A,C) split k∈{1,2,3}.  (Both count once.)
        check("multiple pairs",
              listings("A", days(1, 2, 3),
                       "B", days(3, 4, 5),
                       "C", days(2, 3, 4, 5)),
              1, 5,
              pairs("A", "B",
                    "A", "C"));

        // Gap in middle: A covers [1..3], B covers [5..7]; range [1..7] has
        // day 4 uncovered by both → no valid split.
        check("uncoverable gap",
              listings("A", days(1, 2, 3),
                       "B", days(5, 6, 7)),
              1, 7,
              pairs());

        // Adjacent (no gap, no overlap): A=[1..4], B=[5..8], range [1..8].
        check("adjacent split",
              listings("A", days(1, 2, 3, 4),
                       "B", days(5, 6, 7, 8)),
              1, 8,
              pairs("A", "B"));

        // Listings outside the range are ignored entirely.
        check("listings outside range",
              listings("A", days(1, 2, 3, 4, 5),
                       "B", days(100, 101)),
              1, 5,
              pairs());

        // Only one listing → can't pair with itself.
        check("single listing",
              listings("A", days(1, 2, 3, 4, 5)),
              1, 5,
              pairs());

        // Empty listings input.
        check("empty listings",
              new HashMap<>(),
              1, 5,
              pairs());

        // P covers entire range but Q only covers tail — (P,Q) valid:
        // split at k = end-1 leaves Q with just [end].
        check("Q only owns end day",
              listings("A", days(1, 2, 3, 4, 5),
                       "B", days(5)),
              1, 5,
              pairs("A", "B"));

        // P only owns start day, Q owns the rest.
        check("P only owns start day",
              listings("A", days(1),
                       "B", days(2, 3, 4, 5)),
              1, 5,
              pairs("A", "B"));

        // Overlap on seam day — still counts as one combo.
        // A=[1..4], B=[3..6], range [1..6]. k can be 2, 3, or 4. (A,B) once.
        check("overlap on seam",
              listings("A", days(1, 2, 3, 4),
                       "B", days(3, 4, 5, 6)),
              1, 6,
              pairs("A", "B"));
    }

    /* --------------------------- helpers --------------------------- */

    private static Set<Integer> days(int... d) {
        Set<Integer> s = new HashSet<>();
        for (int x : d) s.add(x);
        return s;
    }

    /** Builds a LinkedHashMap to preserve insertion order in error messages. */
    @SuppressWarnings("unchecked")
    private static Map<String, Set<Integer>> listings(Object... kv) {
        Map<String, Set<Integer>> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], (Set<Integer>) kv[i + 1]);
        }
        return m;
    }

    /** pairs("A","B","C","D") -> [[A,B],[C,D]]. */
    private static List<String[]> pairs(String... flat) {
        List<String[]> out = new ArrayList<>();
        for (int i = 0; i < flat.length; i += 2) out.add(new String[]{flat[i], flat[i + 1]});
        return out;
    }

    private static void check(String label,
                              Map<String, Set<Integer>> listings, int start, int end,
                              List<String[]> expected) {
        List<String[]> got = splitStays(listings, start, end);
        boolean ok = sameOrdered(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + " [" + start + "," + end + "]"
                + " expected=" + fmt(expected) + " got=" + fmt(got));
    }

    private static boolean sameOrdered(List<String[]> a, List<String[]> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) if (!Arrays.equals(a.get(i), b.get(i))) return false;
        return true;
    }

    private static String fmt(List<String[]> ps) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ps.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(Arrays.toString(ps.get(i)));
        }
        return sb.append("]").toString();
    }
}
