package airbnb.New2026;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/*
================================================================================
  Fill Layover Hours by Booking Experiences  (Airbnb)
  Unbounded Knapsack / Exact-Sum Feasibility — LC 322/518 cousin.
================================================================================

  Given experiences[] (each is a duration in hours) and `layover` (target),
  decide whether there is a multiset of experiences whose total duration is
  EXACTLY `layover`. Each experience can be booked any number of times.

  Example
    experiences = [2, 3],  layover = 10  →  true   (2+2+3+3 = 10)

  DP
    dp[t] = true iff `t` is reachable using any multiset of experiences.
    dp[0] = true (empty selection).
    Transition (the standard unbounded-knapsack template):

        for each experience e:
            for t in e..layover:
                dp[t] |= dp[t - e]

    The key ordering: after fixing `e`, iterating `t` ascending lets
    dp[t - e] already reflect selections that USED `e` — so e is reused
    freely. (Iterating `t` descending would forbid reuse and turn this
    into 0/1 knapsack instead.)

  Complexity
    Time:    O(n · layover)     ≈ 200 · 1e4 = 2 · 10^6
    Memory:  O(layover)

  IO (per spec)
    Line 1: n layover
    Line 2: n space-separated durations
    Output: "true" or "false"

  Followups worth mentioning
    F1. Return the actual multiset → keep a parent pointer per dp slot
        (which experience produced the transition).
    F2. Minimum number of experiences to fill layover → switch dp from
        boolean to int (Integer.MAX_VALUE = unreachable), take 1 + min.
    F3. Count of distinct combinations summing to layover → dp becomes
        long count; this is LC 518 directly.
    F4. Bounded knapsack (each experience usable at most K times) →
        iterate t descending and unroll K copies, or use 0/1 transform.
================================================================================
*/
public class FillLayoverHours {

    public static boolean canFill(int[] experiences, int layover) {
        if (layover == 0) return true;

        boolean[] dp = new boolean[layover + 1];
        dp[0] = true;
        for (int e : experiences) {
            if (e <= 0 || e > layover) continue;       // useless or out of range
            for (int t = e; t <= layover; t++) {
                if (dp[t - e]) dp[t] = true;
            }
        }
        return dp[layover];
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
        try { return System.in.available() > 0; } catch (IOException e) { return false; }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int layover = Integer.parseInt(st.nextToken());
        int[] experiences = new int[n];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) experiences[i] = Integer.parseInt(st.nextToken());
        System.out.println(canFill(experiences, layover));
    }

    /* --------------------------- Demos --------------------------- */

    private static void runDemos() {
        check(new int[]{2, 3},        10, true);   // spec example: 2+2+3+3
        check(new int[]{5},            0, true);   // empty selection always works
        check(new int[]{3},            9, true);   // 3+3+3
        check(new int[]{3},           10, false);  // 10 not a multiple of 3
        check(new int[]{15, 20},       7, false);  // all experiences > layover
        check(new int[]{1, 5, 10},     7, true);   // 1+1+5 (or seven 1s)
        check(new int[]{2, 4},         1, false);  // odd layover, even durations
        check(new int[]{2, 4},         6, true);   // 2+4 or 2+2+2
        check(new int[]{7, 11, 13},   24, true);   // 11+13 = 24
        // Chicken McNugget Theorem: 43 is the largest integer NOT expressible
        // as a non-negative combination of {6, 9, 20}. Every n >= 44 is reachable.
        check(new int[]{6, 9, 20},    41, true);   // 6+9+6+20 = 41
        check(new int[]{6, 9, 20},    43, false);  // unreachable (the Frobenius number)
        check(new int[]{6, 9, 20},    44, true);   // 6+9+9+20 = 44
    }

    private static void check(int[] experiences, int layover, boolean expected) {
        boolean got = canFill(experiences, layover);
        System.out.println((got == expected ? "OK  " : "FAIL")
                + " layover=" + layover + " exp=" + Arrays.toString(experiences)
                + " expected=" + expected + " got=" + got);
    }
}
