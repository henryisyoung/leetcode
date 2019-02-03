package leetcode.highFrequency;

public class BestTimeToBuyAndSellStock {
    public int maxProfit(int[] prices) {
        if (prices == null || prices.length == 0) {
            return 0;
        }
        int low = prices[0];
        int maxProfit = 0;
        for (int i = 1; i < prices.length; i++) {
            int val = prices[i];
            low = val < low ? val : low;
            maxProfit = Math.max(maxProfit, val - low);
        }
        return maxProfit;
    }
}