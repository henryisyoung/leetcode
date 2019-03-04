package airbnb;

public class RegularExperssion {
//• *: 0个或多个之前字符
//• .: 任何字符
//• +:1个或多个之前字符
    public static boolean regMatch2(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int i = 0; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (j > 1 && p.charAt(j - 1) == '*') {
                    if (dp[i][j - 2]) {
                        dp[i][j] = true; // match 0 time
                    }
                    // * do not match 1 time
                    else if (i > 1 && (s.charAt(i - 1) == p.charAt(j - 2) || p.charAt(j - 2) == '.') && dp[i - 2][j]) {
                        dp[i][j] = true; // match >= 1 time
                    }
                } else if (j > 1 && p.charAt(j - 1) == '+') {
                    // + do not match 0 time
                    if (i > 0 && (s.charAt(i - 1) == p.charAt(j - 2) || p.charAt(j - 2) == '.') && dp[i - 1][j]) {
                        dp[i][j] = true; // match >= 1 time
                    }
                }
                else {
                    if (i > 0 && (( s.charAt(i - 1) == p.charAt(j - 1)) || p.charAt(j - 1) == '.') && dp[i - 1][j - 1]) {
                        dp[i][j] = true;
                    }
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        String s = "a", p = "a+"; // true
//        String s1 = "a", p1 = "a*"; // false
//        String s2 = "", p2 = "a*"; // true
//        String s3 = "aa", p3 = "a."; // true
//        String s4 = "", p4 = "a+"; // false
//        String s5 = "aa", p5 = "a+"; // true
//        String s6 = "aa", p6 = "a*"; // true

        System.out.println(regMatch2(s, p));
//        System.out.println(regMatch2(s1, p1));
//        System.out.println(regMatch2(s2, p2));
//        System.out.println(regMatch2(s3, p3));
//        System.out.println(regMatch2(s4, p4));
//        System.out.println(regMatch2(s5, p5));
//        System.out.println(regMatch2(s6, p6));

    }
}
