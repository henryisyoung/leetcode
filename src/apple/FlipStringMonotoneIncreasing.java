package apple;

public class FlipStringMonotoneIncreasing {
    public int minFlipsMonoIncr(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i - 1] + (s.charAt(i - 1) - '0');
        }
        int result = Integer.MAX_VALUE;

        for (int i = 0; i <= n; i++) {
            result = Math.min(result, dp[i] + (n - i - (dp[n] - dp[i])));
        }
        return result;
    }
}
