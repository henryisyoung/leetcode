package leetcode.dynamicProgramming;

public class LongestCommonSubstring {
    public int longestCommonSubstring(String A, String B) {
        if(A == null || B == null || A.length() == 0 || B.length() == 0) {
            return 0;
        }
        int m = A.length(), n = B.length();

        int[][] dp = new int[m + 1][n + 1];
        int max = 0;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (A.charAt(i - 1) == B.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = 0;
                }
                max = Math.max(max, dp[i][j]);
            }
        }
        return max;
    }
}
