package snowflake;

public class NumberOfWaysToFormTargetString {
    private static final int MOD = 1_000_000_007;

    // dp[i] = ways to form target[0..i-1] using the current column and everything
    // to its right. We sweep columns right-to-left, and for each column update i
    // from high to low so dp[i+1] still refers to "next column" when we read it.
    public int numWays(String[] words, String target) {
        int m = words[0].length();
        int n = target.length();

        int[][] cnt = new int[m][26];
        for (String w : words) {
            for (int k = 0; k < m; k++) {
                cnt[k][w.charAt(k) - 'a']++;
            }
        }

        long[] dp = new long[n + 1];
        dp[n] = 1;

        for (int k = m - 1; k >= 0; k--) {
            for (int i = 0; i < n; i++) {
                int c = target.charAt(i) - 'a';
                dp[i] = (dp[i] + cnt[k][c] * dp[i + 1]) % MOD;
            }
        }
        return (int) dp[0];
    }
}
