package leetcode.dynamicProgramming;

public class MaximumSubarray {
    public int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int n = nums.length, max = nums[0];
        int[] dp = new int[2];
        dp[0] = nums[0];
        for (int i = 1; i < n; i++) {
            if (dp[(i - 1) % 2] < 0) {
                dp[i % 2] = nums[i];
            } else {
                dp[i % 2] = dp[(i - 1) % 2] + nums[i];
            }
            max = Math.max(max, dp[i % 2]);
        }
        return max;
    }
}
