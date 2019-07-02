package google;

public class StoneGame {
    public static int stoneGame(int[] A) {
        // write your code here
        if (A == null || A.length == 0) {
            return 0;
        }
        int n = A.length;
        int[][] sum = new int[n][n];
        for (int i = 0; i < n; i++) {
            sum[i][i] = A[i];
            for (int j = i + 1; j < n; j ++) {
                sum[i][j] = sum[i][j - 1] + A[j];
            }
        }
        int[][] dp = new int[n][n];
        boolean[][] visited = new boolean[n][n];
        return memoSearch(0, n - 1, sum, dp, visited);
    }

    private static int memoSearch(int i, int j, int[][] sum, int[][] dp, boolean[][] visited) {
        if (visited[i][j]) return dp[i][j];
        visited[i][j] = true;
        if (i == j) {
            return dp[i][j];
        } else {
            dp[i][j] = Integer.MAX_VALUE;
            for (int k = i; k < j; k++) {
                dp[i][j] = Math.min(dp[i][j],
                        memoSearch(i, k, sum, dp, visited) + memoSearch(k + 1, j, sum, dp, visited)  + sum[i][j]);
            }
            return dp[i][j];
        }
    }

    public static void main(String[] args) {
        int[] A = {4, 1, 1, 4};
        System.out.println(stoneGame(A));
    }
}
