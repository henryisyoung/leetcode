package airbnb;

public class WildcardMatching {
    public boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        char[] ws = s.toCharArray();
        char[] wp = p.toCharArray();
        boolean[][] dp = new boolean[m+1][n+1];
        dp[0][0] = true;
        for (int j = 1; j <= n; j++)
            dp[0][j] = dp[0][j-1] && wp[j-1] == '*';
        for (int i = 1; i <= m; i++)
            dp[i][0] = false;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (wp[j-1] == '?' || ws[i-1] == wp[j-1])
                    dp[i][j] = dp[i-1][j-1];
                else if (wp[j-1] == '*')
                    dp[i][j] = dp[i-1][j] || dp[i][j-1];
            }
        }
        return dp[m][n];
    }
}
