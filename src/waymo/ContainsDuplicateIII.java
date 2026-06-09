package waymo;
/*
LeetCode 220: Contains Duplicate III  (bucket-sort solution).

Given an integer array `nums` and two integers `indexDiff` (k) and
`valueDiff` (t), return true iff there exist two DISTINCT indices i, j with:
    |i - j| <= k          (close in position)
    |nums[i] - nums[j]| <= t   (close in value)

Examples
  nums=[1,2,3,1], k=3, t=0  -> true   (the two 1s, indices 0 and 3, |0-3|<=3)
  nums=[1,5,9,1,5,9], k=2, t=3 -> false

Two standard solutions
  A) TreeSet sliding window: keep a window of the last k values in a balanced
     BST; for each new value probe floor/ceiling to see if a neighbor is within
     t.  O(n log k) time.
  B) BUCKETING (this file): O(n) time.  The trick below.

Bucket idea -- map each value to a bucket of WIDTH (t + 1)
  Put value v into bucket id  = floor(v / (t + 1)).
  Why width t+1?  Any two values that land in the SAME bucket differ by at
  most t (the bucket spans exactly t+1 consecutive integers: [id*(t+1) ..
  id*(t+1) + t]).  So a same-bucket collision is an instant "true".

  Two values within t can also straddle ADJACENT buckets (e.g. t=3, values
  3 and 4 -> buckets 0 and 1).  So we additionally check the neighbor buckets
  id-1 and id+1, but there we must verify |v - other| <= t explicitly because
  adjacent buckets can also hold pairs that differ by MORE than t.

  Maintain only the last k indices: each bucket holds at most ONE value
  (if a second value wanted the same bucket, we'd already have returned true),
  and we evict the value that falls out of the window (index i-k).

Why bucket width t+1 and not t
  Width t would make a bucket span t+1 integers too only if we used inclusive
  off-by-one math; the clean invariant "same bucket => diff <= t" needs the
  divisor to be t+1.  With divisor t, values 0 and t would land in buckets 0
  and 1 despite differing by exactly t (a valid pair) -- you'd miss it unless
  you also probed neighbors, but then the "same bucket => answer" guarantee
  breaks.  t+1 keeps the guarantee crisp.

Negative numbers
  Java integer division truncates toward zero, so -1 / 4 == 0, same bucket as
  0 -- WRONG (we'd want -1 in bucket -1).  Fix: use Math.floorDiv, which floors
  toward negative infinity, giving a correct, monotonic bucket id for negatives.

Overflow
  nums[i] can be up to ~2e9 in magnitude across the int range and t up to 2^31-1.
  Use `long` for the value and the divisor (t + 1L) so (t + 1) doesn't overflow
  when t == Integer.MAX_VALUE, and so floorDiv operates on longs.

Complexity
  Time:  O(n)   one pass, O(1) bucket lookups.
  Space: O(min(n, k))   at most k+1 buckets alive at once.
*/

import java.util.HashMap;
import java.util.Map;

public class ContainsDuplicateIII {

    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        // t < 0 can never be satisfied (|diff| >= 0 > t); k < 1 means no valid pair.
        if (nums == null || nums.length < 2 || k < 1 || t < 0) return false;

        long width = (long) t + 1;          // bucket spans t+1 integers -> same bucket => diff <= t
        Map<Long, Long> bucket = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            long v = nums[i];
            long id = Math.floorDiv(v, width);   // floor division so negatives bucket correctly

            // Same bucket -> guaranteed within t.
            if (bucket.containsKey(id)) return true;

            // Adjacent buckets -> possible, must verify the actual gap.
            Long lo = bucket.get(id - 1);
            if (lo != null && v - lo <= t) return true;
            Long hi = bucket.get(id + 1);
            if (hi != null && hi - v <= t) return true;

            bucket.put(id, v);

            // Evict the value that just left the size-k index window.
            if (i >= k) {
                long oldId = Math.floorDiv((long) nums[i - k], width);
                bucket.remove(oldId);
            }
        }
        return false;
    }

    /* --------------------------- brute-force oracle --------------------------- */

    static boolean brute(int[] nums, int k, int t) {
        if (nums == null || t < 0 || k < 1) return false;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length && j <= i + k; j++) {
                if (Math.abs((long) nums[i] - nums[j]) <= t) return true;
            }
        }
        return false;
    }

    /* --------------------------- tests --------------------------- */

    public static void main(String[] args) {
        ContainsDuplicateIII s = new ContainsDuplicateIII();

        check("LC ex1", s.containsNearbyAlmostDuplicate(new int[]{1,2,3,1}, 3, 0), true);
        check("LC ex2", s.containsNearbyAlmostDuplicate(new int[]{1,5,9,1,5,9}, 2, 3), false);

        check("t=0 exact dup in window",
                s.containsNearbyAlmostDuplicate(new int[]{1,0,1,1}, 1, 0), true);
        check("k too small to reach dup",
                s.containsNearbyAlmostDuplicate(new int[]{1,2}, 0, 3), false);  // k<1 -> false
        check("adjacent-bucket pair",
                s.containsNearbyAlmostDuplicate(new int[]{4,1}, 1, 3), true);   // |4-1|=3<=3
        check("negative values",
                s.containsNearbyAlmostDuplicate(new int[]{-1,-1}, 1, 0), true);
        check("negative straddle zero",
                s.containsNearbyAlmostDuplicate(new int[]{-3, 3}, 1, 6), true); // |−3−3|=6<=6
        check("big values no overflow",
                s.containsNearbyAlmostDuplicate(
                        new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}, 1, Integer.MAX_VALUE),
                false);                                                          // gap > t
        check("single element",
                s.containsNearbyAlmostDuplicate(new int[]{5}, 1, 0), false);

        // Cross-check against brute force on random inputs.
        java.util.Random rng = new java.util.Random(42);
        int fails = 0, trials = 2000;
        for (int tr = 0; tr < trials; tr++) {
            int n = 1 + rng.nextInt(8);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rng.nextInt(21) - 10;   // -10..10
            int k = rng.nextInt(5);          // 0..4
            int t = rng.nextInt(6);          // 0..5
            boolean fast = s.containsNearbyAlmostDuplicate(a, k, t);
            boolean slow = brute(a, k, t);
            if (fast != slow) {
                fails++;
                if (fails <= 3) {
                    System.out.println("MISMATCH a=" + java.util.Arrays.toString(a)
                            + " k=" + k + " t=" + t + " fast=" + fast + " slow=" + slow);
                }
            }
        }
        System.out.println((fails == 0 ? "OK   " : "FAIL ")
                + "stress (" + trials + " random cases, fails=" + fails + ")");
    }

    private static void check(String label, boolean got, boolean expected) {
        System.out.println((got == expected ? "OK    " : "FAIL  ") + label
                + " got=" + got + (got == expected ? "" : " expected=" + expected));
    }
}
