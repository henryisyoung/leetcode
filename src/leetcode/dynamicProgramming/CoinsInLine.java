package leetcode.dynamicProgramming;

public class CoinsInLine {
    public boolean firstWillWin(int n) {
        int[] dp = new int[n + 1];
        return search(dp, n);
    }

    private boolean search(int[] dp, int n) {
        if (dp[n] != 0) {
            return dp[n] == 1; // 1 is true
        }
        if (n <= 0) {
            return false;
        }
        if (n <= 2) {
            dp[n] = 1;
            return true;
        }
        if (n == 3) {
            dp[n] = 2; // 2 is false
            return false;
        }
        if((search(dp, n - 2) && search(dp, n - 3)) ||
                (search(dp, n -3) && search( dp, n -4) )) {
            dp[n] = 1;
            return true;
        } else {
            dp[n] = 2;
            return false;
        }
    }
}
