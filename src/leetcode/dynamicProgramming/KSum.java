package leetcode.dynamicProgramming;

import java.util.Arrays;

public class KSum {
    /**
     Given n distinct positive integers, integer k (k <= n) and a number target.
     Find k numbers where sum is target. Calculate how many solutions there are?
     */
    public int kSum(int[] A, int k, int target) {

//        if (A == null || A.length == 0) {
//            return 0;
//        }
//        int n = A.length;
//        int[][][] dp = new int[n + 1][k + 1][target + 1];
//        for (int i = 0; i <= n; i++) {
//            dp[i][0][0] = 1;
//        }
//        for (int i = 1; i <= n; i++) {
//            for (int j = 1; j <=k; j++) {
//                for (int t = 1; t <= target; t++) {
//                    if (t >= A[i - 1]) {
//                        dp[i][j][t] = dp[i - 1][j - 1][t - A[i - 1]] + dp[i - 1][j][t];
//                    } else {
//                        dp[i][j][t] = dp[i - 1][j][t];
//                    }
//                }
//            }
//        }
//
//        return dp[n][k][target];

        int n = A.length;
        int[][][] dp = new int[n + 1][k + 1][target + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0][0] = 1;
        }
        for (int i = 1; i <= n; i++){
            for (int j = 1; j <= k; j++) {
                for (int t = 1; t <= target; t++) {
                    if(A[i - 1] <= t) {
                        dp[i][j][t] = dp[i - 1][j - 1][t - A[i - 1]] + dp[i - 1][j][t];
                    } else {
                        dp[i][j][t] = dp[i - 1][j][t];
                    }
                }
            }
        }
        return dp[n][k][target];
    }
}
