package leetcode.dynamicProgramming;

public class JumpGameII {
    public int jump(int[] nums) {
        if(nums == null || nums.length == 0) {
            return 0;
        }
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = 0;
        for (int i = 1; i < n; i++) {
            dp[i] = Integer.MIN_VALUE;
            for (int j = 0; j < i; j++) {
                if(dp[j] != Integer.MAX_VALUE && nums[j] + j >= i) {
                    dp[i] = Math.min(dp[i], dp[j] + 1);
                }
            }
        }
        return dp[n - 1];
    }
    public int jump2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int start = 0, end = 0, count = 0, n = nums.length, max = 0;
        if (n == 1) {
            return 0;
        }
        while (start <= end) {
            count++;
            for (int i = start; i <= end; i++) {
                if (nums[i] + i >= n - 1) {
                    return count;
                }
                if (nums[i] + i > max) {
                    max = nums[i] + i;
                }
            }
            start = end + 1;
            end = max;
        }
        return count;
    }
}
