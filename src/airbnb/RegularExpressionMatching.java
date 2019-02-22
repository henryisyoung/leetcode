package airbnb;

public class RegularExpressionMatching {
    public static boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int i = 0; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) != '*' && p.charAt(j - 1) != '.') {
                    if (i > 0 && s.charAt(i - 1) == p.charAt(j - 1) && dp[i - 1][j - 1])
                        dp[i][j] = true;
                } else if (p.charAt(j - 1) == '.') {
                    if (i > 0 && dp[i - 1][j - 1])
                        dp[i][j] = true;
                } else if (j > 1) {
                    if (dp[i][j - 1] || dp[i][j - 2]) {
                        // multiply 1 or 0 preceding element
                        dp[i][j] = true;
                    }
                    else if (i > 0 && (p.charAt(j - 2) == s.charAt(i - 1) || p.charAt(j - 2) == '.') && dp[i - 1][j])
                        // multiply multiple preceding elements
                        dp[i][j] = true;
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        String s = "", p = "**";
        String s2 = "aaa", p2 = "**";
        String s3 = "", p3 = "**";
        String s4 = "", p4 = "**";
        System.out.println(isMatch(s, p));
    }
}