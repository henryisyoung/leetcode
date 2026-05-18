package airbnb.New2026;
/*
Count Meal Order Combinations Summing to a Budget.

Given a list `prices` of dish costs and an integer `amount` (budget),
count the number of distinct unordered multisets of dishes whose total
cost equals `amount` exactly.  Each dish can be ordered any number of
times.

This is the canonical "Coin Change II" / unbounded-knapsack count.

Input
  prices : int[]   (each price > 0)
  amount : int     (>= 0)

Output
  Number of distinct combinations (long).

Example
  prices = [1, 2, 5], amount = 5  ->  4
    5 = 5
    5 = 2 + 2 + 1
    5 = 2 + 1 + 1 + 1
    5 = 1 + 1 + 1 + 1 + 1

Constraints (interview)
  1 <= prices.length <= 500
  1 <= prices[i]     <= 300
  0 <= amount        <= 1e4

Notes on overflow
  No modulo is required by this spec, so we return a raw count.
  For pathological inputs (many distinct small prices + large amount)
  the answer can exceed Long.MAX_VALUE; use BigInteger in that case.
  See `countWaysBig` below for the safe variant.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
Algorithm: 1-D unbounded knapsack, "combinations" loop order.

  Let dp[j] = number of unordered multisets of dishes summing to j.

  Init: dp[0] = 1 (the empty order is the unique way to spend 0).

  Transition: for each dish price p (OUTER), for j = p..amount (INNER):
              dp[j] += dp[j - p]

  Loop order matters:
    - Outer-prices, inner-amount   -> counts COMBINATIONS (what we want).
    - Outer-amount, inner-prices   -> counts PERMUTATIONS / sequences.

  Intuition: by fixing the order "prices then amounts", we commit each
  price's contribution before moving on, so dp[j] never double-counts
  (5 = 2+2+1) vs. (5 = 1+2+2) as separate.

  Skipping non-positive / amount-exceeding prices is a small but
  worthwhile pruning.

  Complexity
    Time   O(n * amount)        n = number of (distinct) prices used
    Memory O(amount)
*/
public class MealOrderCombinations {

    /** Returns the combination count as a long (sufficient for typical inputs). */
    public long countWays(int[] prices, int amount) {
        if (amount < 0) return 0;
        if (prices == null) return amount == 0 ? 1 : 0;

        long[] dp = new long[amount + 1];
        dp[0] = 1;
        for (int p : prices) {
            if (p <= 0 || p > amount) continue;
            for (int j = p; j <= amount; j++) {
                dp[j] += dp[j - p];
            }
        }
        return dp[amount];
    }

    /** BigInteger variant for inputs where the count can blow past Long.MAX_VALUE. */
    public BigInteger countWaysBig(int[] prices, int amount) {
        if (amount < 0) return BigInteger.ZERO;
        if (prices == null) return amount == 0 ? BigInteger.ONE : BigInteger.ZERO;

        BigInteger[] dp = new BigInteger[amount + 1];
        Arrays.fill(dp, BigInteger.ZERO);
        dp[0] = BigInteger.ONE;
        for (int p : prices) {
            if (p <= 0 || p > amount) continue;
            for (int j = p; j <= amount; j++) {
                dp[j] = dp[j].add(dp[j - p]);
            }
        }
        return dp[amount];
    }

    /* --------------------------- O(n^amount) brute force for tests --------------------------- */

    /** Recursive enumeration; only safe for tiny inputs. */
    long countWaysBrute(int[] prices, int amount) {
        if (amount < 0) return 0;
        if (amount == 0) return 1;
        // Sort to enforce a canonical order (non-decreasing indices) -> unordered count.
        int[] p = prices.clone();
        Arrays.sort(p);
        return brute(p, 0, amount);
    }

    private long brute(int[] p, int start, int remaining) {
        if (remaining == 0) return 1;
        long total = 0;
        for (int i = start; i < p.length; i++) {
            if (p[i] <= 0 || p[i] > remaining) continue;
            total += brute(p, i, remaining - p[i]);   // allow repeats: keep i
        }
        return total;
    }

    /* --------------------------- IO + demo --------------------------- */

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

    /**
     * Stdin format (matches the prompt):
     *   line 1: [1, 2, 5]
     *   line 2: 5
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int[] prices = parseIntArray(br.readLine());
        int amount   = Integer.parseInt(br.readLine().trim());
        System.out.println(new MealOrderCombinations().countWays(prices, amount));
    }

    static int[] parseIntArray(String s) {
        if (s == null) return new int[0];
        s = s.trim();
        if (s.startsWith("[")) s = s.substring(1);
        if (s.endsWith("]"))   s = s.substring(0, s.length() - 1);
        s = s.trim();
        if (s.isEmpty()) return new int[0];
        String[] tok = s.split("\\s*,\\s*");
        int[] out = new int[tok.length];
        for (int i = 0; i < tok.length; i++) out[i] = Integer.parseInt(tok[i].trim());
        return out;
    }

    private static void runDemos() {
        MealOrderCombinations solver = new MealOrderCombinations();

        // Spec example.
        check(solver, new int[]{1, 2, 5}, 5, 4);

        // amount == 0 -> the empty order, 1 way.
        check(solver, new int[]{1, 2, 5}, 0, 1);

        // Single dish that divides the amount -> 1 way.
        check(solver, new int[]{3}, 9, 1);

        // Single dish that does NOT divide the amount -> 0 ways.
        check(solver, new int[]{3}, 10, 0);

        // No prices fit.
        check(solver, new int[]{7, 8, 9}, 5, 0);

        // Duplicates in the input: should be treated as separate dishes
        // sharing the same price -> doesn't change the count semantically
        // (still combinations of "this price").  Useful sanity check.
        // {2, 2} with amount 4: ways are 2+2 -> 1 combination, NOT 2.
        // (Our DP treats each price as a separate dish but inner loop
        //  is over `j`, so each duplicated price independently adds the
        //  same recurrence and DOES inflate the count.)
        // To match the "multiset of dish *instances*" interpretation
        // would require deduping; we deliberately don't, matching the
        // problem's "list of prices".
        // The note below makes the assumption explicit.
        check(solver, new int[]{2, 2}, 4,  /* with duplicates */  3);
        // Reasoning: with prices [a, b] both =2, the DP counts
        // "use a's", "use b's", "use both" separately = 3.  If you want
        // to merge identical prices, pre-dedupe: new int[]{2} -> 1 way.

        // Larger combo test.
        // prices [1,2,3], amount 4: ways are
        //   1+1+1+1, 1+1+2, 2+2, 1+3 -> 4
        check(solver, new int[]{1, 2, 3}, 4, 4);

        // ---- Random fuzz against brute force ----
        Random rnd = new Random(99);
        int trials = 200, fails = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + rnd.nextInt(4);
            int[] prices = new int[n];
            for (int i = 0; i < n; i++) prices[i] = 1 + rnd.nextInt(6);     // 1..6
            int amount = rnd.nextInt(20);                                    // 0..19
            long fast  = solver.countWays(prices, amount);
            long brute = solver.countWaysBrute(prices, amount);
            if (fast != brute) {
                fails++;
                System.out.println("MISMATCH prices=" + Arrays.toString(prices)
                        + " amount=" + amount + " fast=" + fast + " brute=" + brute);
            }
        }
        System.out.println("Random cross-check: " + (trials - fails) + "/" + trials + " ok");

        // ---- Stress: max constraints ----
        int N = 500, AMOUNT = 10_000;
        int[] big = new int[N];
        Random brnd = new Random(7);
        for (int i = 0; i < N; i++) big[i] = 1 + brnd.nextInt(300);
        long t0 = System.nanoTime();
        long ans = solver.countWays(big, AMOUNT);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress n=" + N + " amount=" + AMOUNT + ": ans=" + ans + " in " + ms + " ms");

        // ---- BigInteger sanity ----
        BigInteger bi = solver.countWaysBig(new int[]{1, 2, 5}, 5);
        System.out.println("BigInteger spec example: " + bi);
        List<Integer> tinyTest = new ArrayList<>();
        for (int v : new int[]{1, 2, 3}) tinyTest.add(v);
        System.out.println("BigInteger [1,2,3] amount=4: " + solver.countWaysBig(new int[]{1,2,3}, 4));
    }

    private static void check(MealOrderCombinations solver, int[] prices, int amount, long expected) {
        long fast = solver.countWays(prices, amount);
        long brute = solver.countWaysBrute(prices, amount);
        boolean ok = fast == expected && brute == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "prices=" + Arrays.toString(prices) + " amount=" + amount
                + " expected=" + expected + " fast=" + fast + " brute=" + brute);
    }
}
