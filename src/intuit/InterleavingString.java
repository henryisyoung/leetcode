package intuit;

public class InterleavingString {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length(), k = s3.length();
        if (m + n != k) {
            return false;
        }
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int i = 0; i < m; i++) {
            if (s1.charAt(i) == s3.charAt(i)) {
                dp[i + 1][0] = true;
            } else {
                break;
            }
        }
        for (int i = 0; i < n; i++) {
            if (s2.charAt(i) == s3.charAt(i)) {
                dp[0][i + 1] = true;
            } else {
                break;
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char a = s1.charAt(i - 1), b = s2.charAt(j - 1), c = s3.charAt(i + j - 1);
                if (a == c && dp[i - 1][j]) {
                    dp[i][j] = true;
                }
                if (b == c && dp[i][j - 1]) {
                    dp[i][j] = true;
                }
            }
        }
        return dp[m][n];
    }
}
