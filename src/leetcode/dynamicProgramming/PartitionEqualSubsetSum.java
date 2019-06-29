package leetcode.dynamicProgramming;

import java.util.Arrays;

public class PartitionEqualSubsetSum {
    public static boolean canPartition(int[] nums) {
        if (nums == null || nums.length == 0) {
            return false;
        }
        int sum = 0;
        for (int i : nums) {
            sum += i;
        }
        if (sum % 2 == 1) return false;
        sum /= 2;
        int n = nums.length;
        boolean[][] dp = new boolean[n + 1][sum + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }
        Arrays.sort(nums);
        for (int i = 1; i <= n; i++) {
            for (int size = 1; size <= sum; size++) {
                dp[i][size] = dp[i - 1][size];
                if (size >= nums[i - 1] && dp[i - 1][size - nums[i - 1]]) {
                    dp[i][size] = true;
                }
            }
        }
        for (int i = n; i >= 0; i--) {
            if (dp[i][sum]) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        int[] nums = {1, 5, 11, 5};
        System.out.println(canPartition(nums));
    }
}
