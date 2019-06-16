package leetcode.solution;

public class NumArray {
	   private int[] dp;
	    public NumArray(int[] nums) {
	        dp = new int[nums.length];
	        int sum = 0;
	        for (int i = 0; i < nums.length; i++) {
	            sum += nums[i];
	            dp[i] = sum;
	        }
	    }

	    public int sumRange(int i, int j) {
	        return i == 0 ? dp[j] : dp[j] - dp[i - 1];
	    }
}
