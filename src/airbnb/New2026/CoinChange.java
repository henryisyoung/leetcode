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

public class CoinChange {
    public int coinChange(double[] coins, double target) {
        int n = coins.length;
        int[] coinsInt = new int[n];
        for (int i = 0; i < n; i++) {
            coinsInt[i] = (int) Math.round(coins[i] * 100);
        }
        int targetInt = (int) Math.round(target * 100);
        int[] dp = new int[targetInt + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= targetInt; i++) {
            for (int j = 0; j < n; j++) {
                int val = coinsInt[j];
                if (val > 0 && i >= val && dp[i - val] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[i - val] + 1);
                }
            }
        }
        return dp[targetInt] == Integer.MAX_VALUE ? -1 : dp[targetInt];
    }
}
