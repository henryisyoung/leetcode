package uber;

public class FourKeysKeyboard {
    public int maxA(int N) {
        int[] dp = new int[N];
        dp[0] = 1;
        for (int i = 1; i < N; i++) {
            dp[i] = dp[i - 1] + 1;
            for (int j = 0; j <= i - 3; j++) {
                dp[i] = Math.max(dp[i], dp[j] * (i - j - 1));
            }
        }
        return dp[N - 1];
    }
}
