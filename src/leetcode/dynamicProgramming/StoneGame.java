package leetcode.dynamicProgramming;

public class StoneGame {


    public int stoneGame(int[] A) {
        if (A == null || A.length == 0) {
            return 0;
        }
        int n = A.length;
        int[][] dp = new int[n][n];
        boolean[][] visited = new boolean[n][n];
        int[][] sum = new int[n][n];
        for (int i = 0; i < n; i++) {
            sum[i][i] = A[i];
            for (int j = i + 1; j < n; j ++) {
                sum[i][j] = sum[i][j - 1] + A[j];
            }
        }
        return search(A, dp, sum, 0, n - 1, visited);
    }

    private int search(int[] nums, int[][] dp, int[][] sum, int left, int right, boolean[][] visited) {
        if (visited[left][right]) {
            return dp[left][right];
        }
        visited[left][right] = true;
        if (left == right) {
            return dp[left][right];
        }
        dp[left][right] = Integer.MAX_VALUE;
        for (int k = left; k < right; k++) {
            dp[left][right] = Math.min(dp[left][right], search(nums, dp, sum, left, k, visited) +
                    search(nums, dp, sum, k + 1, right, visited) + sum[left][right]);
        }
        return dp[left][right];
    }
}
