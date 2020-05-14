package facebook;

public class StoneGame {
    public static int stoneGame(int[] stones) {
        if (stones == null || stones.length == 0) return 0;
        int n = stones.length;
        int[][] dp = new int[n][n];
        int[] sum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            sum[i + 1] = sum[i] + stones[i];
        }

        return stoneHelper(sum, dp, 0, n - 1, new boolean[n][n]);
    }

    private static int stoneHelper(int[] sum, int[][] dp, int left, int right, boolean[][] visited) {
        if (visited[left][right]) return dp[left][right];
        if (left == right) return dp[left][right];
        visited[left][right] = true;
        for (int k = left; k < right; k++) {
            dp[left][right] = Math.max(dp[left][right],
                    stoneHelper(sum, dp, left, k, visited) + stoneHelper(sum, dp, k + 1, right, visited) + sum[right + 1] - sum[left]);
        }
        return dp[left][right];
    }

    public static void main(String[] args) {
        int[] stones = {4, 2, 1, 3};
        int[] stones2 = {2, 3, 9, 8, 4};
        System.out.println(stoneGame(stones));
        System.out.println(stoneGame(stones2));
    }
}
