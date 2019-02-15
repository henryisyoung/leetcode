package leetcode.dynamicProgramming;

public class CopyBooks {
    public int copyBooks(int[] pages, int k) {
        if (pages == null || pages.length == 0 || k <= 0) {
            return 0;
        }
        int n = pages.length, result = 0;
        int[][] timeCost = calTime(pages);
        int[][] dp = new int[n + 1][k + 1]; // dp[j][t] 前j本书被t个人抄的最小时间
        if (n <= k) {
            for (int i : pages) {
                result = Math.max(i, result);
            }
            return result;
        }

        for (int i = 1; i <= k; i++) {
            dp[1][i] = 1;
        }

        for (int i = 1; i <= n; i++) {
            dp[i][1] = timeCost[0][i - 1];
        }

        for (int people = 2; people <= k; people++) {
            for (int book = people; book <= n; book++) {
                dp[book][people] = Integer.MAX_VALUE;
                for (int j = 1; j < book; j++) {
                    if (Math.max(dp[j][people - 1], timeCost[j - 1][book - 1]) < dp[book][people]) {
                        dp[book][people] = Math.max(dp[j][people - 1], timeCost[j - 1][book - 1]);
                    }
                }
            }
        }
        return dp[n][k];
    }

    private int[][] calTime(int[] pages) {
        int n = pages.length;
        int[][] timeCost = new int[n][n];
        int[] sum = new int[n];
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                sum[i] = pages[i];
            } else {
                sum[i] = pages[i] + sum[i - 1];
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i == j) {
                    timeCost[i][j] = pages[i];
                } else {
                    if (i == 0) {
                        timeCost[i][j] = sum[j];
                    } else {
                        timeCost[i][j] = sum[j] - sum[i-1];
                    }
                }
            }
        }
        return timeCost;
    }
}
