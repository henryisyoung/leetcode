package pinterest;

import java.util.Map;

public class  LongestIncreasingPathInMatrix {
    public int longestIncreasingPath(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return 0;
        }
        int m = matrix.length, n = matrix[0].length;
        int[][] dp = new int[m][n];
        int max = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j] = searchList(matrix, i, j, dp);
                max = Math.max(max, dp[i][j]);
            }
        }
        return max;
    }

    private int searchList(int[][] matrix, int r, int c, int[][] dp) {
        if (dp[r][c] > 0) {
            return dp[r][c];
        }
        int m = matrix.length, n = matrix[0].length;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        int result = 1;
        for (int[] dir : dirs) {
            int nR = r + dir[0], nC = c + dir[1];
            if (nR >= 0 && nR < m && nC >= 0 && nC < n && matrix[nR][nC] < matrix[r][c]) {
                result = Math.max(result, 1 + searchList(matrix, nR, nC, dp));
            }
        }
        dp[r][c] = result;
        return result;
    }
}
