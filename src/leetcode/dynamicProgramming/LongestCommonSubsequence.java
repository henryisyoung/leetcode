package leetcode.dynamicProgramming;

public class LongestCommonSubsequence {
    public int longestCommonSubsequence(String A, String B) {
        if(A == null || B == null || A.length() == 0 || B.length() == 0) {
            return 0;
        }
        int m = A.length(), n = B.length();

        int[][] dp = new int[m + 1][n + 1];
        for(int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if(A.charAt(i - 1) == B.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }
}
