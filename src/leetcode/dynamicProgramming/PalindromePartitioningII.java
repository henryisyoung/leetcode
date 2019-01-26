package leetcode.dynamicProgramming;

public class PalindromePartitioningII {
    public int minCut(String s) {
        if(s == null || s.length() < 2) {
            return 0;
        }
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            dp[i] = i;
        }
        boolean[][] isPalindrome = isPalindromeHelper(s);
        for(int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if(isPalindrome[j][i - 1]) {
                    dp[i] = Math.min(dp[i], dp[j] + 1);
                }
            }
        }
        return dp[n] - 1;
    }

    private boolean[][] isPalindromeHelper(String s) {
        int n = s.length();
        boolean[][] isPalindrome = new boolean[n][n];
        for(int i = 0; i < s.length(); i++){
            isPalindrome[i][i] = true;
        }
        for(int i = 0; i < s.length() - 1; i++){
            if(s.charAt(i) == s.charAt(i + 1)){
                isPalindrome[i][i + 1] = true;
            }
        }
        for (int len = 2; len < n; len++){
            for(int start = 0; start + len < n; start++){
                if(isPalindrome[start + 1][start + len - 1] && s.charAt(start) == s.charAt(start + len)) {
                    isPalindrome[start][start + len] = true;
                }
            }
        }
        return isPalindrome;
    }
}
