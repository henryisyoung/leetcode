package leetcode.dynamicProgramming;

public class BackpackII {
    public int backPackII(int m, int[] A, int[] V) {
        // write your code here
        if (A == null || A.length == 0 || A.length != V.length || m == 0) {
            return 0;
        }
        int n = A.length;
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <=n ; i++) {
            for (int t = 1; t <= m; t++) {
                if (t >= A[i - 1]) {
                    dp[i][t] = Math.max(dp[i - 1][t], dp[i - 1][t - A[i - 1]] + V[i - 1]);
                } else {
                    dp[i][t] = dp[i - 1][t];
                }
            }
        }
        return dp[n][m];
    }
}