package leetcode.dynamicProgramming;

public class BackpackIV {
    public int backPackIV(int[] nums, int target) {
        int[] dp = new int[target+1];
        dp[0] = 1;
        for (int i = 0; i < nums.length; i++) {
            //从小到大拿多次
            for (int j = 1; j <= target; j++) {
                if (nums[i] == j) dp[j]++;
                else if (nums[i] < j) dp[j] += dp[j - nums[i]];
            }
        }
        return dp[target];
    }
}