package leetcode;

public class Solution115 {
    public int numDistinct(String S, String T) {
    	int[][] dp = new int[T.length() + 1][S.length() + 1];  
    	  dp[0][0] = 1;  
    	  for (int i = 1; i <= T.length(); i++) {  
    	    dp[i][0] = 0;  
    	  }  
    	  for (int j = 1; j <= S.length(); j++) {  
    	    dp[0][j] = 1;  
    	  }  
    	  for (int i = 1; i <= T.length(); i++) {  
    	    for (int j = 1; j <= S.length(); j++) {  
    	      dp[i][j] = dp[i][j - 1];  
    	      if (T.charAt(i - 1) == S.charAt(j - 1)) {  
    	        dp[i][j] += dp[i - 1][j - 1];  
    	      }  
    	    }  
    	  }  
    	  return dp[T.length()][S.length()];  
    }  
}
