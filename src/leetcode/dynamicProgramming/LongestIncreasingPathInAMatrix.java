package leetcode.dynamicProgramming;

public class LongestIncreasingPathInAMatrix {
    public int longestIncreasingPath(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return 0;
        }
        int rows = matrix.length, cols = matrix[0].length, max = 0;
        int[][] dp = new int[rows][cols];
        int[][] isVisted = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dp[i][j] = search(i, j, dp, isVisted, matrix);
                max = Math.max(max, dp[i][j]);
            }
        }
        return max;
    }

    private int search(int i, int j, int[][] dp, int[][] isVisted, int[][] matrix) {
        if (isVisted[i][j] > 0) {
            return dp[i][j];
        }
        int rows = isVisted.length, cols = isVisted[0].length;
        int ans = 1;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nRow = i + dir[0], nCol = j + dir[1];
            if (nRow >= 0 && nRow < rows && nCol >= 0 && nCol < cols) {
                if (matrix[nRow][nCol] < matrix[i][j]) {
                    ans = Math.max(ans, search(nRow, nCol, dp, isVisted, matrix) + 1);
                }
            }
        }
        dp[i][j] = ans;
        isVisted[i][j] = 1;
        return dp[i][j];
    }
}
