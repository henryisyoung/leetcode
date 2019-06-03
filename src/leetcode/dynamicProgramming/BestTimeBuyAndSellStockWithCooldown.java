package leetcode.dynamicProgramming;

public class BestTimeBuyAndSellStockWithCooldown {
    public int maxProfit(int[] prices) {
        if(prices == null || prices.length == 0) {
            return 0;
        }
        int len = prices.length;
        int[] buy = new int[len + 1];       // before i, for any sequence last action at i is going to be buy
        int[] sell = new int[len + 1];      // before i, for any sequence last action at i is going to be sell
        int[] cooldown = new int[len + 1];  // before i, for any sequence last action at i is going to be cooldown
        buy[0] = Integer.MIN_VALUE;

        for(int i = 1; i < len + 1; i++) {
            buy[i] = Math.max(buy[i - 1], cooldown[i - 1] - prices[i - 1]);     // must sell to get profit
            sell[i] = Math.max(buy[i - 1] + prices[i - 1], sell[i - 1]);
            cooldown[i] = Math.max(sell[i - 1], Math.max(buy[i - 1], cooldown[i - 1]));
        }

        return Math.max(buy[len], Math.max(sell[len], cooldown[len]));
    }
}
