package airbnb.New2026;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/*
================================================================================
  Minimum Purchases to Exactly Fill Layover Hours (Airbnb)
  Unbounded Knapsack — Minimum Count = LC 322 "Coin Change"
================================================================================

  Followup to FillLayoverHours: feasibility was true/false. Now we want the
  FEWEST experiences whose durations sum to EXACTLY X. Unlimited reuse.
  Return 0 if impossible (per spec).

  Decimal handling
    Inputs have exactly 1 decimal place → multiply by 10 to get integers,
    eliminating floating-point comparison bugs entirely.

  DP (Coin Change minimum)
    dp[t] = min number of experiences whose durations sum to t.
    dp[0] = 0;  dp[t] = INF if unreachable.

        for t in 1..X:
            for each duration d:
                if t >= d and dp[t - d] != INF:
                    dp[t] = min(dp[t], dp[t - d] + 1)

    Iterating `t` in the outer loop (and `d` inside) is the canonical
    coin-change-minimum order. Either nesting works for unbounded knapsack
    when the value is min-count; the (d outer, t inner ascending) order
    used in FillLayoverHours also computes the same dp[t] correctly.
    I keep `t` outer here because it reads more naturally as
    "for each target, try every coin".

  Complexity
    Time:    O(n · X)   ≤ 30 · 1000 = 3·10^4  (after ×10 scaling)
    Memory:  O(X)

  IO (per spec)
    Line 1: durations as space-separated decimals  (e.g. "3.0 2.0")
    Line 2: X as a decimal                          (e.g. "7.0")
    Output: min purchases, or 0 if impossible

  Followups worth mentioning
    F1. Reconstruct the chosen multiset → store a parent (which duration
        produced the min) per dp slot; walk back from dp[X].
    F2. Tie-breaking when multiple multisets achieve the minimum (e.g.
        prefer shorter durations / specific experiences) → store extra
        info alongside the count.
    F3. Bounded reuse (each experience usable at most K times) → 0/1
        knapsack with k-copies expansion, or iterate dp descending.
    F4. Approximate / large X (X up to 10^9 scaled) → switch to BFS on
        reachable sums with pruning, or use number-theoretic shortcuts
        (gcd, Frobenius) when n is tiny.
    F5. 2+ decimal places → just scale by 100 / 1000; the math is the same.
================================================================================
*/
public class MinPurchasesToFillLayover {

    /** Returns the min count of experiences summing to exactly X, or 0 if impossible. */
    public static int minPurchases(double[] durations, double X) {
        int target = (int) Math.round(X * 10);
        int[] d = new int[durations.length];
        for (int i = 0; i < durations.length; i++) d[i] = (int) Math.round(durations[i] * 10);
        return minPurchases(d, target);
    }

    public static int minPurchases(int[] durations, int target) {
        if (target <= 0) return 0;

        final int INF = Integer.MAX_VALUE;
        int[] dp = new int[target + 1];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int t = 1; t <= target; t++) {
            for (int d : durations) {
                if (d > 0 && d <= t && dp[t - d] != INF) {
                    dp[t] = Math.min(dp[t], dp[t - d] + 1);
                }
            }
        }
        return dp[target] == INF ? 0 : dp[target];
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
        double[] durations = new double[st.countTokens()];
        for (int i = 0; i < durations.length; i++) durations[i] = Double.parseDouble(st.nextToken());
        double X = Double.parseDouble(br.readLine().trim());
        System.out.println(minPurchases(durations, X));
    }

    /* --------------------------- Demos --------------------------- */

    private static void runDemos() {
        check(new double[]{3.0, 2.0},        7.0, 3);   // spec: 2+2+3
        check(new double[]{3.0, 2.0},        6.0, 2);   // 3+3 (beats 2+2+2)
        check(new double[]{2.0, 3.0},       10.0, 4);   // 2+2+3+3
        check(new double[]{5.0},             0.0, 0);   // trivially zero
        check(new double[]{3.0},             9.0, 3);   // 3+3+3
        check(new double[]{3.0},            10.0, 0);   // impossible
        check(new double[]{15.0, 20.0},      7.0, 0);   // all too big
        check(new double[]{1.0, 5.0, 10.0},  7.0, 3);   // 1+1+5  (not seven 1s)
        check(new double[]{2.0, 4.0},        6.0, 2);   // 2+4 (beats 2+2+2)

        // Decimal cases
        check(new double[]{2.5, 1.5},        5.0, 2);   // 2.5+2.5
        check(new double[]{0.5},             3.0, 6);   // six halves
        check(new double[]{1.5, 2.5, 0.5},   5.5, 3);   // 2.5+2.5+0.5
        check(new double[]{1.5, 2.5, 0.5},   5.4, 0);   // not a multiple of 0.1 from any combo

        // Chicken McNugget {6, 9, 20}: 43 unreachable.
        check(new double[]{6.0, 9.0, 20.0}, 43.0, 0);
        check(new double[]{6.0, 9.0, 20.0}, 44.0, 4);   // 6+9+9+20
    }

    private static void check(double[] durations, double X, int expected) {
        int got = minPurchases(durations, X);
        System.out.println((got == expected ? "OK  " : "FAIL")
                + " X=" + X + " durations=" + Arrays.toString(durations)
                + " expected=" + expected + " got=" + got);
    }
}
