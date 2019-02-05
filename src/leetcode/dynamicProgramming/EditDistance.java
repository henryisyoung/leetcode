package leetcode.dynamicProgramming;

public class EditDistance {
    // Given two words word1 and word2, find the minimum number of operations required to convert word1 to word2.
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] f = new int[m + 1][n + 1];
        for(int i = 1; i <= m; i++) f[i][0] = i;
        for(int i = 1; i <= n; i++) f[0][i] = i;
        for (int i = 1; i <= m; i++){
            for (int j = 1; j <= n; j++){
                if(word1.charAt(i-1) == word2.charAt(j-1)){
                    f[i][j] = Math.min(Math.min(f[i-1][j] + 1,f[i][j-1] + 1),f[i-1][j-1]);
                }else {
                    f[i][j] = Math.min(Math.min(f[i-1][j],f[i][j-1]),f[i-1][j-1]) + 1;
                }
            }
        }
        return f[m][n];
    }

    public int minDistance2(String word1, String word2) {
        if (word1 == null || word2 == null) {
            return 0;
        }
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 1; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[m][n];
    }
}
