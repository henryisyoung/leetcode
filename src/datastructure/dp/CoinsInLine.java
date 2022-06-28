package datastructure.dp;

public class CoinsInLine {
    public boolean firstWillWin(int n) {
        int[] dp = new int[n + 1];
        dp[0] = -1;
        return memoFind(dp, n);
    }

    private boolean memoFind(int[] dp, int n) {
        if (dp[n] != 0) return dp[n] == 1;
        if (n <= 2) {
            dp[n] = 1;
            return true;
        }else if (n <= 3) {
            dp[n] = -1;
            return false;
        } else {
            if (memoFind(dp, n - 2) && memoFind(dp, n - 3) || memoFind(dp, n - 3) && memoFind(dp, n - 4)) {
                dp[n] = 1;
                return true;
            } else {
                dp[n] = -1;
                return false;
            }
        }
    }
}
