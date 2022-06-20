package datastructure.dp;

public class LongestValidParentheses {
    public int longestValidParentheses(String s) {
        if (s.length() == 0) return 0;
        int n = s.length();
        int[] dp = new int[n + 1];
        int max = 0;

        for (int i = 1; i <= n; i++) {
            char c = s.charAt(i - 1);
            if (c == '(') continue;
            int j = i - 2 - dp[i - 1];
            if (j >= 0 && s.charAt(j) == '(') {
                dp[i] = dp[i - 1] + 2 + dp[j];
            }
            max = Math.max(max, dp[i]);
        }

        return max;
    }
}
