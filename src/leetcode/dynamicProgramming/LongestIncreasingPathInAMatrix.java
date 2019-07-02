package leetcode.dynamicProgramming;

public class LongestIncreasingPathInAMatrix {
    public int longestIncreasingPath(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return 0;
        }
        int rows = matrix.length, cols = matrix[0].length;
        int[][] dp = new int[rows][cols];
        int max = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (dp[i][j] == 0) {
                    dp[i][j] = memoFind(dp, i, j, matrix);
                    max = Math.max(max, dp[i][j]);
                }
            }
        }
        return max;
    }

    private int memoFind(int[][] dp, int r, int c, int[][] matrix) {
        if (dp[r][c] > 0) return dp[r][c];
        int val = 1;
        int rows = matrix.length, cols = matrix[0].length;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && matrix[nr][nc] < matrix[r][c]) {
                val = Math.max(val, 1 + memoFind(dp, nr, nc, matrix));
            }
        }
        dp[r][c] = val;
        return val;
    }
}
