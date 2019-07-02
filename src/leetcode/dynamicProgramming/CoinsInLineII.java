package leetcode.dynamicProgramming;

public class CoinsInLineII {
    public boolean firstWillWin(int[] values) {
        // write your code here
        if (values == null || values.length == 0) {
            return false;
        }
        int n = values.length;
        int[] dp = new int[n + 1];
        boolean[] visited = new boolean[n + 1];
        int sum = 0;
        for (int i = 0; i <= n; i++) {
            dp[i] = search(dp, visited, values, i, n); // 剩i个硬币最多拿多少
            if (i != n) {
                sum += values[i];
            }
        }
        return dp[n] > sum / 2;
    }

    private int search(int[] dp, boolean[] visited, int[] values, int i, int n) {
        if (visited[i]) {
            return dp[i];
        } else if (i == 0) {
            visited[i] = true;
            return 0;
        } else if (i == 1) {
            visited[i] = true;
            return values[n - 1];
        } else if (i == 2) {
            visited[i] = true;
            return values[n - 1] + values[n - 2];
        } else if (i == 3) {
            visited[i] = true;
            return values[n - 3] + values[n - 2];
        } else {
            visited[i] = true;
            return Math.max(Math.min(search(dp, visited, values, i - 2, n), search(dp, visited, values, i - 3, n)) + values[n - i],
                    Math.min(search(dp, visited, values, i - 3, n), search(dp, visited, values, i - 4, n)) + values[n - i] + values[n - i + 1]);
        }
    }
}
