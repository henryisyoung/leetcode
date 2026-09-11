package waymo;

/*
LeetCode 1143: Longest Common Subsequence

Given two strings text1 and text2, return the length of their longest common
subsequence.

A subsequence keeps relative order but does not need to be contiguous.

Examples:
  text1="abcde", text2="ace"   -> 3  ("ace")
  text1="abc",   text2="abc"   -> 3
  text1="abc",   text2="def"   -> 0
*/

/*
DP definition:
  dp[i][j] = LCS length of text1[0..i) and text2[0..j)

Transition:
  If text1[i-1] == text2[j-1]:
      dp[i][j] = dp[i-1][j-1] + 1

  Else:
      dp[i][j] = max(dp[i-1][j], dp[i][j-1])

Why:
  When chars match, we can extend the best subsequence before both chars.
  When they do not match, the optimal answer skips one char from either text1
  or text2.

Complexity:
  Time:  O(m * n)
  Space: O(m * n)
*/
public class LongestCommonSubsequence {

    public int longestCommonSubsequence(String text1, String text2) {
        if (text1 == null || text2 == null) return 0;

        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        LongestCommonSubsequence solver = new LongestCommonSubsequence();

        check(solver, "abcde", "ace", 3);
        check(solver, "abc", "abc", 3);
        check(solver, "abc", "def", 0);
        check(solver, "", "abc", 0);
        check(solver, "abc", "", 0);
        check(solver, "bsbininm", "jmjkbkjkv", 1);
        check(solver, "ezupkr", "ubmrapg", 2);

        System.out.println("All tests passed.");
    }

    private static void check(LongestCommonSubsequence solver, String text1, String text2, int expected) {
        int actual = solver.longestCommonSubsequence(text1, text2);
        if (actual != expected) {
            throw new AssertionError("text1=" + text1 + " text2=" + text2
                    + " expected " + expected + " but got " + actual);
        }
        System.out.println("text1=" + text1 + " text2=" + text2 + " -> " + actual);
    }
}
