package google.vo;

public class LongesIncreasingPathMatrix {
    public int longestIncreasingPath(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return 0;
        }        int rows = matrix.length, cols = matrix[0].length;
        int max = 0;

        int[][] dp = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (dp[i][j] == 0) {
                    dp[i][j] = findAll(i, j, dp, matrix);
                    max = Math.max(max, dp[i][j]);
                }
            }
        }
        return max;
    }

    private int findAll(int r, int c, int[][] dp, int[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        if (dp[r][c] != 0) return dp[r][c];
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        dp[r][c] = 1;
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && matrix[nr][nc] < matrix[r][c]) {
                dp[r][c] = Math.max(dp[r][c], 1 + findAll(nr, nc, dp, matrix));
            }
        }
        return dp[r][c];
    }
}
