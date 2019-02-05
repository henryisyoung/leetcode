package leetcode.dynamicProgramming;

public class InterleavingString {
    //Given s1, s2, s3, find whether s3 is formed by the interleaving of s1 and s2.
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length(), k = s3.length();
        if (m + n != k) {
            return false;
        }
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for(int i = 1; i <= m; i++) {
            if(s1.charAt(i - 1) == s3.charAt(i - 1) && dp[i - 1][0]) {
                dp[i][0] = true;
            }
        }
        for (int j = 1; j <= n; j++) {
            if (s2.charAt(j - 1) == s3.charAt(j - 1) && dp[0][j - 1]) {
                dp[0][j] = true;
            }
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int t = i + j - 1;
                if(s3.charAt(t) == s1.charAt(i - 1) && dp[i - 1][j]) {
                    dp[i][j] = true;
                }
                if(s3.charAt(t) == s2.charAt(j - 1) && dp[i][j - 1]) {
                    dp[i][j] = true;
                }
            }
        }
        return dp[m][n];
    }
    public boolean isInterleave2(String s1, String s2, String s3) {
        if (s1.length() + s2.length() != s3.length()) {
            return false;
        }
        int m = s1.length(), n = s2.length(), k = s3.length();

        boolean[][] dp = new boolean[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            if (s1.charAt(i - 1) != s3.charAt(i - 1)) {
                break;
            }
            dp[i][0] = true;
        }

        for (int i = 1; i <= n; i++) {
            if (s2.charAt(i - 1) != s3.charAt(i - 1)) {
                break;
            }
            dp[0][i] = true;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s3.charAt(i + j - 1) && dp[i - 1][j]) {
                    dp[i][j] = true;
                }
                if (s2.charAt(j - 1) == s3.charAt(i + j - 1) && dp[i][j - 1]) {
                    dp[i][j] = true;
                }
            }
        }
        return dp[m][n];
    }
}
