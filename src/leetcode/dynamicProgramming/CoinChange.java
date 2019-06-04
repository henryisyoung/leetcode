package leetcode.dynamicProgramming;

import java.util.*;

public class CoinChange {
    int min = Integer.MAX_VALUE;

    public int coinChange(int[] coins, int amount) {
        if (coins == null || coins.length == 0) {
            return -1;
        }
        Arrays.sort(coins);
        dfsSearchAll(coins, new ArrayList<>(), amount, 0, 0);
        return min == Integer.MAX_VALUE ? -1 : min;
    }

    private void dfsSearchAll(int[] coins, List<Integer> list, int amount, int pos, int cur) {
        if (cur == amount) {
            min = Math.min(list.size(), min);
            return;
        }
        for (int i = pos; i < coins.length; i++) {
            if (cur + coins[i] > amount) {
                break;
            }
            list.add(coins[i]);
            dfsSearchAll(coins, list, amount, i, cur + coins[i]);
            list.remove(list.size() - 1);
        }
    }

    public int coinChange2(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int coin : coins) {
            for (int j = coin; j <= amount; j++) {
                if (dp[j - coin] != Integer.MAX_VALUE) {
                    dp[j] = Math.min(dp[j], dp[j - coin] + 1);
                }
            }
        }

        return dp[amount] == Integer.MAX_VALUE ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        CoinChange solver = new CoinChange();
        System.out.println(solver.coinChange(new int[]{265,398,46,78,52}, 7754) ==
                solver.coinChange2(new int[]{265,398,46,78,52}, 7754));
    }
}
