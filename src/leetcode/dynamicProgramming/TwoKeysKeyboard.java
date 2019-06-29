package leetcode.dynamicProgramming;

public class TwoKeysKeyboard {
    public int minSteps(int n) {
        int[] dp = new int[n + 1];
        for (int i = n - 1; i > 1; i--){
            dp[i] = i;
            for (int j = 2; j < i; j++) {
                if (i % j == 0) {
                    dp[i] = Math.min(dp[i], dp[j] + i/j);
                }
            }
        }
        return dp[n];
    }

    public int minSteps2(int n) {
        if (n == 1) return 0;
        int result = n;
        for (int i = n - 1; i > 1; i--) {
            if (n % i == 0) {
                result = Math.min(result, minSteps2(n / i) + i);
            }
        }
        return result;
    }
}
