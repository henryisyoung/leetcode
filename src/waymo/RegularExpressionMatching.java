package waymo;

/*
LeetCode 10: Regular Expression Matching

Given a string s and a pattern p, return whether p matches the entire string.

Pattern rules:
  '.' matches any single character.
  '*' matches zero or more of the previous element.

Important:
  The match must cover the whole string, not just a substring.

Examples:
  s="aa",   p="a"     -> false
  s="aa",   p="a*"    -> true
  s="ab",   p=".*"    -> true
  s="aab",  p="c*a*b" -> true
  s="mississippi", p="mis*is*p*." -> false
*/

/*
DP definition:
  dp[i][j] = whether s[0..i) matches p[0..j)

Base:
  dp[0][0] = true
  Empty string can match patterns like a*, a*b*, c*a*b*.
  So when p[j-1] == '*':
      dp[0][j] = dp[0][j-2]

Transition:
  1. If p[j-1] is a normal char or '.':
       dp[i][j] = dp[i-1][j-1] if current chars match.

  2. If p[j-1] == '*', it refers to p[j-2]:
       zero copies:
           dp[i][j] |= dp[i][j-2]
       one or more copies, if p[j-2] matches s[i-1]:
           dp[i][j] |= dp[i-1][j]

Why dp[i-1][j] for one-or-more:
  We consume one char from s, but keep the same pattern because '*' can match
  more of the previous element.

Complexity:
  Time:  O(m * n)
  Space: O(m * n)
*/
public class RegularExpressionMatching {

    public boolean isMatch(String s, String p) {
        if (s == null || p == null) return false;

        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    // Invalid patterns like "*" are not expected by LC, but this keeps indexing safe.
                    if (j < 2) continue;

                    dp[i][j] = dp[i][j - 2]; // zero copies of p[j-2]
                    char prev = p.charAt(j - 2);
                    if (matches(s.charAt(i - 1), prev)) {
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                } else if (matches(s.charAt(i - 1), pc)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }

        return dp[m][n];
    }

    private boolean matches(char sc, char pc) {
        return pc == '.' || sc == pc;
    }

    public static void main(String[] args) {
        RegularExpressionMatching solver = new RegularExpressionMatching();

        check(solver, "aa", "a", false);
        check(solver, "aa", "a*", true);
        check(solver, "ab", ".*", true);
        check(solver, "aab", "c*a*b", true);
        check(solver, "mississippi", "mis*is*p*.", false);
        check(solver, "", "a*", true);
        check(solver, "", "c*a*b*", true);
        check(solver, "ab", ".*c", false);
        check(solver, "aaa", "a*a", true);
        check(solver, "aaa", "ab*a*c*a", true);

        System.out.println("All tests passed.");
    }

    private static void check(RegularExpressionMatching solver, String s, String p, boolean expected) {
        boolean actual = solver.isMatch(s, p);
        if (actual != expected) {
            throw new AssertionError("s=" + s + " p=" + p + " expected " + expected + " but got " + actual);
        }
        System.out.println("s=" + s + " p=" + p + " -> " + actual);
    }
}
