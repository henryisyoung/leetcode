package leetcode.dynamicProgramming;

public class BackpackIII {
    public int backPackII(int m, int[] A, int[] V) {
        if (A == null || V == null || A.length != V.length || A.length == 0 || m == 0) {
            return 0;
        }
        int n = A.length;
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int t = 1; t <= m; t++) {
                dp[i][t] = dp[i - 1][t];
                if (t >= A[i - 1]) {
                    dp[i][t] = Math.max(dp[i][t], dp[i][t - A[i - 1]] + V[i - 1]);
                }
            }
        }
        return dp[n][m];
    }
}