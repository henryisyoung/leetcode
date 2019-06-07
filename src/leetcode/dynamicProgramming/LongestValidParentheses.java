package leetcode.dynamicProgramming;

public class LongestValidParentheses {
    public int longestValidParentheses(String s) {
        int n = s.length(), maxLen = 0;
        int[] dp = new int[n + 1];
        for(int i=1; i<=n; i++) {
            int j = i-2-dp[i-1];
            if(s.charAt(j - 1)=='(' || j<0 || s.charAt(j)==')')
                dp[i] = 0;
            else {
                dp[i] = dp[i-1]+2+dp[j];
                maxLen = Math.max(maxLen, dp[i]);
            }
        }
        return maxLen;
    }
}
