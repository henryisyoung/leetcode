package leetcode.dynamicProgramming;

public class PartitionArrayForMaximumSum {
    public int maxSumAfterPartitioning(int[] A, int K) {
        if (A == null || A.length == 0) {
            return 0;
        }
        int len = A.length;
        int[] dp = new int[len + 1];
        for (int i = 0; i < len; i++) {
            int m = A[i];
            for (int j = 0; j < K && i - j >= 0; j++) {
                m = Math.max(m, A[i - j]);
                dp[i + 1] = Math.max(dp[i + 1], dp[i - j] + m * (j+1));
            }
        }
        return dp[len];
    }
}
