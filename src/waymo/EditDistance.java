package waymo;

/*
LeetCode 72: Edit Distance

Given two strings word1 and word2, return the minimum number of operations
required to convert word1 to word2.

Allowed operations:
  - insert a character
  - delete a character
  - replace a character

Examples:
  word1="horse",     word2="ros"       -> 3
  word1="intention", word2="execution" -> 5
*/

/*
DP definition:
  dp[i][j] = minimum edit distance between word1[0..i) and word2[0..j)

Base:
  dp[i][0] = i  // delete all i chars from word1
  dp[0][j] = j  // insert all j chars into word1

Transition:
  If word1[i-1] == word2[j-1]:
      dp[i][j] = dp[i-1][j-1]

  Else:
      dp[i][j] = 1 + min(
          dp[i-1][j],     // delete word1[i-1]
          dp[i][j-1],     // insert word2[j-1]
          dp[i-1][j-1]    // replace word1[i-1] with word2[j-1]
      )

Complexity:
  Time:  O(m * n)
  Space: O(m * n)
*/
public class EditDistance {

    public int minDistance(String word1, String word2) {
        if (word1 == null || word2 == null) return -1;

        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int delete = dp[i - 1][j];
                    int insert = dp[i][j - 1];
                    int replace = dp[i - 1][j - 1];
                    dp[i][j] = 1 + Math.min(delete, Math.min(insert, replace));
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        EditDistance solver = new EditDistance();

        check(solver, "horse", "ros", 3);
        check(solver, "intention", "execution", 5);
        check(solver, "", "", 0);
        check(solver, "", "abc", 3);
        check(solver, "abc", "", 3);
        check(solver, "abc", "abc", 0);
        check(solver, "abc", "yabd", 2);
        check(solver, "kitten", "sitting", 3);

        System.out.println("All tests passed.");
    }

    private static void check(EditDistance solver, String word1, String word2, int expected) {
        int actual = solver.minDistance(word1, word2);
        if (actual != expected) {
            throw new AssertionError("word1=" + word1 + " word2=" + word2
                    + " expected " + expected + " but got " + actual);
        }
        System.out.println("word1=" + word1 + " word2=" + word2 + " -> " + actual);
    }
}
