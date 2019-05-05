package leetcode.dynamicProgramming;

public class Backpack {
    // 在n个物品中挑选若干物品装入背包，最多能装多满？假设背包的大小为m，每个物品的大小为A[i]
    public int backPack(int m, int[] A) {
        // write your code here
        if (m <= 0 || A == null || A.length == 0) {
            return 0;
        }
        int n = A.length;
        boolean[][] dp = new boolean[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }
        for (int i = 1; i <= n; i++) {
            for (int size = 1; size <= m; size++) {
                dp[i][size] = dp[i - 1][size];
                if (size >= A[i - 1] && dp[i - 1][size - A[i - 1]]) {
                    dp[i][size] = true;
                }
            }
        }
        for (int i = m; i >= 0; i--) {
            if (dp[n][i]) {
                return i;
            }
        }
        return 0;
    }

    public int backPack2(int m, int[] A) {
        int[] dp = new int[m + 1];

        for(int i = 0; i < A.length; i++) {
            for (int size = m; size >= A[i]; size--) {
                dp[size] = Math.max(dp[size], dp[size - A[i]] + A[i]);
            }
        }
        return dp[m];
    }

    public int backPack3(int capacity, int[] A) {
        if(A == null || A.length == 0 || capacity <= 0) {
            return 0;
        }
        int n = A.length;
        boolean[][] dp = new boolean[n + 1][capacity + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }
        for (int i = 1; i <= n; i++) {
            for (int t = 1; t <= capacity; t++) {
                if (t >= A[i - 1]) {
                    dp[i][t] = dp[i - 1][t - A[i - 1]] || dp[i - 1][t];
                } else {
                    dp[i][t] = dp[i - 1][t];
                }
            }
        }
        for (int i = capacity; i >= 0; i--) {
            if (dp[n][i]) {
                return i;
            }
        }
        return 0;
    }
}