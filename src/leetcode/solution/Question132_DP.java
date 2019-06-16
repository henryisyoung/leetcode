package leetcode.solution;

public class Question132_DP {
    public int minCut(String s) {
        if(s == null || s.length() == 0) return 0;
        int[] dp = new int[s.length() + 1];
        boolean[][] isPalindrome = isPalindrome(s);
        dp[0] = 0;
        for(int i =1; i  <= s.length(); i++){
            dp[i] = i;
            for(int j = 0; j < i;j++){
                if(isPalindrome[j][i -1]){
                    dp[i] = Math.min(dp[i], dp[j] + 1);
                }
            }
        }
        return dp[s.length()] - 1;
    }

    private boolean[][] isPalindrome(String s) {
        int n = s.length();
        boolean[][] isP = new boolean[n][n];
        for (int i = 0; i < n; i++){
            isP[i][i] = true;
        }
        for(int i = 0; i < n-1;i++){
            if(s.charAt(i) == s.charAt(i+1)){
                isP[i][i+1] = true;
            }
        }
        for(int len = 2; len < n; len++){
            for(int start = 0; len + start < n; start++){
                if(isP[start + 1][start + len - 1] && s.charAt(start) == s.charAt(start+len)){
                    isP[start][start + len] = true;
                }else {
                    isP[start][start + len] = false;
                }
            }
        }
        return isP;
    }
}
