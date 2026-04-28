package snowflake.mianjing;

import java.util.Arrays;

/*
Problem
-------
Coin denominations: {1, 5, 10, 50, 100, 200} (infinite supply of each).
You owe `n`. You can OVERPAY and the other party gives change back, also in
these coins. Return the minimum total number of coins exchanged
(coins you hand over + coins you receive back).

Example
    n = 41  →  3
    Pay 50 + 1  (2 coins),  receive 10 back  (1 coin),   total = 3.

Insight: rewrite as a "make N from ±coins" problem
---------------------------------------------------
Every transaction is either "I hand over coin c" (+c to the net) or
"I receive coin c as change" (-c from the net). The net must equal n.
So we want non-negative integer counts a_i (paid) and b_i (received) such that

    sum( (a_i - b_i) * c_i )  =  n,           minimize  sum( a_i + b_i )

Equivalently — and this is the trick — we pick a payment amount P >= n. The
change is exactly P - n. Both P and P - n must be expressible exactly using
the coin set, and the total cost is

    minCoinsExact(P)  +  minCoinsExact(P - n)

We minimize over P. Because every extra unit of overpayment is itself made of
coins ≥ 1, an optimal P never overpays by more than the largest coin (200);
beyond that, swapping a 200 from change with a 200 from payment strictly
reduces the count. So we only scan P ∈ [n, n + 200].

Algorithm
---------
1. Standard 1D coin-change DP for `minCoinsExact(x)` over [0, n + 200].
2. Sweep P from n to n + 200, take min of (dp[P] + dp[P - n]).

Time:  O(n * |coins|)  — the DP dominates.
Space: O(n)            — the dp array.
 */
public class MinCoinsWithChange {

    private static final int[] COINS    = {1, 5, 10, 50, 100, 200};
    private static final int   MAX_COIN = 200;

    public int minCoins(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n == 0) return 0;

        // dp[amt] = min coins to make `amt` exactly with the given coin set.
        int upper = n + MAX_COIN;
        int[] dp = new int[upper + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int amt = 1; amt <= upper; amt++) {
            for (int c : COINS) {
                if (amt >= c && dp[amt - c] != Integer.MAX_VALUE) {
                    dp[amt] = Math.min(dp[amt], dp[amt - c] + 1);
                }
            }
        }

        // For every choice of payment P in [n, n + MAX_COIN]:
        //   coins paid  = dp[P]
        //   change back = P - n     →  coins received = dp[P - n]
        // Sum them; keep the smallest.
        int best = Integer.MAX_VALUE;
        for (int p = n; p <= upper; p++) {
            int change = p - n;
            int total = dp[p] + dp[change];   // both are guaranteed reachable (the 1-coin always works)
            if (total < best) best = total;
        }
        return best;
    }

    /**
     * Same answer but also returns one optimal (paymentCoins, changeCoins) pair,
     * useful for explanations and unit tests.
     */
    public Result minCoinsWithBreakdown(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n == 0) return new Result(0, 0, 0);

        int upper = n + MAX_COIN;
        int[] dp = new int[upper + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int amt = 1; amt <= upper; amt++) {
            for (int c : COINS) {
                if (amt >= c && dp[amt - c] != Integer.MAX_VALUE) {
                    dp[amt] = Math.min(dp[amt], dp[amt - c] + 1);
                }
            }
        }

        int bestTotal = Integer.MAX_VALUE, bestP = n;
        for (int p = n; p <= upper; p++) {
            int total = dp[p] + dp[p - n];
            if (total < bestTotal) {
                bestTotal = total;
                bestP = p;
            }
        }
        return new Result(bestTotal, dp[bestP], dp[bestP - n]);
    }

    public static class Result {
        public final int total;        // total coins exchanged
        public final int paid;         // coins handed over
        public final int change;       // coins received back

        public Result(int total, int paid, int change) {
            this.total = total;
            this.paid = paid;
            this.change = change;
        }

        @Override
        public String toString() {
            return "total=" + total + " (paid=" + paid + ", change=" + change + ")";
        }
    }

    // ============================================================
    // Demo / tests
    // ============================================================
    public static void main(String[] args) {
        MinCoinsWithChange s = new MinCoinsWithChange();

        // Example from the prompt.
        check(s.minCoins(41), 3, "41 → pay 50+1, change 10");

        // Exact-pay cases (no overpayment).
        check(s.minCoins(0),   0, "0");
        check(s.minCoins(1),   1, "1");
        check(s.minCoins(5),   1, "5");
        check(s.minCoins(200), 1, "200");
        check(s.minCoins(201), 2, "201 = 200 + 1");
        check(s.minCoins(250), 2, "250 = 200 + 50");
        check(s.minCoins(150), 2, "150 = 100 + 50");

        // Cases where overpaying wins.
        check(s.minCoins(4),   2, "4   → pay 5,   change 1");
        check(s.minCoins(49),  2, "49  → pay 50,  change 1");
        check(s.minCoins(95),  2, "95  → pay 100, change 5");
        check(s.minCoins(99),  2, "99  → pay 100, change 1");
        check(s.minCoins(199), 2, "199 → pay 200, change 1");

        // Stress / sanity.
        check(s.minCoins(3),   3, "3   = 1+1+1 (overpaying needs more)");
        check(s.minCoins(8),   3, "8   → pay 10,  change 1+1");
        check(s.minCoins(11),  2, "11  → pay 10+1");

        System.out.println();
        System.out.println("Breakdown for 41: " + s.minCoinsWithBreakdown(41));
        System.out.println("Breakdown for 4:  " + s.minCoinsWithBreakdown(4));
        System.out.println("Breakdown for 99: " + s.minCoinsWithBreakdown(99));
    }

    private static void check(int got, int expected, String label) {
        boolean ok = got == expected;
        System.out.println(label + ": " + got + (ok ? "  OK" : "  FAIL (expected " + expected + ")"));
    }
}
