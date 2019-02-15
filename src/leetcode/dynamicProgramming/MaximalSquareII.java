package leetcode.dynamicProgramming;

public class MaximalSquareII {
    /**
     * State: dp[i][j]: the max length of a square with only diagonal 1s whose bottom right corner is matrix[i][j].

     Function: dp[i][j] = 0, if matrix[i][j] == 0;

     　　　　 dp[i][j] = 1 + min {leftZeros[i][j], upZeros[i][j], dp[i - 1][j - 1]}, if matrix[i][j] == 1;

     Similarly with Maxmial Square, when matrix[i][j] is 1, we also need to check its left, top, top left side.

     leftZeros[i][j] is the max number of consecutive 0s to the left of matrix[i][j];

     upZeros[i][j] is the max number of consecutive 0s to the top of matrix[i][j];

     These two along with dp[i - 1][j - 1] decides the max length of a sqaure with only diagonal 1s whose bottom right corner is matrix[i][j].
     * @param matrix
     * @return
     */
    public int maxSquare2(int[][] matrix) {
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0){
            return 0;
        }
        int n = matrix.length; int m = matrix[0].length;
        int[][] leftZeros = new int[n][m];
        int[][] upZeros = new int[n][m];
        for(int i = 0; i < n; i++){
            leftZeros[i][0] = 0;
        }
        for(int j = 0; j < m; j++){
            upZeros[0][j] = 0;
        }
        for(int i = 0; i < n; i++){
            for(int j = 1; j < m; j++){
                if(matrix[i][j - 1] == 0){
                    leftZeros[i][j] = leftZeros[i][j - 1] + 1;
                }
                else{
                    leftZeros[i][j] = 0;
                }
            }
        }
        for(int i = 1; i < n; i ++){
            for(int j = 0; j < m; j++){
                if(matrix[i - 1][j] == 0){
                    upZeros[i][j] = upZeros[i - 1][j] + 1;
                }
                else{
                    upZeros[i][j] = 0;
                }
            }
        }
        int[][] dp = new int[n][m];
        for(int i = 0; i < n; i++){
            dp[i][0] = matrix[i][0];
        }
        for(int j = 0; j < m; j++){
            dp[0][j] = matrix[0][j];
        }
        for(int i = 1; i < n; i++){
            for(int j = 1; j < m; j++){
                if(matrix[i][j] == 0){
                    dp[i][j] = 0;
                }
                else{
                    dp[i][j] = Math.min(Math.min(leftZeros[i][j], upZeros[i][j]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        int max = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                max = Math.max(max, dp[i][j]);
            }
        }
        return max * max;
    }
}
