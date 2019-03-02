package airbnb;

public class RegularExpressionMatching {
    public static boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int i = 0; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) != '*' && p.charAt(j - 1) != '.') {
                    if (i > 0 && s.charAt(i - 1) == p.charAt(j - 1) && dp[i - 1][j - 1]) {
                        dp[i][j] = true;
                    }
                } else if (p.charAt(j - 1) == '.') {
                    if (i > 0 && dp[i - 1][j - 1]) {
                        dp[i][j] = true;
                    }
                } else {
                    if (j > 1) {
                        if (dp[i][j - 1] || dp[i][j - 2]) {
                            dp[i][j] = true;
                        } else if (i > 0 && (p.charAt(j - 2) == s.charAt(i - 1) || p.charAt(j -2) == '.') && dp[i - 1][j]) {
                            dp[i][j] = true;
                        }
                    }
                }
            }
        }
        return dp[m][n];
    }

    public boolean regMatch(String s, String p) {
        if (s == null || p == null) return false;
        boolean[][] dp = new boolean[p.length() + 1][s.length() + 1];
        dp[0][0] = true;
        for (int i = 1; i <= p.length(); i++) {
            if (p.charAt(i - 1) == '*' && dp[i - 1][0]) dp[i][0] = true;
        }

        for (int i = 1; i <= p.length(); i++) {
            for (int j = 1; j <= s.length(); j++) {
                if (p.charAt(i - 1) == '.' || p.charAt(i - 1) == s.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (p.charAt(i - 1) == '*' || p.charAt(i - 1) == '+') {
                    if (p.charAt(i - 2) == '.' || p.charAt(i - 2) == s.charAt(j - 1)) {
                        if (p.charAt(i - 1) == '*') {
                            dp[i][j] = dp[i - 2][j] || dp[i - 2][j - 1] || dp[i][j - 1];
                        } else {
                            dp[i][j] = dp[i - 2][j - 1] || dp[i][j - 1];
                        }
                    } else {
                        dp[i][j] = p.charAt(i - 1) == '*' && dp[i - 2][j];
                    }
                }
            }
        }
        return dp[p.length()][s.length()];
    }

    public static void main(String[] args) {
        String s = "", p = "**";
        String s2 = "aaa", p2 = "**";
        String s3 = "", p3 = "**";
        String s4 = "", p4 = "**";
//        System.out.println(isMatch(s, p));

        String s5 = "saaaa", p5 = "s+a*";
        RegularExpressionMatching solver = new RegularExpressionMatching();
        boolean result = solver.regMatch(s5, p5);
        System.out.println(result);
    }
}