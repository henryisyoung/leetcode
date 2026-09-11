package waymo;

/*
LeetCode 115: Distinct Subsequences

Given two strings s and t, return the number of distinct subsequences of s
that equal t.

A subsequence deletes zero or more characters without changing the order of
the remaining characters.

Examples:
  s="rabbbit", t="rabbit" -> 3
  s="babgbag", t="bag"   -> 5
*/

/*
DP definition:
  dp[i][j] = number of ways s[0..i) can form t[0..j)

Base:
  dp[i][0] = 1
    Empty t can always be formed by deleting all chars from s[0..i).

  dp[0][j] = 0 for j > 0
    Empty s cannot form a non-empty t.

Transition:
  For s[i-1] and t[j-1]:

  1. Always allowed: skip s[i-1]
       dp[i][j] += dp[i-1][j]

  2. If s[i-1] == t[j-1], also allowed: use s[i-1] to match t[j-1]
       dp[i][j] += dp[i-1][j-1]

Complexity:
  Time:  O(m * n)
  Space: O(m * n)
*/
public class DistinctSubsequences {

    public long numDistinct(String s, String t) {
        if (s == null || t == null) return 0;

        int m = s.length(), n = t.length();
        long[][] dp = new long[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = 1;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = dp[i - 1][j]; // skip s[i-1]
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] += dp[i - 1][j - 1]; // use s[i-1]
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        DistinctSubsequences solver = new DistinctSubsequences();

        check(solver, "rabbbit", "rabbit", 3);
        check(solver, "babgbag", "bag", 5);
        check(solver, "abc", "abc", 1);
        check(solver, "abc", "ac", 1);
        check(solver, "abc", "abcd", 0);
        check(solver, "", "", 1);
        check(solver, "abc", "", 1);
        check(solver, "", "a", 0);
        check(solver, "aaaaa", "aa", 10);

        System.out.println("All tests passed.");
    }

    private static void check(DistinctSubsequences solver, String s, String t, long expected) {
        long actual = solver.numDistinct(s, t);
        if (actual != expected) {
            throw new AssertionError("s=" + s + " t=" + t
                    + " expected " + expected + " but got " + actual);
        }
        System.out.println("s=" + s + " t=" + t + " -> " + actual);
    }
}
