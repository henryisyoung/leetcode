package airbnb.New2026;
/*
================================================================================
  Split Stay — Two-Listing Date Coverage  (BITMASK variant)
================================================================================

  Same problem as SplitStayCombos:

    Given a map of listing-name -> set-of-available-days and a requested
    inclusive range [start, end], find every ordered pair (P, Q) of two
    DIFFERENT listings such that P covers a non-empty prefix [start, k]
    and Q covers the remaining non-empty suffix [k+1, end] for some
    split point k in [start, end-1]. (Overlap on the seam is fine.)

  This file implements the BITMASK approach that the spec calls out as
  bonus credit. The prefix/suffix-end approach lives in SplitStayCombos.

  Spec example
      listings = {A: {1,2,3,6,7,10,11},
                  B: {3,4,5,6,8,9,10,13},
                  C: {7,8,9,10,11}}
      range    = [3, 11]   →   [(B, C)]
        B covers [3..6]  (k = 6)
        C covers [7..11]

  Algorithm — bitmask over the requested window

    Let  D = end - start + 1   (size of the requested window).
    For each listing L, compute a D-bit mask:
        bit i of mask[L]  =  1   iff   day (start + i) is in L's availability

    From each mask derive two cheap integers:
        prefixLen(L) = longest run of 1s starting at bit 0
                       = "P can cover [start, start + prefixLen - 1]"
        suffixLen(L) = longest run of 1s ending at bit D-1
                       = "Q can cover [end - suffixLen + 1, end]"

    Both are O(D) ≤ O(64) with shift-and-count loops; could be O(1) with
    Long.numberOfTrailingZeros / numberOfLeadingZeros if preferred.

    A pair (P, Q) with P != Q forms a valid split iff:
        (1)  D >= 2                       — room for two non-empty halves
        (2)  prefixLen[P] >= 1            — P actually starts the trip
        (3)  suffixLen[Q] >= 1            — Q actually ends the trip
        (4)  prefixLen[P] + suffixLen[Q] >= D
                                          — no gap in the middle
                                            (overlap is fine — pick any k)

  Why both conditions need to be checked AT THE PAIR level

    A weaker filter would be "(mask[P] | mask[Q]) == FULL" — i.e. between
    them they cover every day. That's NECESSARY but NOT SUFFICIENT: P
    and Q could collectively own every day yet leave a gap in the middle
    that neither owns ALONE, breaking the prefix-of-P / suffix-of-Q
    structure. Example over window of 4 days:
        mask[P] = 0b1001  (owns day 0 and day 3, not 1 or 2)
        mask[Q] = 0b0110  (owns days 1 and 2)
        OR      = 0b1111  (full coverage)
    But P's prefix run from bit 0 is just length 1, and Q's suffix run
    from bit D-1 is also just length 1 — no contiguous (prefix, suffix)
    pairing for a single split point exists. prefixLen + suffixLen = 2
    < D = 4, so condition (4) correctly rejects it.

  Complexity
    Let L = #listings, D = end - start + 1.
    Time:   O(L · D)   for mask construction
          + O(L · D)   for prefix/suffix derivation
          + O(L²)      for the pair check
    Memory: O(L)

    Same big-O as SplitStayCombos. The bitmask form is instructive
    because (a) it makes the "necessary vs sufficient" distinction
    explicit, and (b) it generalises cleanly to multi-listing follow-ups
    via mask OR.

  Constraint of THIS implementation
    Window size D is assumed to fit in a long (D <= 64). For larger
    windows, swap `long mask` for `BitSet` — same logic, identical
    public API. Flagged as F4 below.

  Followups worth mentioning
    F1. Return the valid split points per pair — once prefixLen[P] and
        suffixLen[Q] are known, valid k values are k in
        [start + (D - suffixLen[Q]) - 1, start + prefixLen[P] - 1]
        intersected with [start, end-1].
    F2. K-way splits — bitmask DP: reach[mask] = true iff some subset
        of listings collectively forms a valid split for that mask.
        Reconstruct K-listing arrangements via standard subset DP.
    F3. Cost / preference scoring — annotate each listing with a price
        and minimize cost over valid k for each pair.
    F4. D > 64 — use BitSet (no upper bound). See `splitStaysBitSet`
        below: `prefixLen` = `mask.nextClearBit(0)` (clamped to D),
        `suffixLen` = `D - 1 - mask.previousClearBit(D - 1)` (or D if
        that returns -1). Same algorithm, same complexity.
================================================================================
*/

import java.util.*;

public class SplitStay {

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

        int D = end - start + 1;

        Map<String, Integer> prefix = new HashMap<>();
        Map<String, Integer> suffix = new HashMap<>();

        for (Map.Entry<String, Set<Integer>> entry : listings.entrySet()) {
            String key = entry.getKey();
            Set<Integer> days = entry.getValue();
            int count = 0, d = 0;
            while (d < D && days.contains(d + start)) {
                count++;
                d++;
            }
            prefix.put(key, count);
            count = 0;
            d = 0;
            while (d < D && days.contains(end - d)) {
                count++;
                d++;
            }
            suffix.put(key, count);
        }
        List<String> list = new ArrayList<>(listings.keySet());
        Collections.sort(list);

        List<String[]> result = new ArrayList<>();
        for (String p : list) {
            if (prefix.get(p) == 0) continue;
            for (String q : list) {
                if (q.equals(p)) continue;
                if (suffix.get(q) == 0) continue;
                if (prefix.get(p) + suffix.get(q) >= D) {
                    result.add(new String[]{p, q});
                }
            }
        }

        return result;
    }

    private static Integer calSuffix(long mask, int D) {
        if (D == 0) return 0;
        long topBit = 1L << (D - 1);

        int count = 0;
        while (count < D && (mask & topBit) != 0L) {
            mask <<= 1;
            count++;
        }

        return count;
    }

    private static Integer calPrefix(long mask, int D) {
        int count = 0;
        while (count < D && (mask & 1L) == 1L) {
            mask >>>= 1;
            count++;
        }
        return count;
    }

    /* --------------------------- BitSet variant (no D ceiling) --------------------------- */
    /*
     * Drop-in replacement of `splitStays` that uses java.util.BitSet so D is
     * unbounded. Same four pair-validity conditions, same big-O. The only
     * change is how `prefixLen` and `suffixLen` are computed:
     *
     *   prefixLen = BitSet.nextClearBit(0)         clamped to D
     *   suffixLen = D - 1 - BitSet.previousClearBit(D - 1)
     *               (D if previousClearBit returns -1, i.e. window all set)
     *
     * Both BitSet methods are O(D / 64) — they scan the underlying long[]
     * a word at a time — so for typical stay windows the constant is small.
     */
    public static List<String[]> splitStaysBitSet(
            Map<String, Set<Integer>> listings, int start, int end) {

        if (listings == null || listings.size() < 2 || start >= end) {
            return new ArrayList<>();
        }
        int D = end - start + 1;

        Map<String, Integer> prefixLen = new HashMap<>();
        Map<String, Integer> suffixLen = new HashMap<>();
        for (Map.Entry<String, Set<Integer>> e : listings.entrySet()) {
            Set<Integer> days = e.getValue();
            BitSet m = new BitSet(D);
            for (int i = 0; i < D; i++) {
                if (days.contains(start + i)) m.set(i);
            }
            prefixLen.put(e.getKey(), prefixRunBs(m, D));
            suffixLen.put(e.getKey(), suffixRunBs(m, D));
        }

        List<String> names = new ArrayList<>(listings.keySet());
        Collections.sort(names);

        List<String[]> result = new ArrayList<>();
        for (String p : names) {
            int pl = prefixLen.get(p);
            if (pl < 1) continue;
            for (String q : names) {
                if (p.equals(q)) continue;
                int sl = suffixLen.get(q);
                if (sl >= 1 && pl + sl >= D) {
                    result.add(new String[]{p, q});
                }
            }
        }
        return result;
    }

    private static int prefixRunBs(BitSet mask, int D) {
        // nextClearBit(0) returns D when bits [0..D-1] are all set
        // (BitSet treats out-of-range positions as clear, so the first clear
        // is exactly at index D). Clamp anyway in case mask has trailing 1s
        // someone set beyond D-1.
        return Math.min(mask.nextClearBit(0), D);
    }

    private static int suffixRunBs(BitSet mask, int D) {
        int prevClear = mask.previousClearBit(D - 1);
        if (prevClear < 0) return D;
        return D - 1 - prevClear;
    }

    /* --------------------------- Brute force (reference oracle) --------------------------- */
    /*
     * The "obvious" baseline: for every ordered pair (P, Q) with P != Q, try
     * every split point k in [start, end-1] and check directly whether P
     * covers [start, k] and Q covers [k+1, end] by `Set.contains` per day.
     *
     * Complexity: O(L^2 * D^2) — L^2 pairs, D split points, O(D) coverage
     * check per side. The bitmask/BitSet variants run in O(L * D + L^2).
     *
     * Why keep it? Two reasons:
     *   1. It's the version to write first in an interview before optimizing;
     *      having it side-by-side documents that progression.
     *   2. It's an independent oracle: the dual-implementation tests below
     *      route the same corpus through all three solutions, so any future
     *      refactor that diverges silently gets caught immediately.
     */
    public static List<String[]> splitStaysBruteForce(
            Map<String, Set<Integer>> listings, int start, int end) {

        if (listings == null || listings.size() < 2 || start >= end) {
            return new ArrayList<>();
        }
        List<String> names = new ArrayList<>(listings.keySet());
        Collections.sort(names);

        List<String[]> result = new ArrayList<>();
        for (String p : names) {
            Set<Integer> pDays = listings.get(p);
            for (String q : names) {
                if (p.equals(q)) continue;
                Set<Integer> qDays = listings.get(q);
                for (int k = start; k <= end - 1; k++) {
                    if (covers(pDays, start, k) && covers(qDays, k + 1, end)) {
                        result.add(new String[]{p, q});
                        break;                                 // one valid k is enough per pair
                    }
                }
            }
        }
        return result;
    }

    private static boolean covers(Set<Integer> days, int lo, int hi) {
        for (int d = lo; d <= hi; d++) {
            if (!days.contains(d)) return false;
        }
        return true;
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

        // Three listings: (A,B) at k∈{2,3}, (A,C) at k∈{1,2,3}.
        check("multiple pairs",
              listings("A", days(1, 2, 3),
                       "B", days(3, 4, 5),
                       "C", days(2, 3, 4, 5)),
              1, 5,
              pairs("A", "B",
                    "A", "C"));

        // Gap in middle that neither covers.
        check("uncoverable gap",
              listings("A", days(1, 2, 3),
                       "B", days(5, 6, 7)),
              1, 7,
              pairs());

        // Adjacent (no gap, no overlap).
        check("adjacent split",
              listings("A", days(1, 2, 3, 4),
                       "B", days(5, 6, 7, 8)),
              1, 8,
              pairs("A", "B"));

        // Listings outside the range are ignored.
        check("listings outside range",
              listings("A", days(1, 2, 3, 4, 5),
                       "B", days(100, 101)),
              1, 5,
              pairs());

        // Only one listing.
        check("single listing",
              listings("A", days(1, 2, 3, 4, 5)),
              1, 5,
              pairs());

        // Empty input.
        check("empty listings",
              new HashMap<>(),
              1, 5,
              pairs());

        // Q only owns the end day.
        check("Q only owns end day",
              listings("A", days(1, 2, 3, 4, 5),
                       "B", days(5)),
              1, 5,
              pairs("A", "B"));

        // P only owns start day.
        check("P only owns start day",
              listings("A", days(1),
                       "B", days(2, 3, 4, 5)),
              1, 5,
              pairs("A", "B"));

        // Overlap on seam — still one combo per direction.
        check("overlap on seam",
              listings("A", days(1, 2, 3, 4),
                       "B", days(3, 4, 5, 6)),
              1, 6,
              pairs("A", "B"));

        // Tricky case the bitmask comment calls out:
        //   mask[P] = 1001  (days 0 and 3 only)
        //   mask[Q] = 0110  (days 1 and 2)
        // P|Q == 1111 (full coverage), but neither has a usable prefix/suffix
        // pairing for a single split. prefixLen[P]=1, suffixLen[Q]=0 — fails.
        check("OR covers but no valid split",
              listings("P", days(1, 4),
                       "Q", days(2, 3)),
              1, 4,
              pairs());

        // Cross-validation: all four implementations agree on the spec example.
        Map<String, Set<Integer>> specIn = listings(
                "A", days(1, 2, 3, 6, 7, 10, 11),
                "B", days(3, 4, 5, 6, 8, 9, 10, 13),
                "C", days(7, 8, 9, 10, 11));
        List<String[]> viaLong   = splitStays(specIn, 3, 11);
        List<String[]> viaBitSet = splitStaysBitSet(specIn, 3, 11);
        List<String[]> viaBrute  = splitStaysBruteForce(specIn, 3, 11);
        List<String[]> viaCombos = SplitStayCombos.splitStays(specIn, 3, 11);
        check("cross-validate long   vs SplitStayCombos",
                pairsFromList(viaLong),   pairsFromList(viaCombos));
        check("cross-validate bitset vs SplitStayCombos",
                pairsFromList(viaBitSet), pairsFromList(viaCombos));
        check("cross-validate brute  vs SplitStayCombos",
                pairsFromList(viaBrute),  pairsFromList(viaCombos));
        check("cross-validate long   vs bitset",
                pairsFromList(viaLong),   pairsFromList(viaBitSet));
        check("cross-validate long   vs brute",
                pairsFromList(viaLong),   pairsFromList(viaBrute));

        // BitSet-only stress: D > 64. The long-mask variant would throw here.
        // Window [1..100]; A owns days 1..60, B owns days 50..100 → (A,B) valid
        // (split at k=50: A=[1..50], B=[51..100]). prefixLen[A]=60, suffixLen[B]=51,
        // 60+51 = 111 >= D=100.
        Map<String, Set<Integer>> wide = new LinkedHashMap<>();
        Set<Integer> aDays = new HashSet<>();
        for (int d = 1;  d <= 60;  d++) aDays.add(d);
        Set<Integer> bDays = new HashSet<>();
        for (int d = 50; d <= 100; d++) bDays.add(d);
        wide.put("A", aDays);
        wide.put("B", bDays);
        check("D=100 [bitset]", splitStaysBitSet(wide, 1, 100), pairs("A", "B"));
    }

    /* --------------------------- helpers --------------------------- */

    private static Set<Integer> days(int... d) {
        Set<Integer> s = new HashSet<>();
        for (int x : d) s.add(x);
        return s;
    }

    private static Map<String, Set<Integer>> listings(Object... kv) {
        Map<String, Set<Integer>> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            @SuppressWarnings("unchecked")
            Set<Integer> v = (Set<Integer>) kv[i + 1];
            m.put((String) kv[i], v);
        }
        return m;
    }

    /** Expected output, encoded as flat pairs("A","B","C","D") -> [{A,B},{C,D}]. */
    private static List<String[]> pairs(String... flat) {
        List<String[]> out = new ArrayList<>();
        for (int i = 0; i < flat.length; i += 2) out.add(new String[]{flat[i], flat[i + 1]});
        return out;
    }

    private static List<String[]> pairsFromList(List<String[]> in) {
        // Defensive copy for the cross-check (so order/equality is stable).
        List<String[]> out = new ArrayList<>(in.size());
        for (String[] p : in) out.add(new String[]{p[0], p[1]});
        return out;
    }

    private static void check(String label, Map<String, Set<Integer>> listings,
                              int start, int end, List<String[]> expected) {
        check(label + " [long]  ", splitStays(listings, start, end),           expected);
        check(label + " [bitset]", splitStaysBitSet(listings, start, end),     expected);
        check(label + " [brute] ", splitStaysBruteForce(listings, start, end), expected);
    }

    private static void check(String label, List<String[]> got, List<String[]> expected) {
        boolean ok = sameSet(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + "  got=" + render(got) + " expected=" + render(expected));
    }

    private static boolean sameSet(List<String[]> a, List<String[]> b) {
        if (a.size() != b.size()) return false;
        Set<String> aa = new HashSet<>(), bb = new HashSet<>();
        for (String[] p : a) aa.add(p[0] + "," + p[1]);
        for (String[] p : b) bb.add(p[0] + "," + p[1]);
        return aa.equals(bb);
    }

    private static String render(List<String[]> in) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < in.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("(").append(in.get(i)[0]).append(",").append(in.get(i)[1]).append(")");
        }
        return sb.append("]").toString();
    }
}
