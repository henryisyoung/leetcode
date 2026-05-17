package waymo;
/*
LeetCode 360. Sort Transformed Array.

Given a sorted (ascending) integer array `nums` and the three integer
coefficients (a, b, c) of the quadratic function

      f(x) = a * x^2 + b * x + c

return a sorted (ascending) array containing f(nums[i]) for every i.

Watch out: a may be NEGATIVE — that flips the parabola upside-down and
means the largest values land in the middle of the input, not at the
ends.

Examples
  nums=[-4,-2,2,4], a=1, b=3, c=5    -> [ 3,  9, 15, 33]
  nums=[-4,-2,2,4], a=-1, b=3, c=5   -> [-23, -5, 1, 7]
  nums=[1,2,3,4,5], a=0, b=-1, c=0   -> [-5,-4,-3,-2,-1]
  nums=[1,2,3,4,5], a=0, b=0, c=42   -> [42,42,42,42,42]

Constraints (LC 360)
  1 <= n <= 200
  -100 <= nums[i], a, b, c <= 100
  nums is sorted ascending.
 */

import java.util.Arrays;
import java.util.Random;

/*
Algorithm: O(n) two-pointer "merge from the parabola's open end".

  Key observation:
    * If a > 0 the parabola is U-shaped — the LARGEST values of f are at
      the two endpoints of nums (whichever endpoint is farther from the
      vertex), the smallest in the middle.
    * If a < 0 the parabola is upside-down — the SMALLEST values of f
      are at the endpoints, the largest in the middle.
    * If a == 0 the function is linear; both directions still work
      (whichever pointer's f is "more extreme" is correct to consume).

  So:
    * a >= 0  ->  fill RESULT FROM THE END, picking the LARGER of
                  f(nums[left]) and f(nums[right]) and advancing that
                  pointer inward.
    * a <  0  ->  fill RESULT FROM THE START, picking the SMALLER of
                  f(nums[left]) and f(nums[right]) and advancing that
                  pointer inward.

  After the two pointers cross we've placed all n values in the right
  order in O(n) time and O(1) extra memory beyond the result.

  Why a == 0 is fine in the a >= 0 branch:
    For linear f the two endpoints' f-values are the two extremes; the
    "pick larger from end" rule degenerates into "always pick the
    monotonically-larger end first" which produces a correctly-sorted
    result regardless of the slope's sign.

Complexity
  Time:  O(n)
  Space: O(1) extra (the returned array doesn't count).

  Cross-checked in tests against an O(n log n) baseline that just
  computes f for every element and sorts.
*/
public class SortTransformedArray {

    /** O(n) two-pointer solution. */
    public int[] sortTransformedArray(int[] nums, int a, int b, int c) {
        if (nums == null) throw new IllegalArgumentException("nums is null");
        int n = nums.length;
        int[] res = new int[n];
        int left = 0, right = n - 1;

        if (a >= 0) {
            // Fill from the end with the larger of f(left), f(right).
            int idx = n - 1;
            while (left <= right) {
                long fL = f(nums[left], a, b, c);
                long fR = f(nums[right], a, b, c);
                if (fL >= fR) {
                    res[idx--] = (int) fL;
                    left++;
                } else {
                    res[idx--] = (int) fR;
                    right--;
                }
            }
        } else {
            // Fill from the start with the smaller of f(left), f(right).
            int idx = 0;
            while (left <= right) {
                long fL = f(nums[left], a, b, c);
                long fR = f(nums[right], a, b, c);
                if (fL <= fR) {
                    res[idx++] = (int) fL;
                    left++;
                } else {
                    res[idx++] = (int) fR;
                    right--;
                }
            }
        }
        return res;
    }

    /** Long-arithmetic guard against intermediate overflow on big coefficients. */
    private static long f(long x, int a, int b, int c) {
        return a * x * x + b * x + c;
    }

    /* --------------------------- Naive baseline (tests) --------------------------- */

    /** Compute f for every element, then sort.  O(n log n).  Used to cross-check. */
    int[] sortTransformedArrayNaive(int[] nums, int a, int b, int c) {
        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) res[i] = (int) f(nums[i], a, b, c);
        Arrays.sort(res);
        return res;
    }

    /* --------------------------- Demo + tests --------------------------- */

    public static void main(String[] args) {
        SortTransformedArray solver = new SortTransformedArray();

        // a > 0: U-shape, ends largest.
        check(solver, new int[]{-4, -2, 2, 4}, 1, 3, 5,
                new int[]{3, 9, 15, 33});

        // a < 0: upside-down, ends smallest.
        check(solver, new int[]{-4, -2, 2, 4}, -1, 3, 5,
                new int[]{-23, -5, 1, 7});

        // a == 0, b > 0: linear increasing.
        check(solver, new int[]{1, 2, 3, 4, 5}, 0, 1, 0,
                new int[]{1, 2, 3, 4, 5});

        // a == 0, b < 0: linear decreasing.
        check(solver, new int[]{1, 2, 3, 4, 5}, 0, -1, 0,
                new int[]{-5, -4, -3, -2, -1});

        // a == 0, b == 0: constant.
        check(solver, new int[]{1, 2, 3, 4, 5}, 0, 0, 42,
                new int[]{42, 42, 42, 42, 42});

        // a > 0, vertex inside the input -> smallest values come from the middle.
        check(solver, new int[]{-3, -1, 0, 2}, 1, 0, 0,
                new int[]{0, 1, 4, 9});

        // a < 0, vertex inside the input -> largest values come from the middle.
        // f(x) = -x^2 over [-3,-1,0,2] -> [-9,-1,0,-4] -> sorted [-9,-4,-1,0]
        check(solver, new int[]{-3, -1, 0, 2}, -1, 0, 0,
                new int[]{-9, -4, -1, 0});

        // Single element.
        check(solver, new int[]{5}, 2, 3, 4, new int[]{2 * 25 + 15 + 4});

        // Empty array.
        check(solver, new int[]{}, 1, 2, 3, new int[]{});

        // Large coefficients near the LeetCode bound.
        check(solver, new int[]{-100, -50, 0, 50, 100}, 100, -100, -100,
                solver.sortTransformedArrayNaive(
                        new int[]{-100, -50, 0, 50, 100}, 100, -100, -100));

        /* ---------- Random fuzz vs the naive baseline ---------- */
        Random rnd = new Random(42);
        int fails = 0;
        for (int trial = 0; trial < 1000; trial++) {
            int n = rnd.nextInt(20);              // 0..19 length
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = -50 + rnd.nextInt(101);
            Arrays.sort(nums);                     // sorted input is required
            int a = -10 + rnd.nextInt(21);         // -10..10 (often hits a==0)
            int b = -10 + rnd.nextInt(21);
            int c = -10 + rnd.nextInt(21);

            int[] got = solver.sortTransformedArray(nums.clone(), a, b, c);
            int[] want = solver.sortTransformedArrayNaive(nums.clone(), a, b, c);
            if (!Arrays.equals(got, want)) {
                fails++;
                System.out.println("MISMATCH a=" + a + " b=" + b + " c=" + c
                        + " nums=" + Arrays.toString(nums)
                        + " got=" + Arrays.toString(got)
                        + " want=" + Arrays.toString(want));
            }
        }
        System.out.println("Random fuzz vs naive: " + (1000 - fails) + "/1000 ok");
    }

    private static void check(SortTransformedArray solver,
                              int[] nums, int a, int b, int c, int[] expected) {
        int[] got = solver.sortTransformedArray(nums.clone(), a, b, c);
        boolean ok = Arrays.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ")
                + "f(x)=" + a + "x^2 + " + b + "x + " + c
                + "  nums=" + Arrays.toString(nums)
                + "  expected=" + Arrays.toString(expected)
                + "  got=" + Arrays.toString(got));
    }
}
