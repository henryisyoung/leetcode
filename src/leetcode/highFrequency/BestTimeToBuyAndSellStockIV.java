package leetcode.highFrequency;

public class BestTimeToBuyAndSellStockIV {
    public int maxProfit(int k, int[] prices) {
        if (prices == null || prices.length == 0) {
            return 0;
        }
        int n = prices.length;
        if(k >= n) {
            return maxProfitHelper(prices);
        }
        int[][] local = new int[n][k + 1];
        int[][] global = new int[n][k + 1];
        for (int i = 1; i < n; i++) {
            int diff = prices[i] - prices[i - 1];
            for (int j = 1; j <= k; j++) {
                local[i][j] = Math.max(global[i - 1][j - 1], local[i-1][j]) + diff;
                global[i][j] = Math.max(global[i - 1][j], local[i][j]);
            }
        }
        return global[n - 1][k];
    }

    private int maxProfitHelper(int[] prices) {
        int max = 0;
        for (int i = 1; i < prices.length; i++) {
            if(prices[i] > prices[i - 1]) {
                max += prices[i] - prices[i - 1];
            }
        }
        return max;
    }
}
