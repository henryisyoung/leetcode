package airbnb;

public class RegularExpressionMatching {
    public boolean isMatch(String s, String p) {
        char[] ss = s.toCharArray(), pp=p.toCharArray();
        int m=ss.length,n=pp.length;
        boolean[][] dp = new boolean[m+1][n+1];
        dp[0][0]=true;

        for(int i=0;i<=m;i++){
            for(int j=1;j<=n;j++){
                if(pp[j-1] != '*' && pp[j-1] != '.'){
                    if(i > 0 && ss[i-1] == pp[j-1] && dp[i-1][j-1])
                        dp[i][j] = true;
                } else if (pp[j-1] == '.') {
                    if(i > 0 && dp[i-1][j-1])
                        dp[i][j] = true;
                } else if (j > 1) {
                    if(dp[i][j-1] || dp[i][j-2])  // match 0 or 1 preceding element
                        dp[i][j] = true;
                    else if(i>0 && (pp[j-2]==ss[i-1] || pp[j-2]=='.') && dp[i-1][j]) // match multiple preceding elements
                        dp[i][j] = true;
                }
            }
        }
        return dp[m][n];
    }
}
