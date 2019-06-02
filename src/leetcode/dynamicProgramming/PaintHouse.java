package leetcode.dynamicProgramming;

public class PaintHouse {
    public int minCost(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0] == null || costs[0].length == 0) {
            return 0;
        }
        int n = costs.length;
        int[][] dp = new int[2][3];
        for (int i = 0; i < 3; i++) {
            dp[0][i] = costs[0][i];
        }
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < 3; j++) {
                if (j == 0) {
                    dp[i % 2][j] = Math.min(dp[(i - 1) % 2][1], dp[(i - 1) % 2][2]) + costs[i][0];
                } else if (j == 1) {
                    dp[i % 2][j] = Math.min(dp[(i - 1) % 2][0], dp[(i - 1) % 2][2]) + costs[i][1];
                } else {
                    dp[i % 2][j] = Math.min(dp[(i - 1) % 2][0], dp[(i - 1) % 2][1]) + costs[i][2];
                }
            }
        }
        return Math.min(dp[(n - 1) % 2][0], Math.min(dp[(n - 1) % 2][1], dp[(n - 1) % 2][2]));
    }

    public static void main(String[] args) {
        PaintHouse solver = new PaintHouse();
        System.out.println(solver.minCost(new int[][]{{17,2,17},{16,16,5},{14,3,19}}));
    }
}
