package apple;

public class InterleavingString {
    public  static boolean isInterleave(String s1, String s2, String s3) {
        if(s1.length() + s2.length() != s3.length()) return false;
        int m = s1.length(), n = s2.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int i = 0; i < m; i++) {
            if (s1.charAt(i) == s3.charAt(i)) {
                dp[i + 1][0] = true;
            } else {
                break;
            }
        }

        for (int j = 0; j < n; j++) {
            if (s2.charAt(j) == s3.charAt(j)) {
                dp[0][j + 1] = true;
            } else {
                break;
            }
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

    public static void main(String[] args) {
        String s1 = "aabcc", s2 = "dbbca", s3 = "aadbbbaccc";
        System.out.println(isInterleave(s1,s2,s3));
    }
}
