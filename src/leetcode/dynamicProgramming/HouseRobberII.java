package leetcode.dynamicProgramming;

public class HouseRobberII {
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
        int val1 = robRange(0, n - 2, nums);
        int val2 = robRange(1, n - 1, nums);
        return val1 > val2 ? val1 : val2;
    }

    private int robRange(int start, int end, int[] nums) {
        int[] dp = new int[nums.length - 1];
        dp[0] = nums[start];
        dp[1] = Math.max(nums[start], nums[start + 1]);
        int max = Math.max(dp[0], dp[1]);
        for (int i = start + 2; i <= end; i++) {
            dp[i - start] = Math.max(dp[i - 1 - start], dp[i - 2 - start] + nums[i]);
            max = Math.max(max, dp[i - start]);
        }
        return max;
    }
}
