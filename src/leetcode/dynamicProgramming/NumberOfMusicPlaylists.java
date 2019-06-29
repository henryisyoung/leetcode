package leetcode.dynamicProgramming;

public class NumberOfMusicPlaylists {
    public int numMusicPlaylists(int N, int L, int K) {
        int[][] dp = new int[L + 1][N + 1];
        int mod = (int) (1e9 + 7);
        dp[0][0] = 1;
        for (int i = 1 ; i <= L; i++) {
            for (int j = 1; j <= L; j++) {
                dp[i][j] = (dp[i - 1][j - 1] * (N - (j - 1)) + dp[i - 1][j] * Math.max(0, j - K)) % mod;
            }
        }
        return dp[L][N];
    }

    public static void main(String[] args) {
        System.out.println(1e9 + 7 > 10000);
    }
}
