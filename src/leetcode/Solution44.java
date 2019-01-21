package leetcode;

public class Solution44 {
	public boolean isMatch(String s, String p) { // slowest
        int n = s.length();
        int m = p.length();
        boolean[][] rec = new boolean[n + 1][m + 1];
        rec[0][0] = true;

        for (int i = 1; i <= m; ++i){
            for (int j = 0; j <= n; ++j){
                if (p.charAt(i - 1) != '*'){
                    if (j != 0 && (p.charAt(i - 1) == '?' || p.charAt(i - 1) == s.charAt(j - 1))){
                        rec[j][i] = rec[j - 1][i - 1];
                    }
                }
                else {
                    if (i == 1) rec[j][i] = true;
                    if (rec[j][i - 1] || (j > 0 && rec[j - 1][i])) rec[j][i] = true;
                }
            }
        }

        return rec[n][m];
    }
	public boolean isMatch2(String s, String p) {//slower
        int m = s.length(), n = p.length();
        char[] ws = s.toCharArray();
        char[] wp = p.toCharArray();
        boolean[][] dp = new boolean[m+1][n+1];
        dp[0][0] = true;
        for (int j = 1; j <= n; j++)
            dp[0][j] = dp[0][j-1] && wp[j-1] == '*';
        for (int i = 1; i <= m; i++)
            dp[i][0] = false;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (wp[j-1] == '?' || ws[i-1] == wp[j-1])
                    dp[i][j] = dp[i-1][j-1];
                else if (wp[j-1] == '*')
                    dp[i][j] = dp[i-1][j] || dp[i][j-1];
            }
        }
        return dp[m][n];
    }
	public boolean isMatch3(String s, String p) {
        int saved_p=-1, saved_s=-1;
        int indexP=0;
        for(int indexS=0; indexS<s.length();){

            if(indexP<p.length() && (s.charAt(indexS)==p.charAt(indexP)||p.charAt(indexP)=='?')){
                //match to a single character
                indexP++;
                indexS++;
            }
            else if(indexP<p.length() && p.charAt(indexP)=='*'){
                // go into the * state, we need to save the P next position and save S next position
                // when any mismatch happen, we can revert the search to it previous state '*'
                saved_p=indexP;
                //move the saved_s, next time it should skip current one
                saved_s=indexS+1;
                indexP++;
            }
            else if(saved_p!=-1){
                //means not match, we need to revert 
                indexP=saved_p;
                indexS=saved_s;
            }
            else{
                //means not match, but not wildcard
                return false;
            }
        }
        //examine the left char in the pattern
        //they should all be '*' if any char left
        for(int index=indexP; index<p.length();index++){
            if(p.charAt(index)!='*'){
                return false;
            }
        }
        return true;
    }
}
