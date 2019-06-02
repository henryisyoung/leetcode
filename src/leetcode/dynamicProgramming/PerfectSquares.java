package leetcode.dynamicProgramming;

import java.util.Arrays;

public class PerfectSquares {
    public int numSquares(int n) {
        if (n <= 1) {
            return 1;
        }
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        for (int i = 1; i * i <= n; i++) {
            dp[i * i] = 1;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j * j + i <= n; j++) {
                dp[i + j*j] = Math.min(dp[i] + 1, dp[i + j * j]);
            }
        }
        return dp[n];
    }
}
