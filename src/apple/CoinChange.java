package apple;

import java.util.Arrays;

public class CoinChange {
    public static int coinChange(int[] coins, int amount) {
        if (coins == null || coins.length == 0) return 0;
        int n = coins.length;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);

        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= amount; j++) {
                if (j >= coins[i - 1]) {
                    dp[j] = Math.min(dp[j], 1 + dp[j - coins[i - 1]]);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        int[] coins = {1,2,5};
        System.out.println(coinChange(coins, 11));
    }
}
