package leetcode;

public class Questionlintcode77_DP_LCS {
    public int longestCommonSubsequence(String A, String B) {
        if (A == null || B == null) return 0;
        if( A.length() == 0 | B.length() == 0) return 0;
        int m = A.length(), n =B.length();
        int[][] f = new int[m+1][n+1];
        for(int i = 1; i <= m; i++){
            for(int j = 1; j <=n; j++){
                if(A.charAt(i - 1) == B.charAt(j - 1)){
                    f[i][j] = f[i-1][j-1] + 1;
                }else {
                    f[i][j] = Math.max(f[i-1][j],f[i][j-1]);
                }
            }
        }
        return f[m][n];
    }
}
