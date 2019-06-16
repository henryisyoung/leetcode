package leetcode.solution;

public class Solution188 {
	
	//http://liangjiabin.com/blog/2015/04/leetcode-best-time-to-buy-and-sell-stock.html
	
	/*
			 * local[i][j] = max(global[i – 1][j – 1] , local[i – 1][j] + diff)
		global[i][j] = max(global[i – 1][j], local[i][j])
		local[i][j]和global[i][j]的区别是：local[i][j]意味着在第i天一定有交易（卖出）发生，
		当第i天的价格高于第i-1天（即diff > 0）时，那么可以把这次交易（第i-1天买入第i天卖出）
		跟第i-1天的交易（卖出）合并为一次交易，即local[i][j]=local[i-1][j]+diff；
		当第i天的价格不高于第i-1天（即diff<=0）时，那么local[i][j]=global[i-1][j-1]+diff，
		而由于diff<=0，所以可写成local[i][j]=global[i-1][j-1]。global[i][j]就是我们所求的前i天最多进行
		k次交易的最大收益，可分为两种情况：如果第i天没有交易（卖出），那么global[i][j]=global[i-1][j]；
		如果第i天有交易（卖出），那么global[i][j]=local[i][j]。
	 */
    public int maxProfit(int k, int[] prices) {
        if (prices.length < 2) return 0;
        
        int days = prices.length;
        if (k >= days) return maxProfit2(prices);
        
        int[][] local = new int[days][k + 1];
        int[][] global = new int[days][k + 1];
        
        for (int i = 1; i < days ; i++) {
            int diff = prices[i] - prices[i - 1];
            
            for (int j = 1; j <= k; j++) {
                local[i][j] = Math.max(global[i - 1][j - 1], local[i - 1][j] + diff);
                global[i][j] = Math.max(global[i - 1][j], local[i][j]);
             }
        }
        
        return global[days - 1][k];
    }
    
    
    public int maxProfit2(int[] prices) {
        int maxProfit = 0;
        
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                maxProfit += prices[i] - prices[i - 1];
            }
        }
        
        return maxProfit;
    }
}
