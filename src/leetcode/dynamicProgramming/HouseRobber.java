package leetcode.dynamicProgramming;

public class HouseRobber {
    public int rob(int[] nums) {
        int max = 0;
        if (nums == null || nums.length == 0) {
            return max;
        }
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        if (n == 2) {
            return Math.max(nums[0], nums[1]);
        }
        int[] dp = new int[2];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        max = Math.max(dp[0], dp[1]);
        for (int i = 2; i < n; i++) {
            dp[i % 2] = Math.max(dp[(i - 2) % 2] + nums[i], dp[(i - 1) % 2]);
            max = Math.max(max, dp[i % 2]);
        }
        return max;
    }
}
