package leetcode.dynamicProgramming;

public class OutOfBoundaryPaths {
    public static int findPaths(int m, int n, int N, int i, int j) {
        long[][][] dp = new long[N + 1][m][n];
        for (int k = 1; k <= N; ++k) {
            for (int x = 0; x < m; ++x) {
                for (int y = 0; y < n; ++y) {
                    long v1 = (x == 0) ? 1 : dp[k - 1][x - 1][y];
                    long v2 = (x == m - 1) ? 1 : dp[k - 1][x + 1][y];
                    long v3 = (y == 0) ? 1 : dp[k - 1][x][y - 1];
                    long v4 = (y == n - 1) ? 1 : dp[k - 1][x][y + 1];
                    dp[k][x][y] = (v1 + v2 + v3 + v4) % 1000000007;
                }
            }
        }
        return (int) dp[N][i][j];
    }

    public static void main(String[] args) {
        int m = 2, n = 2, N = 2, i = 0, j = 0;
        System.out.println(findPaths(m, n, N, i, j));
    }
}
