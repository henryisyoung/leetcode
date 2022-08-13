package uber.phone.leetcode;

public class DecodeWays {
    public int numDecodings(String s) {
        if(s == null || s.isEmpty() || s.charAt(0) == '0') return 0;
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for(int i = 2; i <= n;i++){
            int prev = s.charAt(i - 2) - '0', cur = s.charAt(i - 1) - '0';
            if((prev == 0 && cur ==0) || (cur == 0 && (prev *10 + cur) > 26) ){ //无法成数
                return 0;
            }else if(prev == 0 || (prev *10 + cur) > 26 ){
                // 末尾一位成一个数
                dp[i] = dp[i - 1];
            }else if(cur == 0){  ///末尾2位成一个数
                dp[i] = dp[i - 2];
            }else{  // 末尾两位可以成2个数
                dp[i] = dp[i - 1] + dp[i - 2];
            }
        }
        return dp[n];
    }
}
