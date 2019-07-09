package airbnb;

public class RegularExpressionMatching {
    public static boolean isMatch(String s, String p) {
        char[] ss = s.toCharArray(), pp=p.toCharArray();
        int m=ss.length,n=pp.length;
        boolean[][] dp = new boolean[m+1][n+1];
        dp[0][0]=true;

        for(int i = 0;i<=m;i++){
            for(int j=1;j<=n;j++){
                if (i > 0 && (ss[i - 1] == pp[j - 1] || pp[j - 1] == '.')) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (pp[j - 1] == '*') {
                    if (dp[i][j - 1] || (j > 1 && dp[i][j - 2])) {
                        dp[i][j] = true;
                    } else if (i > 0 && j > 1 && (ss[i - 1] == pp[j - 2] || pp[j - 2] == '.') && dp[i - 1][j]) {
                        dp[i][j] = true;
                    }
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        String s = "", p = "**";
        String s2 = "aaa", p2 = "**";
        String s3 = "", p3 = "**";
        String s4 = "", p4 = "**";
//        System.out.println(isMatch(s, p));

        String s5 = "saaaa", p5 = "s+a*";
        RegularExpressionMatching solver = new RegularExpressionMatching();
        boolean result = solver.isMatch(s5, p5);
        System.out.println(result);
    }
}