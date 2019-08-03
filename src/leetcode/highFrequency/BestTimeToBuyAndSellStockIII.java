package leetcode.highFrequency;

public class BestTimeToBuyAndSellStockIII {
    public int maxProfit(int[] prices) {
        if(prices == null || prices.length == 0){
            return 0;
        }
        int n = prices.length;
        int[] left = new int[n];
        int[] right = new int[n];
        int low = prices[0];

        for(int i = 1; i < n; i++){
            left[i] = Math.max(left[i - 1], prices[i] - low);
            low = Math.min(low, prices[i]);
        }

        int high = prices[n - 1];
        for(int i = n - 2; i >= 0; i--){
            right[i] = Math.max(high - prices[i], right[i + 1]);
            high = Math.max(high, prices[i]);
        }

        int max = 0;
        for(int i = 0; i < n; i++){
            if(max < left[i] + right[i]){
                max = left[i] + right[i];
            }
        }
        return max;
    }
    public int maxProfit3(int[] prices) {
        if (prices == null || prices.length == 0) {
            return 0;
        }
        int n = prices.length;
        int[] left = new int[n], right = new int[n];
        int low = prices[0];
        for (int i = 1; i < n; i++) {
            left[i] = Math.max(left[i - 1], prices[i] - low);
            low = Math.min(low, prices[i]);
        }
        int high = prices[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            right[i] = Math.max(right[i + 1], high - prices[i]);
            high = Math.max(high, prices[i]);
        }
        int max = 0;
        for (int i = 0; i < n; i++) {
            max = Math.max(left[i] + right[i], max);
        }
        return max;
    }
}
