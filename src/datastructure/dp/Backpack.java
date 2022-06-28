package datastructure.dp;

import java.util.Arrays;

public class Backpack {
    public int backPack(int capacity, int[] items) {
        if (items == null || items.length == 0) return 0;
        Arrays.sort(items);
        int len = items.length;
        boolean[][] dp = new boolean[len + 1][capacity + 1];
        for (int i = 0; i <= len; i++) {
            dp[i][0] = true;
        }
        for (int i = 1; i <= len; i++) {
            for (int j = 1; j <= capacity; j++) {
                if (j >= items[i - 1]) {
                    dp[i][j] = dp[i - 1][j - items[i - 1]] || dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        for (int i = capacity; i >=0; i--) {
            if (dp[len][i]) return i;
        }
        return 0;
    }

    // Backpack II
    public int backPackII(int size, int[] items, int[] values) {
        // write your code here
        if (items == null || items.length == 0) return 0;
        int len = items.length;
        int[][] dp = new int[len + 1][size + 1];
        for (int i = 1; i <= len; i++) {
            for (int j = 1; j <= size; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j >= items[i - 1]) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - items[i - 1]] + values[i - 1]);
                }
            }
        }
        return dp[len][size];
    }

    public static void main(String[] args) {

    }
}
