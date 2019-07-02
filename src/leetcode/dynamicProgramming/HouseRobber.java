package leetcode.dynamicProgramming;

public class HouseRobber {
    public static int rob(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        int[] dp = new int[2];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        int max = dp[1];
        for (int i = 2; i < n; i++) {
            dp[i % 2] = Math.max(dp[(i - 1) % 2], dp[(i - 2) % 2] + nums[i]);
            max = Math.max(max, dp[i % 2]);
        }
        return max;
    }

    public static void main(String[] args) {
        int[] nums = {2,7,9,3,1};
        System.out.println(rob(nums));
    }
}
