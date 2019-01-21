package leetcode;

public class Solution121 {
    public int maxProfit(int[] prices) {
        if(prices == null || prices.length == 0){
        	return 0;
        }
        int[] dp = new int[prices.length];
        dp[0] = 0;
        int low = prices[0], max = 0;
        for(int i = 1; i < prices.length; i++){
        	low = Math.min(low, prices[i]);
        	dp[i] = prices[i] - low;
        	max = Math.max(max, dp[i]);
        }
        return max;
    }
}
