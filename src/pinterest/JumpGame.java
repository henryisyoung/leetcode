package pinterest;

import java.util.Map;

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
        int end = 0;
        for (int i = 0; i < nums.length && i <= end; i++) {
            if (nums[i] + i >= nums.length - 1) {
                return true;
            }
            end = Math.max(end, nums[i] + i);
        }
        return false;
    }
}