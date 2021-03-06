package leetcode.highFrequency;

public class MaximumSubarray {
    public int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        int max = nums[0];

        for (int i = 1; i < nums.length; i++) {
            if (dp[i - 1] < 0) {
                dp[i] = nums[i];
            } else {
                dp[i] = dp[i - 1] + nums[i];
            }
            if (dp[i] > max) {
                max = dp[i];
            }
        }
        return max;
    }

    public int maxSubArray2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int max = 0, n = nums.length;
        int[] sum = new int[n];
        sum[0] = Math.max(0, nums[0]);
        for (int i = 1; i < n; i++) {
            if (nums[i] < 0) {
                sum[i] = sum[i - 1];
            } else {
                sum[i] = sum[i - 1] + nums[i];
            }
            max = Math.max(sum[i], max);
        }

        return max;
    }
}
