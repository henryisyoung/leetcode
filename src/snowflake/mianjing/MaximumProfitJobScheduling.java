package snowflake.mianjing;

import java.util.Arrays;
import java.util.Comparator;

public class MaximumProfitJobScheduling {
    public static int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int len = startTime.length;
        int[][] jobs = new int[len][3];
        for (int i = 0; i < len; i++) {
            jobs[i] = new int[]{startTime[i], endTime[i], profit[i]};
        }
        Arrays.sort(jobs, Comparator.comparingInt(a -> a[1]));
        Arrays.sort(endTime);
        int[] dp = new int[len];
        dp[0] = jobs[0][2];
        int max = 0 ;
        for (int i = 1; i < len; i++) {
            int start = jobs[i][0];
            int index = binaryFind(start, endTime);
            dp[i] = jobs[i][2];
            if (index != -1) {
                dp[i] = Math.max(dp[index] + jobs[i][2], dp[i]);
            }
            dp[i] = Math.max(dp[i], dp[i - 1]);
            max = Math.max(max, dp[i]);
        }
        return max;
    }

    private static int binaryFind(int time, int[] times) {
        int left = 0, right = times.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (times[mid] > time) right = mid;
            else left = mid;
        }
        if (times[right] <= time) return right;
        if (times[left] <= time) return left;
        return -1;
    }
}