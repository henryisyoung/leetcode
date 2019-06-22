package leetcode.binarySearch;

import java.util.Arrays;

public class SplitArrayLargestSum {
    public int splitArray(int[] nums, int m) {
        if(nums == null || nums.length == 0) {
            return 0;
        }
        long max = nums[0], sum = 0;
        for (int i : nums) {
            max = Math.max(max, i);
            sum += i;
        }
        long left = max, right = sum;
        while (left < right) {
            long mid = left + (right - left) / 2;
            if (canSplit(nums, m, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return (int) left;
    }

    private boolean canSplit(int[] nums, int m, long sum) {
        long cnt = 1, curSum = 0;
        for (int i = 0; i < nums.length; ++i) {
            curSum += nums[i];
            if (curSum > sum) {
                curSum = nums[i];
                ++cnt;
                if (cnt > m) return false;
            }
        }
        return true;
    }
    public int splitArrayDP(int[] nums, int m) {
        int n = nums.length;
        int[] sums = new int[n + 1];
        int[][] dp = new int[m + 1][n + 1];
        for (int[] arr : dp) {
            Arrays.fill(arr, Integer.MAX_VALUE);
        }
        dp[0][0] = 0;
        for (int i = 1; i <= n; ++i) {
            sums[i] = sums[i - 1] + nums[i - 1];
        }
        for (int i = 1; i <= m; ++i) {
            for (int j = 1; j <= n; ++j) {
                for (int k = i - 1; k < j; ++k) {
                    int val = Math.max(dp[i - 1][k], sums[j] - sums[k]);
                    dp[i][j] = Math.min(dp[i][j], val);
                }
            }
        }
        return dp[m][n];
    }
}
