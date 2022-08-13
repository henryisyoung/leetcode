package roblox.onsite;

public class RegularExpressionMatching {
    public boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        char[] sChars = s.toCharArray(), pChars = p.toCharArray();

        for (int i = 0 ;i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (i > 0 && (sChars[i - 1] == pChars[j - 1] || pChars[j - 1] == '.')) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (pChars[j - 1] == '*') {
                    if (j >= 2 && dp[i][j - 2]) {
                        dp[i][j] = true;
                    } else if (i > 0 && j >= 2 && (pChars[j - 2] == '.' || pChars[j - 2] == sChars[i - 1]) && dp[i - 1][j]) {
                        dp[i][j] = true;
                    }
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {

    }
}
