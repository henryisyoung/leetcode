package leetcode.solution;

public class Question79_DP {
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
}
