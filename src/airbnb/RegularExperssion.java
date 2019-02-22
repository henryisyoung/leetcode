package airbnb;

public class RegularExperssion {
    public static boolean regMatch(String source, String pattern) {
        if (pattern.length() == 0) {
            return source.length() == 0;
        } if (pattern.length() == 1) {
            if (source.length() != 1) {
                return false;
            }
            return source.charAt(0) == pattern.charAt(0) || pattern.charAt(0) == '.';
        }
        if (source.length() != 0 && (pattern.charAt(0) == '.' || pattern.charAt(0) == source.charAt(0))) {
            if (pattern.charAt(1) == '*') {
                return regMatch(source.substring(1), pattern) || regMatch(source, pattern.substring(2));
            } else if (pattern.charAt(1) == '+') {
                return regMatch(source.substring(1), pattern.substring(2)) || regMatch(source.substring(1), pattern.substring(2));
            } else {
                return regMatch(source.substring(1), pattern.substring(1));
            }
        }
        return pattern.charAt(1) == '*' && regMatch(source, pattern.substring(2));
    }

    public static boolean regMatch2(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int i = 0; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) != '*' && p.charAt(j - 1) != '.' &&  p.charAt(j - 1) != '+') {
                    if (i > 0 && s.charAt(i - 1) == p.charAt(j - 1) && dp[i - 1][j - 1])
                        dp[i][j] = true;
                } else if (p.charAt(j - 1) == '.') {
                    if (i > 0 && dp[i - 1][j - 1])
                        dp[i][j] = true;
                } else if (p.charAt(j - 1) == '*') {
                    if (j > 1 && dp[i][j - 2]) {
                        // multiply 0 preceding element
                        dp[i][j] = true;
                    }
                    else if (j > 1 && i > 0 && (p.charAt(j - 2) == s.charAt(i - 1) || p.charAt(j - 2) == '.') && dp[i - 1][j]) {
                        // multiply multiple preceding elements
                        dp[i][j] = true;
                    }
                } else {
                    if (j > 1 && dp[i][j - 1]) {
                        // multiply 1 or 0 preceding element
                        dp[i][j] = true;
                    }
                    else if (j > 1 && i > 0 && (p.charAt(j - 2) == s.charAt(i - 1) || p.charAt(j - 2) == '.') && dp[i - 1][j]) {
                        // multiply multiple preceding elements
                        dp[i][j] = true;
                    }
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        String s = "aa", p = "a+";
        String s1 = "aa", p1 = "a*";
        String s2 = "", p2 = "a*";
        String s3 = "aa", p3 = "a.";
        System.out.println(regMatch2(s, p));
        System.out.println(regMatch2(s1, p1));
        System.out.println(regMatch2(s2, p2));
        System.out.println(regMatch2(s3, p3));

        System.out.println(regMatch(s, p));
        System.out.println(regMatch(s1, p1));
        System.out.println(regMatch(s2, p2));
        System.out.println(regMatch(s3, p3));

    }
}
