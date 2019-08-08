package snap;

public class BestTimeToBuyAndSellStockWithTransactionFee {
    public int maxProfit(int[] prices, int fee) {
        int sold = 0, hold = -prices[0];
        for (int i = 1; i < prices.length; i++) {
            sold = Math.max(sold, hold + prices[i] - fee);
            hold = Math.max(hold, sold - prices[i]);
        }
        return sold;
    }
}
