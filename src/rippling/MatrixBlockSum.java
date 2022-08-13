package rippling;

public class MatrixBlockSum {
    // 1314
    public int[][] matrixBlockSum(int[][] mat, int k) {
        int rows = mat.length, cols = mat[0].length;
        int[][] rangeSum = new int[rows + 1][cols + 1];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rangeSum[i + 1][j + 1] = rangeSum[i + 1][j] + rangeSum[i][j + 1] - rangeSum[i][j] + mat[i][j];
            }
        }
        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int r1 = Math.max(0, i - k), c1 = Math.max(0, j - k);
                int r2 = Math.min(rows, i + k + 1), c2 = Math.min(cols, j + k + 1);
                result[i][j] = rangeSum[r2][c2] - rangeSum[r1][c2] - rangeSum[r2][c1] + rangeSum[r1][c1];
            }
        }
        return result;

        /*
        int m = mat.length, n = mat[0].length;
        int[][] rangeSum = new int[m + 1][n + 1];
        for (int i = 0; i < m; ++i)
            for (int j = 0; j < n; ++j)
                rangeSum[i + 1][j + 1] = rangeSum[i + 1][j] + rangeSum[i][j + 1] - rangeSum[i][j] + mat[i][j];

        int[][] ans = new int[m][n];
        for (int i = 0; i < m; ++i)
            for (int j = 0; j < n; ++j) {
                int r1 = Math.max(0, i - K), c1 = Math.max(0, j - K), r2 = Math.min(m, i + K + 1), c2 = Math.min(n, j + K + 1);
                ans[i][j] = rangeSum[r2][c2] - rangeSum[r2][c1] - rangeSum[r1][c2] + rangeSum[r1][c1];
            }
        return ans;
         */
    }
}
