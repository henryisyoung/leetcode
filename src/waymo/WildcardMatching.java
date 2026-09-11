package waymo;

/*
LeetCode 44: Wildcard Matching

Given a string s and wildcard pattern p, return whether p matches the entire
string.

Pattern rules:
  '?' matches any single character.
  '*' matches any sequence of characters, including the empty sequence.

Important difference from LeetCode 10:
  In regex matching, '*' modifies the previous character, e.g. a*.
  In wildcard matching, '*' is standalone and can match any length.

Examples:
  s="aa",  p="a"    -> false
  s="aa",  p="*"    -> true
  s="cb",  p="?a"   -> false
  s="adceb", p="*a*b" -> true
  s="acdcb", p="a*c?b" -> false
*/

/*
DP definition:
  dp[i][j] = whether s[0..i) matches p[0..j)

Base:
  dp[0][0] = true
  Empty string can match only a prefix made entirely of '*':
      p="*"    -> true
      p="**"   -> true
      p="*a"   -> false

Transition:
  1. Normal char or '?':
       if p[j-1] == s[i-1] or p[j-1] == '?':
           dp[i][j] = dp[i-1][j-1]

  2. '*':
       dp[i][j] = dp[i][j-1]   // '*' matches empty
               || dp[i-1][j]   // '*' consumes one char from s and can consume more

Complexity:
  Time:  O(m * n)
  Space: O(m * n)
*/
public class WildcardMatching {

    public boolean isMatch(String s, String p) {
        if (s == null || p == null) return false;

        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 1]; // match empty string
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j]; // match empty string || match one more character
                } else if (pc == '?' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        WildcardMatching solver = new WildcardMatching();

        check(solver, "aa", "a", false);
        check(solver, "aa", "*", true);
        check(solver, "cb", "?a", false);
        check(solver, "adceb", "*a*b", true);
        check(solver, "acdcb", "a*c?b", false);
        check(solver, "", "*", true);
        check(solver, "", "**", true);
        check(solver, "", "*a", false);
        check(solver, "abc", "a?c", true);
        check(solver, "abc", "a*?", true);

        System.out.println("All tests passed.");
    }

    private static void check(WildcardMatching solver, String s, String p, boolean expected) {
        boolean actual = solver.isMatch(s, p);
        if (actual != expected) {
            throw new AssertionError("s=" + s + " p=" + p + " expected " + expected + " but got " + actual);
        }
        System.out.println("s=" + s + " p=" + p + " -> " + actual);
    }
}
