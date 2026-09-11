package waymo;

/*
Coins in a Line II

There are n coins with different values in a line. Two players take turns taking
one or two coins from the LEFT side until no coins remain. The player with the
larger total value wins.

Return true if the first player wins; otherwise return false.

Examples:
  values = [1, 2, 2]      -> true
    Player 1 takes 1 coin: 1
    Player 2 takes 2 coins: 2 + 2 = 4
    bad.
    Better: Player 1 takes 2 coins: 1 + 2 = 3, Player 2 gets 2 -> Player 1 wins.

  values = [1, 2, 4]      -> false
    Player 1 takes 1 -> Player 2 takes 2+4 = 6
    Player 1 takes 1+2 = 3 -> Player 2 takes 4
    Player 1 cannot get more than Player 2.
*/

/*
DP idea:
  Since coins can only be taken from the left, the remaining state is just the
  start index i.

  dp[i] = maximum value the current player can collect from values[i..n).

Let suffix[i] = total value of values[i..n).

Transition:
  If current player takes one coin:
      gain = values[i]
      opponent then can collect dp[i+1] from the remaining suffix.
      current player's final total from this suffix =
          values[i] + (suffix[i+1] - dp[i+1])

  If current player takes two coins:
      values[i] + values[i+1] + (suffix[i+2] - dp[i+2])

  dp[i] = max(takeOne, takeTwo)

Answer:
  first = dp[0]
  second = suffix[0] - first
  First player wins iff first > second.

Complexity:
  Time:  O(n)
  Space: O(n)
*/
public class CoinsInLineII {

    public boolean firstWillWin(int[] values) {
        if (values == null || values.length == 0) return false;

        int n = values.length;
        int[] suffix = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            suffix[i] = suffix[i + 1] + values[i];
        }

        int[] dp = new int[n + 2];
        for (int i = n - 1; i >= 0; i--) {
            int takeOne = values[i] + suffix[i + 1] - dp[i + 1];
            int takeTwo = Integer.MIN_VALUE;
            if (i + 1 < n) {
                takeTwo = values[i] + values[i + 1] + suffix[i + 2] - dp[i + 2];
            }
            dp[i] = Math.max(takeOne, takeTwo);
        }

        int first = dp[0];
        int second = suffix[0] - first;
        return first > second;
    }

    public static void main(String[] args) {
        CoinsInLineII solver = new CoinsInLineII();

        check(solver, new int[]{1, 2, 2}, true);
        check(solver, new int[]{1, 2, 4}, false);
        check(solver, new int[]{5}, true);
        check(solver, new int[]{1, 1}, true);
        check(solver, new int[]{1, 1, 1, 1}, false); // tie is not a win
        check(solver, new int[]{3, 2, 2}, true);
        check(solver, new int[]{1, 20, 4}, true);

        System.out.println("All tests passed.");
    }

    private static void check(CoinsInLineII solver, int[] values, boolean expected) {
        boolean actual = solver.firstWillWin(values);
        if (actual != expected) {
            throw new AssertionError(java.util.Arrays.toString(values)
                    + " expected " + expected + " but got " + actual);
        }
        System.out.println(java.util.Arrays.toString(values) + " -> " + actual);
    }
}
