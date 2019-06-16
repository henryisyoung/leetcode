package leetcode.solution;

public class Solution343 {
    public int integerBreak(int n) {
        if(n < 2) return 0;
        else if(n == 2) return 1;
        else if(n == 3) return 2;
        int[] dp = new int[n + 1];
        dp[2] = 1;
        dp[3] = 2;
        for(int i = 4; i <= n; i++){
        	int max = Integer.MIN_VALUE;
        	for(int j = 2; j <= i - j; j++){
        		max = Math.max(max, dp[j]*dp[i - j]);
        	}
        	dp[i] = max;
        }
        return dp[n];
    }
}
