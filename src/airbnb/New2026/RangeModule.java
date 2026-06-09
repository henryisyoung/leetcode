package airbnb.New2026;
/*
LeetCode 715: Range Module.

Track half-open ranges [left, right) of integers and answer:

  void    addRange(int left, int right)     -- mark [left, right) as tracked
  boolean queryRange(int left, int right)   -- is EVERY x in [left, right) tracked?
  void    removeRange(int left, int right)  -- un-track every x in [left, right)

Half-open is the right convention: [1, 3) and [3, 5) touch but don't overlap,
and after addRange they should fuse into [1, 5).

Storage -- a TreeMap of disjoint, non-touching intervals
  Key   = interval start (inclusive)
  Value = interval end   (exclusive)

  Invariant after every op: no two entries overlap or touch.  This makes
  query trivial (one floorKey) and keeps the structure compact.

  Sizes: at most O(N) entries where N = #addRange calls; merges only
  decrease the count.

Why TreeMap (not segment tree, not Set<Integer>)
  - Coordinates are up to 1e9; a Set<Integer> blows memory.
  - We need range merge / split / clear in O(log N) per touched neighbor.
    TreeMap.subMap(...).clear() is the magic primitive.
  - Segment tree with coordinate compression also works, but only if the
    full set of coordinates is known up front.  TreeMap handles the
    dynamic case directly.

Two tricks worth pointing out
  T1. addRange's "anchor on floorKey" trick.
      For both edges (left and right), look at the largest existing key
      <= edge.  If its interval reaches our edge or beyond, swallow it.
      Then nuke everything strictly between the new left and right via
      subMap.clear().  No iterating, no manual loops.

  T2. removeRange splits BEFORE clearing.
      We may need to keep a left tail (start, left) and / or a right
      tail [right, end).  Order matters: splice the right tail in FIRST
      (its key = right, lives outside the clear window), then trim the
      left tail by overwriting (start, end) with (start, left).  Finally
      subMap(left, true, right, false).clear() drops every key in
      [left, right) -- including any spurious (left, left) we may have
      written when start == left.  This last detail is what makes the
      "always overwrite, let clear sort it out" pattern correct.

Complexity (N = #intervals stored)
  add     amortized O(log N + K)   K intervals merged on this call (cleared at the end).
  query   O(log N)
  remove  O(log N + K)             K intervals split or cleared.
*/

import java.util.Map;
import java.util.TreeMap;

public class RangeModule {

    private final TreeMap<Integer, Integer> map = new TreeMap<>();

    public void addRange(int left, int right) {
        // T1: extend left edge if a floor interval reaches us.
        Integer s = map.floorKey(left);
        if (s != null && map.get(s) >= left) left = s;

        // Extend right edge if some interval starting <= right ends past us.
        Integer e = map.floorKey(right);
        if (e != null && map.get(e) > right) right = map.get(e);

        // Stamp the merged interval and nuke everything between (left, right].
        map.put(left, right);
        map.subMap(left, false, right, true).clear();
    }

    public boolean queryRange(int left, int right) {
        // The only interval that could possibly cover [left, right) is the
        // one anchored at floorKey(left). If its end >= right, we're done.
        Integer s = map.floorKey(left);
        return s != null && map.get(s) >= right;
    }

    public void removeRange(int left, int right) {
        Integer e = map.floorKey(right);
        Integer s = map.floorKey(left);

        // T2: splice right tail FIRST so its key (= right) survives clear().
        if (e != null && map.get(e) > right) map.put(right, map.get(e));

        // Trim left tail by overwriting (s, end) with (s, left).
        // If s == left, this writes a degenerate (left, left); subMap.clear()
        // below sweeps it away because the clear window is [left, right).
        if (s != null && map.get(s) > left) map.put(s, left);

        map.subMap(left, true, right, false).clear();
    }

    /* ----------------------------- tests ----------------------------- */

    public static void main(String[] args) {
        // LC 715 sample.
        RangeModule rm = new RangeModule();
        rm.addRange(10, 20);
        rm.removeRange(14, 16);
        check("LC q [10,14)", rm.queryRange(10, 14), true);
        check("LC q [13,15)", rm.queryRange(13, 15), false);   // 14..15 removed
        check("LC q [16,17)", rm.queryRange(16, 17), true);

        // Touching merges (half-open semantics).
        RangeModule a = new RangeModule();
        a.addRange(1, 3);
        a.addRange(3, 5);
        check("touching merge add", a.queryRange(1, 5), true);
        check("touching merge size", snapshot(a), "{1=5}");

        // Overlapping adds.
        RangeModule b = new RangeModule();
        b.addRange(1, 5);
        b.addRange(3, 7);
        check("overlap add", snapshot(b), "{1=7}");

        // Add into the middle of a longer interval is a no-op.
        RangeModule c = new RangeModule();
        c.addRange(1, 10);
        c.addRange(3, 5);
        check("add inside", snapshot(c), "{1=10}");

        // Add bridging two intervals merges all three.
        RangeModule d = new RangeModule();
        d.addRange(1, 3);
        d.addRange(7, 9);
        d.addRange(2, 8);
        check("bridge", snapshot(d), "{1=9}");

        // Remove from middle splits in two.
        RangeModule eMod = new RangeModule();
        eMod.addRange(1, 10);
        eMod.removeRange(3, 7);
        check("remove middle", snapshot(eMod), "{1=3, 7=10}");

        // Remove that crosses two intervals trims both and clears the middle.
        RangeModule f = new RangeModule();
        f.addRange(1, 5);
        f.addRange(7, 10);
        f.removeRange(3, 8);
        check("remove crosses", snapshot(f), "{1=3, 8=10}");

        // Remove that engulfs an interval entirely deletes it.
        RangeModule g = new RangeModule();
        g.addRange(2, 5);
        g.removeRange(0, 10);
        check("remove engulf", snapshot(g), "{}");

        // Remove on empty -> still empty.
        RangeModule h = new RangeModule();
        h.removeRange(0, 5);
        check("remove on empty", snapshot(h), "{}");

        // Remove that starts exactly at an interval start (the start == left
        // case from T2: the degenerate (left, left) write is swept away).
        RangeModule i = new RangeModule();
        i.addRange(1, 5);
        i.removeRange(1, 3);
        check("remove from start", snapshot(i), "{3=5}");

        // Remove that ends exactly at an interval end.
        RangeModule j = new RangeModule();
        j.addRange(1, 5);
        j.removeRange(3, 5);
        check("remove to end", snapshot(j), "{1=3}");

        // queryRange straddling a gap -> false.
        RangeModule k = new RangeModule();
        k.addRange(1, 3);
        k.addRange(5, 7);
        check("query across gap", k.queryRange(2, 6), false);

        // queryRange exactly at boundary [left=3, right=5) over [1,3),[5,7) is empty
        // and the convention is that an empty range is not "fully covered" by
        // anything since floorKey(3) = 1 and 3 >= 5 is false. So expect false.
        check("query in gap", k.queryRange(3, 5), false);

        // queryRange equal to a stored interval -> true.
        check("query equals interval", k.queryRange(1, 3), true);

        // Sequence stress: alternating add and remove.
        // Trace:
        //   addRange(1, 100)        -> {1=100}
        //   removeRange(20, 30)     -> {1=20, 30=100}
        //   removeRange(50, 60)     -> {1=20, 30=50, 60=100}
        //   addRange(25, 55)        -> [25,55) absorbs [30,50); [60,100) stays
        //                              separate because 55 != 60 (no touch)
        //                              -> {1=20, 25=55, 60=100}
        RangeModule l = new RangeModule();
        l.addRange(1, 100);
        l.removeRange(20, 30);
        l.removeRange(50, 60);
        l.addRange(25, 55);
        check("alt seq state",     snapshot(l),               "{1=20, 25=55, 60=100}");
        check("alt seq q1",        l.queryRange(1, 20),       true);
        check("alt seq q2 covered",l.queryRange(25, 55),      true);
        check("alt seq q2 gap",    l.queryRange(25, 60),      false);
        check("alt seq q3 gap",    l.queryRange(20, 25),      false);
    }

    /* ----------------------------- helpers ----------------------------- */

    private static String snapshot(RangeModule rm) {
        return rm.map.toString();
    }

    private static void check(String label, Object got, Object expected) {
        boolean ok = String.valueOf(got).equals(String.valueOf(expected));
        System.out.println((ok ? "OK    " : "FAIL  ") + label
                + " got=" + got + (ok ? "" : " expected=" + expected));
    }
}
