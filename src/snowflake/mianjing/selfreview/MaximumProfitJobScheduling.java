package snowflake.mianjing.selfreview;

import java.util.Arrays;

public class MaximumProfitJobScheduling {
    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i] = new int[]{startTime[i], endTime[i], profit[i]};
        }
        Arrays.sort(endTime);
        Arrays.sort(jobs, (a, b) -> (a[1] - b[1]));
        int max = 0;
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            int curStart = jobs[i - 1][0], curProfit = jobs[i - 1][2];
            dp[i] = curProfit;
            int lastIndex = binaryFind(endTime, curStart);
            int j = lastIndex == -1 ? 0 : lastIndex + 1;
            dp[i] = Math.max(dp[i], dp[j] + curProfit);
            dp[i] = Math.max(dp[i], dp[i - 1]);
            max = Math.max(dp[i], max);
        }
        return max;
    }

    private int binaryFind(int[] times, int time) {
        int left = 0, right = times.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (times[mid] > time) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (times[right] <= time) return right;
        if (times[left] <= time) return left;
        return -1;
    }
}
