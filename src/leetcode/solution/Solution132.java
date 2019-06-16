package leetcode.solution;

public class Solution132 {
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
		boolean[][] isPalindrome = new boolean[s.length()][s.length()];
		for(int i = 0; i < s.length(); i++){
			isPalindrome[i][i] = true;
		}
		for(int i = 0; i < s.length() - 1; i++){
			if(s.charAt(i) == s.charAt(i + 1)){
				isPalindrome[i][i + 1] = true;
			}
		}
		for(int len = 2; len < s.length(); len++){
			for(int start = 0; start + len < s.length(); start++){
				if(isPalindrome[start + 1][start + len - 1] && 
						s.charAt(start)==s.charAt(start+len)){
					isPalindrome[start][start + len] = true;
				}else{
					isPalindrome[start][start + len] = false;
				}
			}
		}
		return isPalindrome;
	}

    	
}
