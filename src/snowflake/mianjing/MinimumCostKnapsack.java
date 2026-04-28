package snowflake.mianjing;

import java.util.Arrays;

/*
Problem (modified knapsack with powers-of-two weights)
------------------------------------------------------
You are given n items.
  - The weight of the i-th item is 2^i.
  - The cost   of the i-th item is cost[i].
  - Items can be bought in any non-negative quantity (unbounded).

Goal: pick non-negative counts y_0, y_1, ..., y_{n-1} such that

      sum( y_i * 2^i ) >= minWeight

and the total cost  sum( y_i * cost[i] )  is minimized.

Example
-------
  n = 5,  cost = [2, 5, 7, 11, 25],  minWeight = 26
  Optimal: 2 units of item 0 (weight 1, cost 2) + 3 units of item 3 (weight 8, cost 11)
  Total weight = 2*1 + 3*8 = 26   >= 26
  Total cost   = 2*2 + 3*11 = 37
  Answer: 37


Key insight: "best cost per power of two"
-----------------------------------------
Buying 2 units of item i produces weight 2 * 2^i = 2^(i+1) at cost 2 * cost[i].
That's the same shape as buying 1 unit of item (i+1). So define

      best[i] = cheapest cost to produce exactly 2^i weight
              = min( cost[i],  2 * best[i-1] )      (best[0] = cost[0])

This collapses the unbounded-knapsack-with-stacking into a single price per
power-of-two "bucket". For i >= n we have no real item, but we can still
"synthesize" a 2^i bucket by stacking, so best[i] for i >= n is 2 * best[i-1].

After this rewrite, buying 2 of bucket i is never better than buying 1 of
bucket i+1 (because best[i+1] <= 2 * best[i] by construction). So the
optimal solution uses each bucket at most once — and the problem becomes:

      pick a subset S of buckets {0, 1, 2, ...} with
          sum_{i in S} 2^i  >=  minWeight
          minimizing sum_{i in S} best[i]


Bit DP on minWeight
-------------------
Look at minWeight in binary, low bit to high bit. Track one boolean state:

  TIGHT  = the bits we've chosen so far exactly match minWeight on bits >= i.
           (We still need the remainder to be at least the suffix of minWeight.)
  OVER   = we've already strictly exceeded minWeight at some higher bit.
           (No further work needed; cheapest tail is 0.)

We start TIGHT and process bits 0..M (M = enough bits to cover minWeight,
plus a few in case overshooting at a higher bit alone is cheapest):

  bit_i = 1, TIGHT:
      MUST take this bucket (lower bits can't reach 2^i on their own).
      cost += best[i]
  bit_i = 0, TIGHT:
      Either take 0 here and stay TIGHT,
      or take this bucket and become OVER (cost = best[i], lower bits free → 0).
      cost = min( cost,  best[i] )

In OVER mode we add nothing — we already overshot.

Complexity: O(n + log(minWeight)).

Why extend M past the highest bit of minWeight?
Sometimes the *single cheapest* way to cover minWeight is to buy ONE bucket
strictly larger than the highest bit of minWeight. Example:
   cost = [10, 10, 10]   (all weights {1, 2, 4})  minWeight = 7
   Tight match → 30. But best[3] = 2*best[2] = 20, so 1 bucket of size 8 wins.
We extend until best[i] gets large enough that no further bit can improve us.
 */
public class MinimumCostKnapsack {

    /**
     * O(n + log minWeight) solution using the powers-of-two structure.
     *
     * @param cost      cost[i] for item with weight 2^i
     * @param minWeight required total weight (>= 0)
     * @return minimum total cost to reach weight >= minWeight
     */
    public long getMinimumCost(int[] cost, long minWeight) {
        if (minWeight <= 0) return 0;
        int n = cost.length;
        if (n == 0) {
            throw new IllegalArgumentException("Need at least one item to satisfy minWeight > 0");
        }

        // We need to consider bits up to the highest bit of minWeight, plus
        // a margin so that "overshoot at a single higher bit" is on the table.
        // best[i] doubles for each i >= n, so a margin of ~32 is plenty —
        // beyond that best[i] far exceeds any feasible answer.
        int highMin = 63 - Long.numberOfLeadingZeros(minWeight);
        int M = Math.max(n - 1, highMin) + 32;
        if (M > 62) M = 62; // keep 1L << M safe

        // best[i] = cheapest way to produce exactly 2^i units of weight.
        long[] best = new long[M + 1];
        final long INF = Long.MAX_VALUE / 4;
        best[0] = cost[0];
        for (int i = 1; i <= M; i++) {
            long viaItem = (i < n) ? cost[i] : INF;
            long viaStack = (best[i - 1] >= INF / 2) ? INF : 2 * best[i - 1];
            best[i] = Math.min(viaItem, viaStack);
        }

        // Bit DP: walk bits 0..M, tracking the "tight" cost.
        // (The "over" cost is implicitly 0 — once we've overshot, no more cost.)
        long tight = 0;
        for (int i = 0; i <= M; i++) {
            long bit = (minWeight >> i) & 1L;
            if (bit == 1) {
                // Must take this bucket to keep up with minWeight at bit i.
                tight = tight + best[i];
            } else {
                // Either keep climbing (stay tight) OR overshoot here for free
                // on all lower bits — pay best[i] alone.
                tight = Math.min(tight, best[i]);
            }
        }
        return tight;
    }

    /**
     * O((minWeight + maxItemWeight) * n) reference DP, used to verify the fast
     * solution on small inputs. Standard unbounded-knapsack on weight axis.
     */
    public long getMinimumCostBrute(int[] cost, int minWeight) {
        if (minWeight <= 0) return 0;
        int n = cost.length;
        // The optimum is always within minWeight + (largest single item weight),
        // because any heavier solution can drop one copy of any item and still be valid.
        long maxW = (long) minWeight + (1L << (n - 1));
        if (maxW > 5_000_000) {
            throw new IllegalArgumentException("Brute DP capped at 5M; minWeight too large");
        }
        int W = (int) maxW;
        long[] dp = new long[W + 1];
        Arrays.fill(dp, Long.MAX_VALUE / 4);
        dp[0] = 0;
        for (int w = 1; w <= W; w++) {
            for (int i = 0; i < n; i++) {
                int wi = 1 << i;
                if (w >= wi && dp[w - wi] + cost[i] < dp[w]) {
                    dp[w] = dp[w - wi] + cost[i];
                }
            }
        }
        long ans = Long.MAX_VALUE;
        for (int w = minWeight; w <= W; w++) ans = Math.min(ans, dp[w]);
        return ans;
    }

    // ----------------------------------------------------------------------
    // Demo / tests
    // ----------------------------------------------------------------------
    public static void main(String[] args) {
        MinimumCostKnapsack s = new MinimumCostKnapsack();

        // From the problem statement.
        check(s.getMinimumCost(new int[]{2, 5, 7, 11, 25}, 26), 37, "spec example (n=5, mw=26)");

        // Single item: must buy ceil(minWeight / 1) copies.
        check(s.getMinimumCost(new int[]{5}, 3), 15, "n=1, mw=3");
        check(s.getMinimumCost(new int[]{5}, 4), 20, "n=1, mw=4");

        // Tiny: minWeight = 0.
        check(s.getMinimumCost(new int[]{2, 5}, 0), 0, "minWeight=0");

        // Overshoot wins: cheaper to buy one bigger item than to match exactly.
        // weights {1, 2}, costs {10, 3}, minWeight 1: buy item 1 → cost 3.
        check(s.getMinimumCost(new int[]{10, 3}, 1), 3, "overshoot wins");

        // All same cost: cheapest "1-bigger-bucket" beats "match every bit".
        // weights {1,2,4}, all cost 10, minWeight = 7 (= 0b111)
        // Tight match: 10+10+10 = 30. Single bucket of size 8 (stack two 4s) = 20.
        check(s.getMinimumCost(new int[]{10, 10, 10}, 7), 20, "stack-up wins (mw=7)");

        // Random-ish cross-checks against the brute DP.
        java.util.Random rng = new java.util.Random(42);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rng.nextInt(7);            // 1..7
            int[] c = new int[n];
            for (int i = 0; i < n; i++) c[i] = 1 + rng.nextInt(50);
            int mw = rng.nextInt(500);             // 0..499
            long fast = s.getMinimumCost(c, mw);
            long slow = s.getMinimumCostBrute(c, mw);
            if (fast != slow) {
                System.out.println("MISMATCH n=" + n + " cost=" + Arrays.toString(c)
                        + " mw=" + mw + " fast=" + fast + " brute=" + slow);
                return;
            }
        }
        System.out.println("200 random cross-checks: OK");
    }

    private static void check(long got, long expected, String label) {
        boolean ok = got == expected;
        System.out.println(label + ": " + got + (ok ? "  OK" : "  FAIL (expected " + expected + ")"));
    }
}
