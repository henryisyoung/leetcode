package leetcode.dynamicProgramming;

public class NimGame {
    public boolean canWinNim(int n) {
        int[] dp = new int[n + 1];
        return canWinHelper(dp, n);
    }

    private boolean canWinHelper(int[] dp, int n) {
        if (dp[n] != 0) {
            return dp[n] == 1;
        }
        if (n <= 3) {
            dp[n] = 1;
            return true;
        }
        boolean result = true;
        for (int i = 2; i <= 6 && i <= n; i++) {
            result &= canWinHelper(dp, n - i);
        }
        dp[n] = result ? 1 : -1;
        return result;
    }

    public static void main(String[] args) {
        NimGame sovler = new NimGame();
        System.out.println(sovler.canWinNim(4));
    }
}
