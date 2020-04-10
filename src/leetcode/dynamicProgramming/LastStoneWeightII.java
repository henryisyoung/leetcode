package leetcode.dynamicProgramming;

import java.util.Arrays;

public class LastStoneWeightII {
    public static int lastStoneWeightII(int[] stones) {
        int sum = 0;
        for (int i : stones) sum += i;
        boolean[][] dp = new boolean[stones.length + 1][sum / 2 + 1];
        Arrays.sort(stones);

        for (int i = 0; i < stones.length; i++) dp[i][0] =true;

        for (int i = 1; i <= stones.length; i++) {
            for (int j = 0; j <= sum/2; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j >= stones[i - 1]) {
                    dp[i][j] = dp[i][j] || dp[i - 1][j - stones[i - 1]];
                }
            }
        }

        for (int i = sum/2; i >= 0; i--) {
            if (dp[stones.length][i]) return sum - i - i;
        }
        return sum;
    }

    public static void main(String[] args) {
        int[] stones = {2,7,4,1,8,1};
        int[] stones2 = {1,3};
        System.out.println(lastStoneWeightII(stones));
        System.out.println(lastStoneWeightII(stones2));
    }
}
