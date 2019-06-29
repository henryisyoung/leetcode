package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class ColaMachine {
    boolean result = false;
    public boolean canRange(int[][] nums, int[] v) {
        dfsSearchAll( 0, 0, v[0], v[1], nums, 0);
        return result;
    }

    private void dfsSearchAll(int l, int r, int sumL, int sumR, int[][] nums, int pos) {
        if (l >= sumL && r <= sumR) {
            result = true;
            return;
        }
        for (int i = pos; i < nums.length; i++) {
            int[] cur = nums[i];
            if (r + cur[1] > sumR) break;
            dfsSearchAll(l + cur[0], r + cur[1], sumL, sumR, nums, i + 1);
        }
    }

    class Soda{
        int lower, upper;
        public Soda(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    public static boolean dfs(List<Soda> sodas, int volumeLower, int volumeUpper,
                              int targetLower, int targetUpper, Map<String, Boolean> memo) {

        Boolean val = memo.get(volumeLower + "-" + volumeUpper);
        if (val != null) {
            return val;
        }

        if (volumeLower >= targetLower && volumeUpper <= targetUpper) {
            return true;
        }
        if (volumeUpper > targetUpper) {
            return false;
        }
        // if (volumeUpper - volumeLower > targetUpper - targetLower) return false;
        for (Soda soda : sodas) {
            if (dfs(sodas, volumeLower + soda.lower, volumeUpper + soda.upper, targetLower, targetUpper, memo)) {//false的子问题会重复计算
                memo.put(volumeLower + "-" + volumeUpper, true);
                return true;
            }
        }

        memo.put(volumeLower + "-" + volumeUpper, false);
        return false;
    }

    public static boolean coke(List<List<Integer>> buttons, List<Integer> target) {
        int m = target.get(0);
        int n = target.get(1);
        boolean[][] dp = new boolean[m + 1][n + 1];

        //Init
        for (int i = 0; i <= m; ++i) {
            for (int j = 0; j <= n; ++j) {
                for (List<Integer> button: buttons) {
                    if (i <= button.get(0) && j >= button.get(1)) {
                        dp[i][j] = true;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i <= m; ++i) {
            for (int j = 0; j <= n; ++j) {
                for (List<Integer> button: buttons) {
                    int preL = i - button.get(0);
                    int preR = j - button.get(1);
                    if (preL >= 0 && preR >= 0 && dp[preL][preR]) {
                        dp[i][j] = true;
                        break;
                    }
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        int[][] nums = {{100, 120}, {200, 240}, {400, 410}};
        int[] v = {100, 110};
        ColaMachine solver = new ColaMachine();
        System.out.println(solver.canRange(nums, v));
    }
}
