package uber;

import java.util.*;

public class StudentAttendanceRecordII {
    public int checkRecord(int n) {
        int[][] memo = new int[n + 1][6];
        return memoSearch(n, 1, 2, memo);
    }

    private int memoSearch(int n, int A, int L, int[][] memo) {
        if (n == 0) return 1;
        int key = A * 3 + L;
        if (memo[n][key] > 0) return memo[n][key];
        long ans = 0;
        ans += memoSearch(n - 1, A, 2, memo);
        if (A > 0) {
            ans += memoSearch(n - 1, 0, 2, memo);
        }
        if (L > 0) {
            ans += memoSearch(n - 1, A, L - 1, memo);
        }
        memo[n][key] = (int) (ans % (Math.pow(10, 9) + 7));
        return memo[n][key];
    }
//    先将A排除在外， 只考虑P和L。那么此时就只有以下3种情况：
//
//    以P结尾
//    以PL结尾
//    以PLL结尾
//    dp[i]表示长度为i的字符串中不包含'A'的合法串的数量，dp[i]=dp[i-1]+dp[i-2]+dpi-3。
//    由于字符串中只能有一个'A'，随后枚举i，假设i处为'A'，由dp[i-1] * dp[n-i]，即i处左右两侧子串数量乘积，最后加上dpn。
    public int checkRecordDP(int n) {
        long[] dp = new long[n + 1];
        int MOD = 1000000007;
        dp[0] = 1;
        if (n >= 1) {
            dp[1] = 2;
        }
        if (n >= 2) {
            dp[2] = 4;
        }

        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 3] + dp[i - 2] + dp[i - 1];
            dp[i] %= MOD;
        }
        long result = 0;
        for (int i = 1; i <= n; i++) {
            result += dp[i - 1] * dp[n - i];
            result %= MOD;
        }
        return (int)((result + dp[n]) % MOD);
    }
    public static void main(String[] args) {
        StudentAttendanceRecordII solver = new StudentAttendanceRecordII();
        System.out.println(solver.checkRecord(2));
        System.out.println(solver.checkRecord(3));
    }
}
