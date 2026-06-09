package intervals;
/*
LeetCode 2276: Count Integers in Intervals.

Stream of CLOSED intervals.  Two ops:

  void add(int left, int right)   -- mark every integer in [left, right]
  int  count()                     -- size of the union seen so far

  Constructor takes no args.  Coordinates fit in int (up to 1e9).

Comparison with LC 715 (RangeModule)
  RangeModule:  half-open [L, R), supports remove + query, no count.
  This problem:  closed [L, R],     no remove, just running count.
  Both lean on a TreeMap of disjoint intervals as the workhorse.

Storage
  TreeMap<Integer, Integer> intervals      key = start (incl), val = end (incl)
  int total                                 cached union size

  Invariant after every add:
    - All entries are disjoint and pairwise non-touching*.
    - total = sum over entries of (val - key + 1).

  *Touching merge is OPTIONAL for COUNT correctness -- [1,3] + [4,6]
   stored separately still totals 6 -- but we merge them anyway so the
   structure stays compact and future adds touch at most O(1) entries
   per query.

Algorithm for add(L, R)
  Treat the new interval as eating any existing intervals that overlap or
  touch it, then writing one merged span.  Two passes:

    1) RIGHT side: while the ceiling of L starts at <= R+1 (overlap or
       touch), absorb it and extend R if it ends past R.
    2) LEFT side: at most one floor entry can extend into [L, R+1).  If
       its end >= L-1, absorb it and extend L back to its start.

  Each absorbed entry contributes its size to `total` which we subtract
  out; after the merged interval is written we add (R - L + 1) back.

  The R+1 / L-1 thresholds are what enforce closed-interval touching.
  For half-open semantics these would be R / L instead.

Why one floor check is enough
  Pre-add the map already has disjoint intervals.  The floor entry
  e = floorEntry(L) has key < L (anything with key >= L was just
  swallowed in pass 1).  Any earlier entry has key < e.key and end <
  e.key (disjointness pre-add), so it cannot reach [L, R].  No loop
  needed on the left side.

Complexity
  add()    amortized O(log N) -- a sequence of m adds does O(m log m)
           total because each interval is inserted once and removed at
           most once.  Worst-case single call is O(K log N) where K is
           the number of intervals merged on this call.
  count()  O(1).
*/

import java.util.Map;
import java.util.TreeMap;

public class CountIntegersInIntervals {

    public static class CountIntervals {
        private final TreeMap<Integer, Integer> intervals = new TreeMap<>();
        private int total = 0;

        public void add(int left, int right) {
            // Pass 1: swallow every interval whose start is in [left, right+1].
            //   start == right+1 -> they touch; closed semantics says merge.
            //   start  <= right  -> overlap; merge.
            // Each iteration may push `right` further out, which can pull in
            // more entries -- the loop handles that naturally.
            Map.Entry<Integer, Integer> e;
            while ((e = intervals.ceilingEntry(left)) != null && e.getKey() <= right + 1) {
                right = Math.max(right, e.getValue());
                total -= e.getValue() - e.getKey() + 1;
                intervals.remove(e.getKey());
            }

            // Pass 2: at most ONE floor entry can extend leftward into our span
            // (or sit exactly adjacent at left-1).  Disjointness guarantees no
            // earlier entry could reach us.
            e = intervals.floorEntry(left);
            if (e != null && e.getValue() >= left - 1) {
                left = e.getKey();
                right = Math.max(right, e.getValue());
                total -= e.getValue() - e.getKey() + 1;
                intervals.remove(e.getKey());
            }

            intervals.put(left, right);
            total += right - left + 1;
        }

        public int count() {
            return total;
        }
    }

    /* ----------------------------- tests ----------------------------- */

    public static void main(String[] args) {
        // LC 2276 sample.
        CountIntervals lc = new CountIntervals();
        lc.add(2, 3);
        lc.add(7, 10);
        check("LC count after [2,3],[7,10]", lc.count(), 6);    // 2,3,7,8,9,10
        lc.add(5, 8);
        check("LC count after merge",          lc.count(), 8);  // 2,3,5..10

        // Disjoint adds simply sum.
        CountIntervals a = new CountIntervals();
        a.add(1, 1);
        a.add(3, 3);
        a.add(5, 5);
        check("singletons disjoint", a.count(), 3);

        // Touching closed intervals fuse: [1,3] + [4,6] = [1,6].
        CountIntervals b = new CountIntervals();
        b.add(1, 3);
        b.add(4, 6);
        check("touching closed merge", b.count(), 6);
        check("touching closed state", snapshot(b), "{1=6}");

        // Strict overlap.
        CountIntervals c = new CountIntervals();
        c.add(1, 5);
        c.add(3, 7);
        check("overlap merge", c.count(), 7);
        check("overlap state", snapshot(c), "{1=7}");

        // Nested add (already covered) is a no-op.
        CountIntervals d = new CountIntervals();
        d.add(1, 10);
        d.add(3, 5);
        check("nested no-op count", d.count(), 10);
        check("nested no-op state", snapshot(d), "{1=10}");

        // Bridge: a new add that connects two distant intervals.
        CountIntervals eMod = new CountIntervals();
        eMod.add(1, 3);
        eMod.add(7, 9);
        check("pre-bridge", eMod.count(), 6);
        eMod.add(2, 8);
        check("bridge merges all three", eMod.count(), 9);
        check("bridge state", snapshot(eMod), "{1=9}");

        // Single point add.
        CountIntervals f = new CountIntervals();
        f.add(5, 5);
        check("singleton count", f.count(), 1);
        f.add(5, 5);                                 // duplicate
        check("dup singleton stays 1", f.count(), 1);

        // Many small adds that all collapse.
        CountIntervals g = new CountIntervals();
        for (int i = 1; i <= 10; i++) g.add(i, i);
        check("ten consecutive points fuse", g.count(), 10);
        check("ten consecutive state",       snapshot(g), "{1=10}");

        // Adds that arrive out of order.
        CountIntervals h = new CountIntervals();
        h.add(50, 60);
        h.add(10, 20);
        h.add(30, 40);
        check("disjoint out-of-order count", h.count(), 33);
        h.add(20, 50);
        check("connector merges all",        h.count(), 51);
        check("connector state",             snapshot(h), "{10=60}");

        // Stress vs. brute oracle on randomized small ranges.
        java.util.Random rng = new java.util.Random(11);
        for (int trial = 0; trial < 50; trial++) {
            CountIntervals fast = new CountIntervals();
            java.util.HashSet<Integer> brute = new java.util.HashSet<>();
            for (int op = 0; op < 200; op++) {
                int l = rng.nextInt(50);
                int r = l + rng.nextInt(10);
                fast.add(l, r);
                for (int v = l; v <= r; v++) brute.add(v);
            }
            if (fast.count() != brute.size()) {
                System.out.println("FAIL  stress trial=" + trial
                        + " fast=" + fast.count() + " brute=" + brute.size());
                return;
            }
        }
        System.out.println("OK    stress (50 trials, 200 random adds each)");
    }

    /* ----------------------------- helpers ----------------------------- */

    private static String snapshot(CountIntervals c) {
        return c.intervals.toString();
    }

    private static void check(String label, Object got, Object expected) {
        boolean ok = String.valueOf(got).equals(String.valueOf(expected));
        System.out.println((ok ? "OK    " : "FAIL  ") + label
                + " got=" + got + (ok ? "" : " expected=" + expected));
    }
}
