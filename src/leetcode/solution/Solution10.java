package leetcode.solution;

public class Solution10 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution10 t = new Solution10();
		Integer a = 10;
		Integer b = 10;
		System.out.println(a == b);
		
	}
	
	/***
	'.' Matches any single character.
	'*' Matches zero or more of the preceding element.
	 */
    public boolean isMatchDP(String s, String p) { //DP FASTER
        char[] ss = s.toCharArray(), pp=p.toCharArray();
        int m=ss.length,n=pp.length;
        boolean[][] dp = new boolean[m+1][n+1];
        dp[0][0]=true;
        
        for(int i=0;i<=m;i++){
        	for(int j=1;j<=n;j++){
        		if(pp[j-1]!='*'&&pp[j-1]!='.'){
                    if(i>0 && ss[i-1]==pp[j-1] && dp[i-1][j-1])
                        dp[i][j] = true;
        		}else if(pp[j-1]=='.'){
        			if(i>0&&dp[i-1][j-1])
        				dp[i][j]=true;
        		}else if(j>1){
        			if(dp[i][j-1] || dp[i][j-2])  // match 0 or 1 preceding element
                        dp[i][j] = true;
                    else if(i>0 && (pp[j-2]==ss[i-1] || pp[j-2]=='.') && dp[i-1][j]) // match multiple preceding elements
                    	dp[i][j] = true;
        		}
        	}
        }
        return dp[m][n];
    }
    
         public static boolean isMatch(String s, String p) {
    	          if (p.length() == 0)
    	              return s.length() == 0;
    	  
    	          // length == 1 is the case that is easy to forget.
    	          // as p is subtracted 2 each time, so if original
    	          // p is odd, then finally it will face the length 1
    	          if (p.length() == 1)
    	              return (s.length() == 1) && (p.charAt(0) == s.charAt(0) || p.charAt(0) == '.');
    	 
    	         // next char is not '*': must match current character
    	         if (p.charAt(1) != '*') {
    	             if (s.length() == 0)
    	                 return false;
    	             else
    	                 return (s.charAt(0) == p.charAt(0) || p.charAt(0) == '.')
    	                         && isMatch(s.substring(1), p.substring(1));
    	         }else{
    	             // next char is *
    	             while (s.length() > 0 && (p.charAt(0) == s.charAt(0) || p.charAt(0) == '.')) {
    	                 if (isMatch(s, p.substring(2))) 
    	                     return true;
    	                 s = s.substring(1);
    	             }
    	             return isMatch(s, p.substring(2));
    	         }
    	     }
}
