package uber;

import java.util.*;

public class CoinChangeII {
//    int count;
//    public int change(int amount, int[] coins) {
//        if ((coins == null || coins.length == 0)) {
//            return amount == 0 ? 1 : 0;
//        }
//        count = 0;
//        Arrays.sort(coins);
//        dfsSearchAll(amount, 0, coins, 0);
//        return count;
//    }
//
//    private void dfsSearchAll(int amount, int cur, int[] coins, int pos) {
//        if (amount == cur) {
//            count++;
//            return;
//        }
//        for (int i = pos; i < coins.length; i++) {
//            if (cur + coins[i] > amount) break;
//            System.out.println(cur);
//            dfsSearchAll(amount, cur + coins[i], coins, i);
//        }
//    }
    public int change(int amount, int[] coins) {
        if ((coins == null || coins.length == 0)) {
            return amount == 0 ? 1 : 0;
        }
        int n = coins.length;
        int[][] dp = new int[2][amount + 1];
        for (int i = 0; i <= 1; i++){
            dp[i][0] = 1;
        }
        for (int i = 1; i<= n; i++) {
            for (int j = 0; j <= amount; j++) {
                dp[i % 2][j] = dp[(i - 1) % 2][j];
                if (coins[i - 1] <= j) {
                    dp[i % 2][j] += dp[i % 2][j - coins[i - 1]];
                }
            }
        }
        return dp[n % 2][amount];
    }

    public static List<List<Integer>> changePrintAll(int amount, int[] coins) {
        List<List<List<Integer>>> dp = new ArrayList<>();
        Arrays.sort(coins);
        for (int t = 1; t <= amount; t++) {
            List<List<Integer>> newList = new ArrayList<>();
            for (int i = 0; i < coins.length && coins[i] <= t; i++) {
                if (t == coins[i]) {
                    newList.add(Arrays.asList(t));
                } else {
                    for (List<Integer> list : dp.get(t - coins[i] - 1)){
//                        if (coins[i] >= list.get(0)) {
                            List<Integer> cumList = new ArrayList<>(list);
                            cumList.add(coins[i]);
                            newList.add(cumList);
//                        }
                    }
                }
            }
            dp.add(newList);
        }
        return dp.get(amount - 1);
    }
    public static void main(String[] args) {
        int[] coins = {1,2,5};
        int[] coins2 = {3,5,7,8,9,10,11};
        CoinChangeII solution = new CoinChangeII();
//        System.out.println(solution.change(5, coins));
//        System.out.println(solution.change(500, coins2));

        System.out.println(changePrintAll(5, coins));
    }
}
