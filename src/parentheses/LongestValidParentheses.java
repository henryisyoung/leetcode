package parentheses;

public class LongestValidParentheses {
    public int longestValidParentheses(String s) {
        if (s == null || s.length() < 2) return 0;
        int n = s.length();
        int[] dp = new int[n + 1];
        int max = 0;
        for (int i = 1; i <= n; i++) {
            if (s.charAt(i - 1) == '(') continue;
            int j = i - 2 - dp[i - 1];
            if (j >= 0 && s.charAt(j) == '(') {
                dp[i] = dp[j] + 2 + dp[i - 1];
            }
            max = Math.max(max, dp[i]);
        }
        return max;
    }
}
