package waymo;
/*
Problem: Jump Game Variant (Reachability / Backtracking)

Given an integer array nums of length n, you start at index 0.
  - nums[i] is the maximum jump length from index i to the right.
  - From i, you may jump any distance in 1..nums[i].

Task: determine whether you can reach the last index n-1.

I/O
  Input:  one line integer array nums
  Output: true or false

Constraints
  1 <= n      <= 2 * 10^5
  0 <= nums[i] <= 10^9     (so i + nums[i] can overflow 32-bit int)

Examples
  [2,3,1,1,4] -> true
  [3,2,1,0,4] -> false
  [0]         -> true
  [1,0,1,0]   -> false
  [2,0,0]     -> true
 */

import java.util.Arrays;
import java.util.Random;

/*
Algorithm (greedy, O(n) time, O(1) memory):

  Sweep left-to-right and keep a running "farthest reachable index" so far.
  Process index i only if i ≤ farthest (otherwise nothing has been able to
  reach i yet, and since the sweep is monotone, nothing ever will).  At each
  reachable i, relax  farthest = max(farthest, i + nums[i]).  Stop as soon
  as farthest covers n-1.

Correctness sketch:
  Let R = { i : i is reachable from 0 }.  After processing every j < i with
  j ∈ R, `farthest` equals  max_{j ∈ R, j < i} (j + nums[j]).
  - If i > farthest, no reachable predecessor can jump to i, so i ∉ R and
    no later index can be reached either (R is contiguous prefix of [0..]).
  - If i ≤ farthest, then i ∈ R, so we now allow i to relax farthest.

Why we need long-arithmetic:
  nums[i] can be 10^9 and i up to 2·10^5, so i + nums[i] fits comfortably in
  long but not necessarily int (the sum is ≤ 10^9 + 2·10^5 ~ 1e9, which DOES
  fit in int, but clamping to n-1 anyway keeps the arithmetic obviously safe
  and avoids any maintenance surprise if constraints change).

Complexity:
  Time:    O(n).
  Memory:  O(1).
*/
public class JumpGameReachability {

    /** Greedy reachability.  Returns true iff index n-1 is reachable from index 0. */
    public boolean canJump(int[] nums) {
        if (nums == null || nums.length == 0) return false;
        int n = nums.length;
        int farthest = 0;
        for (int i = 0; i < n; i++) {
            if (i > farthest) return false;          // stuck — index i is past the reachable horizon
            // Cast to long so 2·10^5 + 10^9 always fits even if constraints grow.
            long reach = (long) i + (long) nums[i];
            if (reach > farthest) farthest = (int) Math.min(reach, n - 1L);
            if (farthest >= n - 1) return true;
        }
        return true;
    }

    /* --------------------------- Alternate solutions for cross-checks --------------------------- */

    /** DP variant: reachable[i] = true iff i is reachable from 0.  Same O(n) time and answer. */
    boolean canJumpDp(int[] nums) {
        if (nums == null || nums.length == 0) return false;
        int n = nums.length;
        boolean[] reachable = new boolean[n];
        reachable[0] = true;
        int farthest = 0;
        for (int i = 0; i < n; i++) {
            if (!reachable[i]) break;                 // contiguous reachable prefix
            long reach = Math.min((long) i + nums[i], (long) (n - 1));
            for (int j = farthest + 1; j <= reach; j++) reachable[j] = true;
            farthest = Math.max(farthest, (int) reach);
        }
        return reachable[n - 1];
    }

    /**
     * Backtracking with memoization (the "reachability / backtracking" variant
     * called out in the problem title).  O(n^2) time, O(n) memory.  Cross-checks
     * the greedy answer on small inputs.
     */
    boolean canJumpBacktrack(int[] nums) {
        if (nums == null || nums.length == 0) return false;
        // 0 = unknown, 1 = reachable-to-end, -1 = NOT reachable-to-end.
        int[] memo = new int[nums.length];
        return dfs(nums, 0, memo);
    }

    private boolean dfs(int[] nums, int i, int[] memo) {
        int n = nums.length;
        if (i >= n - 1) return true;
        if (memo[i] != 0) return memo[i] == 1;
        int furthest = (int) Math.min((long) i + nums[i], (long) (n - 1));
        for (int j = furthest; j > i; j--) {           // try big jumps first — they usually win
            if (dfs(nums, j, memo)) {
                memo[i] = 1;
                return true;
            }
        }
        memo[i] = -1;
        return false;
    }

    /* --------------------------- Demo + cross-check --------------------------- */

    public static void main(String[] args) {
        JumpGameReachability solver = new JumpGameReachability();

        check(solver, new int[]{2, 3, 1, 1, 4}, true);
        check(solver, new int[]{3, 2, 1, 0, 4}, false);
        check(solver, new int[]{0}, true);
        check(solver, new int[]{1, 0, 1, 0}, false);
        check(solver, new int[]{2, 0, 0}, true);

        // Additional edge cases.
        check(solver, new int[]{1}, true);                     // single-element
        check(solver, new int[]{5}, true);                     // jump beyond end allowed
        check(solver, new int[]{1, 1, 1, 1, 1}, true);         // exact-1 each step
        check(solver, new int[]{0, 1}, false);                 // stuck at index 0
        check(solver, new int[]{1, 0, 0}, false);              // jump 1, then stuck
        check(solver, new int[]{2, 5, 0, 0}, true);            // big jump from index 1
        check(solver, new int[]{1_000_000_000, 0, 0, 0, 0}, true); // huge nums[0] — no overflow

        // Cross-check the three implementations on 1000 random small inputs.
        Random rnd = new Random(13);
        int mismatches = 0;
        for (int t = 0; t < 1000; t++) {
            int n = 1 + rnd.nextInt(15);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(5); // 0..4, lots of zeros
            boolean a = solver.canJump(nums);
            boolean b = solver.canJumpDp(nums);
            boolean c = solver.canJumpBacktrack(nums);
            if (a != b || a != c) {
                mismatches++;
                System.out.println("MISMATCH greedy=" + a + " dp=" + b + " bt=" + c
                        + " on " + Arrays.toString(nums));
            }
        }
        System.out.println("Random cross-check: " + (1000 - mismatches) + "/1000 ok");

        // Performance: n = 200_000.
        int n = 200_000;
        Random big = new Random(7);
        int[] bigNums = new int[n];
        for (int i = 0; i < n; i++) bigNums[i] = big.nextInt(3);   // mostly 0/1/2 — sometimes blocked
        long t0 = System.nanoTime();
        boolean ans = solver.canJump(bigNums);
        long us = (System.nanoTime() - t0) / 1_000;
        System.out.println("Stress n=200K random nums[i]∈[0,3): ans=" + ans + " in " + us + " µs");

        // Always-reachable big input.
        for (int i = 0; i < n; i++) bigNums[i] = 1;
        t0 = System.nanoTime();
        ans = solver.canJump(bigNums);
        us = (System.nanoTime() - t0) / 1_000;
        System.out.println("Stress n=200K all-ones:           ans=" + ans + " in " + us + " µs");
    }

    private static void check(JumpGameReachability solver, int[] nums, boolean expected) {
        boolean greedy = solver.canJump(nums);
        boolean dp = solver.canJumpDp(nums);
        boolean bt = nums.length <= 30 ? solver.canJumpBacktrack(nums) : greedy;
        boolean ok = greedy == expected && dp == expected && bt == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "expected=" + expected + " greedy=" + greedy + " dp=" + dp + " bt=" + bt
                + "  nums=" + Arrays.toString(nums));
    }
}
