package waymo;

import java.util.Arrays;

/*
LC 164. Maximum Gap.

  Given an unsorted array nums, return the maximum gap between any two
  successive elements in its SORTED form. Must run in O(n) time AND
  O(n) space.

  The "sort then scan" answer is O(n log n) — disqualified.


  --------------------------------------------------------------------
  What is bucket sort?
  --------------------------------------------------------------------
  A non-comparison sort that distributes elements into k "buckets" by
  VALUE, then sorts each bucket (typically with a simple comparison
  sort like insertion sort), then concatenates the buckets in order.

  General recipe:
    1. Find the value range [min, max].
    2. Choose bucket count k and width w = (max - min) / k.
    3. Drop each element x into bucket ⌊(x - min) / w⌋ — O(1) per item.
    4. Sort within each bucket.
    5. Concatenate.

  Why it can beat O(n log n):
    Comparison sorts have a proven Ω(n log n) lower bound. Bucket sort
    sidesteps that by using the VALUE to pick a bucket directly —
    that's an O(1) operation per element, not a comparison.

  When it shines:
    - Values are in a bounded, known range.
    - Distribution is roughly uniform (so no bucket gets all n items
      and degrades to O(n²) inside).

  When it doesn't:
    - Highly skewed input (one bucket holds everything).
    - You don't know the range up front.


  --------------------------------------------------------------------
  Why bucket sort solves Max Gap in O(n) — the pigeonhole argument
  --------------------------------------------------------------------
  For n elements spanning [min, max] there are exactly n - 1 gaps in
  the sorted array. By pigeonhole, the LARGEST gap is at least the
  average gap:

      maxGap >= ⌈(max - min) / (n - 1)⌉

  So if we pick bucket width w = ⌊(max - min) / (n - 1)⌋, then:
    - Any two items inside the same bucket differ by < w  ≤  maxGap.
    - Therefore the max gap CANNOT lie inside any single bucket.
    - It MUST occur between two consecutive non-empty buckets,
      specifically as  (next bucket's min) − (prev bucket's max).

  This means we never sort within a bucket — we only track each
  bucket's MIN and MAX. Two ints per bucket, linear scan, done.


  --------------------------------------------------------------------
  Complexity
  --------------------------------------------------------------------
  Time:   O(n)           — one pass to find min/max, one to distribute,
                          one to scan buckets (k ≤ n + 1).
  Memory: O(n)           — 2 ints per bucket, k ≤ n + 1 buckets.
*/
public class MaximumGap {

    public int maximumGap(int[] nums) {
        if (nums == null || nums.length < 2) return 0;
        int n = nums.length;

        int min = nums[0], max = nums[0];
        for (int v : nums) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        if (max == min) return 0;       // all equal → gap 0

        // Width chosen so maxGap can't fit inside one bucket (see pigeonhole).
        int bucketSize  = Math.max(1, (max - min) / (n - 1));
        int bucketCount = (max - min) / bucketSize + 1;

        int[] bucketMin = new int[bucketCount];
        int[] bucketMax = new int[bucketCount];
        Arrays.fill(bucketMin, Integer.MAX_VALUE);
        Arrays.fill(bucketMax, Integer.MIN_VALUE);

        for (int v : nums) {
            int i = (v - min) / bucketSize;
            if (v < bucketMin[i]) bucketMin[i] = v;
            if (v > bucketMax[i]) bucketMax[i] = v;
        }

        // Max gap = max over consecutive non-empty buckets of (curr.min - prev.max).
        // First non-empty bucket holds `min` itself, so seeding prevMax = min is safe.
        int gap = 0, prevMax = min;
        for (int i = 0; i < bucketCount; i++) {
            if (bucketMin[i] == Integer.MAX_VALUE) continue;   // empty
            gap = Math.max(gap, bucketMin[i] - prevMax);
            prevMax = bucketMax[i];
        }
        return gap;
    }

    /* --- demo --- */
    public static void main(String[] args) {
        MaximumGap s = new MaximumGap();
        System.out.println(s.maximumGap(new int[]{3, 6, 9, 1}));            // 3
        System.out.println(s.maximumGap(new int[]{10}));                    // 0
        System.out.println(s.maximumGap(new int[]{5, 5, 5}));               // 0
        System.out.println(s.maximumGap(new int[]{1, 10_000_000}));         // 9_999_999
        System.out.println(s.maximumGap(new int[]{1, 1, 1, 1, 5, 5, 5, 5}));// 4
        System.out.println(s.maximumGap(new int[]{100, 3, 2, 1}));          // 97
    }
}
