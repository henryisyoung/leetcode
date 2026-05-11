package waymo;
/*
Minimum Index Distance Between 1 and 2

Given an array nums of length n where each element is in {0, 1, 2}, return
the minimum absolute index distance between any pair (i, j) such that
nums[i] = 1 AND nums[j] = 2.

Formally:   min |i - j|   over   nums[i]=1, nums[j]=2.
If there is no 1 OR no 2 in the array, return -1.

Constraints
  1 <= n <= 2*10^5

Stdin format
  Line 1: n
  Line 2: n integers separated by spaces
  Output: a single integer

Examples
  [0,1,0,2,2] -> 2
  [0,0,1,0]   -> -1
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

/*
Algorithm: single linear pass with two trackers.

  Walk left-to-right keeping the most recent indices we have seen:
      last1 = max i' <= i  where nums[i'] = 1, or -1 if none yet
      last2 = max j' <= i  where nums[j'] = 2, or -1 if none yet

  At index i:
    - If nums[i] = 1:  update last1 = i.  If last2 >= 0, candidate = i - last2.
    - If nums[i] = 2:  update last2 = i.  If last1 >= 0, candidate = i - last1.
  Track the running min over candidates.

  Why "previous of the other type" is always optimal:
    For any pair (i, j) with i < j, nums[i] = 1, nums[j] = 2, the gap j - i
    is minimised when i is the LARGEST 1-index ≤ j (any smaller i can only
    grow the gap).  Symmetric reasoning applies if nums[i] = 2 < j with
    nums[j] = 1.  So checking only the "most recent opposite" at each step
    covers every minimal pair.

Complexity
  Time:  O(n)
  Memory: O(1)
*/
public class MinIndexDistanceOneTwo {

    /** Returns the minimum |i - j| with nums[i]=1 and nums[j]=2, or -1 if no such pair. */
    public int minDistance(int[] nums) {
        if (nums == null || nums.length == 0) return -1;
        int last1 = -1, last2 = -1;
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int v = nums[i];
            if (v == 1) {
                last1 = i;
                if (last2 >= 0) {
                    int d = i - last2;
                    if (d < best) best = d;
                }
            } else if (v == 2) {
                last2 = i;
                if (last1 >= 0) {
                    int d = i - last1;
                    if (d < best) best = d;
                }
            }
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    /* --------------------------- Brute reference --------------------------- */

    /** O(n^2) brute force, used for cross-checks on small inputs. */
    int minDistanceBrute(int[] nums) {
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 1) continue;
            for (int j = 0; j < nums.length; j++) {
                if (nums[j] != 2) continue;
                int d = Math.abs(i - j);
                if (d < best) best = d;
            }
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    /* --------------------------- IO --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try {
            return System.in.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());
        int[] nums = new int[n];
        StringTokenizer st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) nums[i] = Integer.parseInt(st.nextToken());
        System.out.println(new MinIndexDistanceOneTwo().minDistance(nums));
    }

    /* --------------------------- Demo + tests --------------------------- */

    private static void runDemos() {
        MinIndexDistanceOneTwo solver = new MinIndexDistanceOneTwo();

        // Spec examples.
        check(solver, new int[]{0, 1, 0, 2, 2}, 2);
        check(solver, new int[]{0, 0, 1, 0}, -1);

        // No 2 at all.
        check(solver, new int[]{1, 1, 1}, -1);
        // No 1 at all.
        check(solver, new int[]{2, 2, 2}, -1);
        // Neither 1 nor 2.
        check(solver, new int[]{0, 0, 0}, -1);

        // Adjacent 1 and 2 → distance 1.
        check(solver, new int[]{1, 2}, 1);
        check(solver, new int[]{2, 1}, 1);

        // Multiple 1s and 2s — pick the closest pair anywhere in the array.
        //   1's at {0, 5}, 2's at {3, 7}.  Pairs: |0-3|=3, |0-7|=7, |5-3|=2, |5-7|=2.  min = 2.
        check(solver, new int[]{1, 0, 0, 2, 0, 1, 0, 2}, 2);

        // Closest pair where 2 comes before 1.
        check(solver, new int[]{0, 0, 2, 0, 0, 1, 0, 0}, 3);     // |5-2| = 3.

        // Length 1 — impossible to form a pair.
        check(solver, new int[]{1}, -1);
        check(solver, new int[]{2}, -1);

        // Same-position pair is NEVER possible (an element can't be both 1 and 2),
        // so 1 immediately next to 2 is the tightest possible bound.
        check(solver, new int[]{0, 1, 2, 0}, 1);

        // Cross-check on 1000 random small arrays.
        Random rnd = new Random(42);
        int mismatches = 0;
        for (int t = 0; t < 1000; t++) {
            int n = 1 + rnd.nextInt(20);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(3);
            int a = solver.minDistance(nums);
            int b = solver.minDistanceBrute(nums);
            if (a != b) {
                mismatches++;
                System.out.println("MISMATCH on " + Arrays.toString(nums) + " scan=" + a + " brute=" + b);
            }
        }
        System.out.println("Random cross-check: " + (1000 - mismatches) + "/1000 ok");

        // Performance: n = 200K.
        int N = 200_000;
        Random big = new Random(3);
        int[] big1 = new int[N];
        for (int i = 0; i < N; i++) big1[i] = big.nextInt(3);
        long t0 = System.nanoTime();
        int ans = solver.minDistance(big1);
        long us = (System.nanoTime() - t0) / 1_000;
        System.out.println("Stress n=200K: ans=" + ans + " in " + us + " µs");
    }

    private static void check(MinIndexDistanceOneTwo solver, int[] nums, int expected) {
        int got = solver.minDistance(nums);
        int brute = solver.minDistanceBrute(nums);
        boolean ok = got == expected && brute == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "expected=" + expected + " scan=" + got + " brute=" + brute
                + "  nums=" + Arrays.toString(nums));
    }
}
