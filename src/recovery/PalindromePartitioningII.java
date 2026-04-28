package recovery;

public class PalindromePartitioningII {
    public int minCut(String s) {
        int n = s.length();
        int[] dp = new int[n];
        for (int i = 0; i < n; i++) dp[i] = i;

        for (int i = 1; i <= n; i++) {
            for (int j = i - 1; j >= 0; j--) {
                String str = s.substring(j , i);
                if (isPalindron(str)) {
                    dp[i] = Math.min(dp[i], dp[j] + 1);
                }
            }
        }
        return dp[n]  - 1;
    }

    private boolean isPalindron(String str) {
        int l = 0, r = str.length() - 1;
        while (l < r) {
            if (str.charAt(l++) != str.charAt(r--)) return false;
        }
        return true;
    }
}

