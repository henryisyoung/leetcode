package leetcode;

public class Solution91 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution91 t = new Solution91();
		
		System.out.println(t.numDecodings("12"));
	}
	
    public int numDecodings(String s) {
    	if(s == null || s.isEmpty() || s.charAt(0) == '0') return 0;
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for(int i = 2; i <= n;i++){
        	int prev = s.charAt(i - 2) - '0', cur = s.charAt(i - 1) - '0';
        	
        	if((prev == 0 && cur ==0) || (cur == 0 && (prev *10 + cur) > 26) ){
        		return 0;
        	}else if(prev == 0 || (prev *10 + cur) > 26 ){
        		dp[i] = dp[i - 1];
        	}else if(cur == 0){
        		dp[i] = dp[i - 2];
        	}else{
        		
        		dp[i] = dp[i - 1] + dp[i - 2];
        	}
        }
        return dp[n];
    }
}
