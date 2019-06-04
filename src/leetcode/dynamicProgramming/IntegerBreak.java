package leetcode.dynamicProgramming;

public class IntegerBreak {
    public static int integerBreak(int n) {
        if (n < 2) {
            return 0;
        }
        int[] dp = new int[n + 1];
        dp[1] = 0;
        dp[2] = 1;
        for (int i = 3; i <= n; i++) {
            dp[i] = (i / 2) * (i - i / 2);
            for (int j = 2; j < i; j++) {
                dp[i] = Math.max(dp[i], dp[j] * (i - j));
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        System.out.println(integerBreak(10));
    }
}
