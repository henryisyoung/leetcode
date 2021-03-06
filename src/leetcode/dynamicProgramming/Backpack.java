package leetcode.dynamicProgramming;
//https://segmentfault.com/a/1190000006325321
public class Backpack {
    // 在n个物品中挑选若干物品装入背包，最多能装多满？假设背包的大小为m，每个物品的大小为A[i]
    public static int backPack(int m, int[] A) {
        if (A == null || A.length == 0) return 0;
        int n = A.length;
        boolean[][] dp = new boolean[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = true;
        for (int i = 1; i <= n; i++) {
            for (int t = 1; t <= m; t++) {
                if (t >= A[i - 1]) {
                    dp[i][t] = dp[i - 1][t] || dp[i - 1][t - A[i - 1]];
                } else {
                    dp[i][t] = dp[i - 1][t];
                }
            }
        }
        for (int i = m ;i >=0 ; i--) {
            if (dp[n][i]) return i;
        }
        return 0;
    }

    public static void main(String[] args) {
        int[] A = {3,4,8,5};
        System.out.println(backPack(10, A));
    }
}