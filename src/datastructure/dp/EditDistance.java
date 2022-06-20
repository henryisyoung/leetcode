package datastructure.dp;

public class EditDistance {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        if(m == 0) return n;
        if(n == 0) return m;
        int[][] dp = new int[m + 1][n + 1];
        for(int i = 1; i <= m; i++) dp[i][0] = i;
        for(int i = 1; i <= n; i++) dp[0][i] = i;

        for(int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char c1 = word1.charAt(i - 1), c2 = word2.charAt(j - 1);
                if(c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]))  + 1;
                }
            }
        }

        return dp[m][n];
    }
}
