package waymo;

/*
LeetCode 97: Interleaving String

Given strings s1, s2, and s3, return whether s3 is formed by an interleaving
of s1 and s2.

Interleaving means:
  - all characters from s1 and s2 are used exactly once
  - relative order inside s1 is preserved
  - relative order inside s2 is preserved

Examples:
  s1="aabcc", s2="dbbca", s3="aadbbcbcac" -> true
  s1="aabcc", s2="dbbca", s3="aadbbbaccc" -> false
  s1="",      s2="",      s3=""           -> true
*/

/*
DP definition:
  dp[i][j] = whether s3[0..i+j) can be formed by interleaving
             s1[0..i) and s2[0..j)

Transition:
  The next char in s3 is at index i + j - 1.

  dp[i][j] is true if either:
    1. dp[i-1][j] is true and s1[i-1] == s3[i+j-1]
       We take the latest char from s1.

    2. dp[i][j-1] is true and s2[j-1] == s3[i+j-1]
       We take the latest char from s2.

Base:
  dp[0][0] = true

Complexity:
  Time:  O(m * n)
  Space: O(m * n)
*/
public class InterleavingString {

    public boolean isInterleave(String s1, String s2, String s3) {
        if (s1 == null || s2 == null || s3 == null) return false;
        int m = s1.length(), n = s2.length();
        if (m + n != s3.length()) return false;

        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 && j == 0) continue;
                int k = i + j - 1;

                if (i > 0 && dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(k)) {
                    dp[i][j] = true;
                }
                if (j > 0 && dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(k)) {
                    dp[i][j] = true;
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        InterleavingString solver = new InterleavingString();

        check(solver, "aabcc", "dbbca", "aadbbcbcac", true);
        check(solver, "aabcc", "dbbca", "aadbbbaccc", false);
        check(solver, "", "", "", true);
        check(solver, "", "abc", "abc", true);
        check(solver, "abc", "", "abc", true);
        check(solver, "abc", "def", "adbcef", true);
        check(solver, "abc", "def", "abdecf", true);
        check(solver, "abc", "def", "abdfec", false);
        check(solver, "a", "b", "abc", false);

        System.out.println("All tests passed.");
    }

    private static void check(InterleavingString solver,
                              String s1, String s2, String s3,
                              boolean expected) {
        boolean actual = solver.isInterleave(s1, s2, s3);
        if (actual != expected) {
            throw new AssertionError("s1=" + s1 + " s2=" + s2 + " s3=" + s3
                    + " expected " + expected + " but got " + actual);
        }
        System.out.println("s1=" + s1 + " s2=" + s2 + " s3=" + s3 + " -> " + actual);
    }
}
