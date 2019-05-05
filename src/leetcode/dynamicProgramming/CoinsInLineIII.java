package leetcode.dynamicProgramming;

public class CoinsInLineIII {
    public boolean firstWillWin(int[] values) {
        // write your code here
        int n = values.length;
        int[][] dp = new int[n + 1][n + 1];
        boolean[][] flag = new boolean[n + 1][n + 1];

        int sum = 0;
        for (int now : values) {
            sum += now;
        }
        return sum < 2 * MemorySearch(0, values.length - 1, dp, flag, values);
    }

    int MemorySearch(int left, int right, int[][] dp, boolean[][] flag, int[] values) {

        if (flag[left][right])
            return dp[left][right];
        flag[left][right] = true;
        if (left > right) {
            dp[left][right] = 0;
        } else if (left == right) {
            dp[left][right] = values[left];
        } else if (left + 1 == right) {
            dp[left][right] = Math.max(values[left], values[right]);
        } else {
            int pick_left = Math.min(MemorySearch(left + 2, right, dp, flag, values),
                    MemorySearch(left + 1, right - 1, dp, flag, values)) + values[left];
            int pick_right = Math.min(MemorySearch(left, right - 2, dp, flag, values),
                    MemorySearch(left + 1, right - 1, dp, flag, values)) + values[right];
            dp[left][right] = Math.max(pick_left, pick_right);
        }
        return dp[left][right];
    }
}
