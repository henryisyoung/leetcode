package leetcode.solution;

public class Questionlintcode79_DP_LCS {
    public int longestCommonSubstring(String A, String B) {
        if (A == null || B == null) return 0;
        if( A.length() == 0 | B.length() == 0) return 0;
        int m = A.length(), n =B.length();
        int[][] f = new int[m+1][n+1];
        int max= 0;
        for(int i = 1; i <= m; i++){
            for(int j = 1; j <=n; j++){
                if(A.charAt(i - 1) == B.charAt(j - 1)){
                    f[i][j] = f[i-1][j-1] + 1;
                }else {
                    f[i][j] = 0;
                }
                max = Math.max(max, f[i][j]);
            }
        }
        return max;
    }
}
