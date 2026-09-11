package airbnb.New2026;
/*
You are given coin denominations coins as decimals (e.g., 0.25, 0.5, 1.0) and a decimal target. Compute:

the minimum number of coins needed to sum to target if possible;
otherwise output -1.
Input (stdin)
Line 1: integer n, number of coin types.
Line 2: n decimal numbers coins[i] separated by spaces.
Line 3: a decimal number target.
Output (stdout)
One integer: minimum number of coins, or -1.
Constraints
1 <= n <= 30
Unlimited usage per coin
coins[i] > 0, target > 0
Inputs have at most 2 decimal places (key nuance: floating-point precision)
After scaling by 100: target_int <= 100000
Requirement
Do not use floating values as DP states. Scale to integers first, then solve standard coin change.

Examples
See the 5 test cases in the Chinese prompt.

Example
Input
3
0.25 0.5 1.0
1.5
Output
2
 */
import java.util.Arrays;

public class CoinChange2 {
    public int coinChange(double[] coins, double target) {
        int intTarget = (int) Math.round(target * 100);
        int n = coins.length;
        int[] intCoins = new int[n];
        for (int i = 0; i < n; i++) {
            intCoins[i] = (int) Math.round(coins[i] * 100);
        }

        int[] dp = new int[intTarget + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);

        dp[0] = 0;
        for (int i = 1; i <= intTarget; i++) {
            for (int coin : intCoins) {
                if (coin > 0 && i >= coin &&  dp[i - coin] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        return dp[intTarget] == Integer.MAX_VALUE ? -1 : dp[intTarget];
    }

    /** Same minimum-coin problem, but with coin/combinations loop order. */
    public int coinChangeCombinationLoop(double[] coins, double target) {
        int intTarget = (int) Math.round(target * 100);
        int[] dp = new int[intTarget + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (double coinValue : coins) {
            int coin = (int) Math.round(coinValue * 100);
            if (coin <= 0) continue;
            for (int amount = coin; amount <= intTarget; amount++) {
                if (dp[amount - coin] != Integer.MAX_VALUE) {
                    dp[amount] = Math.min(dp[amount], dp[amount - coin] + 1);
                }
            }
        }
        return dp[intTarget] == Integer.MAX_VALUE ? -1 : dp[intTarget];
    }

    public int coinChangeOneUse(double[] coins, double target) {
        int intTarget = (int) Math.round(target * 100);
        int[] dp = new int[intTarget + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (double coinValue : coins) {
            int coin = (int) Math.round(coinValue * 100);
            for (int amount = intTarget; amount >= coin; amount--) {
                if (dp[amount - coin] != Integer.MAX_VALUE) {
                    dp[amount] = Math.min(dp[amount], dp[amount - coin] + 1);
                }
            }
        }
        return dp[intTarget] == Integer.MAX_VALUE ? -1 : dp[intTarget];
    }

    public static void main(String[] args) {
        CoinChange2 solver = new CoinChange2();
        check("normal loop example", solver.coinChange(new double[]{0.25, 0.5, 1.0}, 1.5), 2);
        check("combination loop example", solver.coinChangeCombinationLoop(new double[]{0.25, 0.5, 1.0}, 1.5), 2);
        check("combination loop cents", solver.coinChangeCombinationLoop(new double[]{0.01, 0.05, 0.10}, 0.17), 4);
        check("combination loop impossible", solver.coinChangeCombinationLoop(new double[]{0.3, 0.5}, 0.1), -1);
        check("one-use cannot reuse", solver.coinChangeOneUse(new double[]{0.25}, 0.50), -1);
        System.out.println("All tests passed.");
    }

    private static void check(String label, int actual, int expected) {
        if (actual != expected) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
        System.out.println(label + ": " + actual);
    }
}
