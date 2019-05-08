package pinterest;

public class JumpGame {
    public boolean canJumpDP(int[] nums) {
        if (nums == null || nums.length == 0) {
            return true;
        }
        int n = nums.length;
        boolean[] dp = new boolean[n];
        dp[0] = true;
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && nums[j] + j >= i) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n - 1];
    }

    public boolean canJumpGreedy(int[] nums) {
        if (nums == null || nums.length == 0) {
            return true;
        }
        int n = nums.length, end = 0;
        for (int i = 0; i < n && i <= end; i++) {
            if (nums[i] + i >= end) {
                end = nums[i] + i;
            }
            if (end >= n - 1) {
                return true;
            }
        }
        return false;
    }
}