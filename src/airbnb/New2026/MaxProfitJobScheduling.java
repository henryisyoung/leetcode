package airbnb.New2026;
/*
Maximum Profit in Job Scheduling  (LeetCode 1235).

Given n jobs, each with startTime[i], endTime[i], profit[i], choose a
subset of NON-OVERLAPPING jobs that maximizes total profit. Two jobs may
touch at an endpoint: a job ending at time t and another starting at t do
NOT overlap (half-open intervals [start, end)).

I/O
  Input : int[] startTime, int[] endTime, int[] profit  (parallel arrays)
  Output: int  — maximum achievable profit

Constraints (LC)
  1 <= n <= 5e4
  1 <= start < end <= 1e9
  1 <= profit <= 1e4   (sum fits in int, but we use long defensively)

Examples
  start=[1,2,3,3], end=[3,4,5,6], profit=[50,10,40,70] -> 120  (jobs 1 & 4)
  start=[1,2,3,4,6], end=[3,5,10,6,9], profit=[20,20,100,70,60] -> 150
  start=[1,1,1], end=[2,3,4], profit=[5,6,4] -> 6

Why this is NOT JobSequencingWithDeadlines
  That problem is unit-time tasks with deadlines (greedy + min-heap). This
  is weighted interval scheduling: arbitrary [start, end) intervals with
  profits, solved by sort-by-end DP + binary search.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
Algorithm — sort by end time, DP with binary search.

  Sort jobs ascending by END time. Let dp[i] = the best profit obtainable
  using only the first i jobs (in end-sorted order).

  For job i-1 (0-based) with (start s, profit p):
     either SKIP it          -> dp[i-1]
     or     TAKE it           -> p + dp[k]
  where k = number of jobs whose END <= s. Because the array is sorted by
  end, those jobs form a PREFIX, so dp[k] is exactly "best profit from all
  jobs that finish by the time this job starts" — no overlap possible.

     dp[i] = max(dp[i-1], p + dp[k])

  k is found by binary search (upper bound of s in the end-time array).

Complexity
  Time:   O(n log n)  — sort + a binary search per job
  Memory: O(n)        — dp array + sorted jobs
*/
public class MaxProfitJobScheduling {

    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        if (n == 0) return 0;

        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i][0] = startTime[i];
            jobs[i][1] = endTime[i];
            jobs[i][2] = profit[i];
        }
        Arrays.sort(jobs, (a, b) -> Integer.compare(a[1], b[1]));   // by end ascending

        int[] ends = new int[n];
        for (int i = 0; i < n; i++) ends[i] = jobs[i][1];

        long[] dp = new long[n + 1];                                // dp[i] over first i jobs
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            int s = jobs[i - 1][0];
            long p = jobs[i - 1][2];
            int k = countEndsAtMost(ends, s);                       // jobs finishing by s (a prefix)
            dp[i] = Math.max(dp[i - 1], p + dp[k]);
        }
        return (int) dp[n];
    }

    /** Number of elements in `ends` that are <= target (array sorted ascending). */
    private static int countEndsAtMost(int[] ends, int target) {
        int lo = 0, hi = ends.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (ends[mid] <= target) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    /* --------------------------- brute force (reference oracle) --------------------------- */

    /** Try every subset; keep the max-profit subset with no overlapping pair. Small n only. */
    long jobSchedulingBrute(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        long best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (compatible(mask, startTime, endTime)) {
                long sum = 0;
                for (int i = 0; i < n; i++) if ((mask & (1 << i)) != 0) sum += profit[i];
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    private static boolean compatible(int mask, int[] s, int[] e) {
        List<int[]> chosen = new ArrayList<>();
        for (int i = 0; i < s.length; i++) {
            if ((mask & (1 << i)) != 0) chosen.add(new int[]{s[i], e[i]});
        }
        chosen.sort((a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 1; i < chosen.size(); i++) {
            if (chosen.get(i)[0] < chosen.get(i - 1)[1]) return false;   // overlap
        }
        return true;
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        MaxProfitJobScheduling solver = new MaxProfitJobScheduling();

        check(solver, new int[]{1, 2, 3, 3}, new int[]{3, 4, 5, 6}, new int[]{50, 10, 40, 70}, 120);
        check(solver, new int[]{1, 2, 3, 4, 6}, new int[]{3, 5, 10, 6, 9}, new int[]{20, 20, 100, 70, 60}, 150);
        check(solver, new int[]{1, 1, 1}, new int[]{2, 3, 4}, new int[]{5, 6, 4}, 6);

        // Single job.
        check(solver, new int[]{5}, new int[]{9}, new int[]{7}, 7);

        // All jobs overlap -> pick the single most profitable.
        check(solver, new int[]{1, 1, 1}, new int[]{10, 10, 10}, new int[]{3, 9, 5}, 9);

        // Perfectly chained (touching endpoints) -> take all.
        check(solver, new int[]{1, 2, 3}, new int[]{2, 3, 4}, new int[]{1, 2, 3}, 6);

        // Random fuzz against O(2^n) brute force.
        Random rnd = new Random(2026);
        int trials = 500, fails = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + rnd.nextInt(12);
            int[] s = new int[n], e = new int[n], p = new int[n];
            for (int i = 0; i < n; i++) {
                int start = rnd.nextInt(15);
                s[i] = start;
                e[i] = start + 1 + rnd.nextInt(8);
                p[i] = 1 + rnd.nextInt(50);
            }
            long fast  = solver.jobScheduling(s, e, p);
            long brute = solver.jobSchedulingBrute(s, e, p);
            if (fast != brute) {
                fails++;
                System.out.println("MISMATCH fast=" + fast + " brute=" + brute
                        + " s=" + Arrays.toString(s) + " e=" + Arrays.toString(e)
                        + " p=" + Arrays.toString(p));
            }
        }
        System.out.println("Random cross-check: " + (trials - fails) + "/" + trials + " ok");
    }

    private static void check(MaxProfitJobScheduling solver,
                              int[] s, int[] e, int[] p, int expected) {
        int got = solver.jobScheduling(s, e, p);
        long brute = solver.jobSchedulingBrute(s, e, p);
        boolean ok = got == expected && brute == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "expected=" + expected + " fast=" + got + " brute=" + brute);
    }
}
