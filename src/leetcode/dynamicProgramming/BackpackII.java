package leetcode.dynamicProgramming;

public class BackpackII {
    public int backPackII(int m, int[] A, int[] V) {
        if (A == null || V == null || A.length != V.length || A.length == 0 || m == 0) {
            return 0;
        }
        int n = A.length;
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++){
                if (j >= A[i - 1]) {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - A[i - 1]] + V[i - 1]);
                } else {
                    dp[i][j] =  dp[i - 1][j];
                }
            }
        }

        return dp[n][m];
    }
}