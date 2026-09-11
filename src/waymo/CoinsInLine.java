package waymo;

/*
Coins in a Line

There are n coins in a line. Two players take turns taking one or two coins
from the RIGHT side until no coins remain. The player who takes the last coin
wins.

Return true if the first player wins; otherwise return false.

Since coins have no values here, taking from the right vs left does not change
the state: only the number of coins remaining matters.

Examples:
  n = 1 -> true   first takes 1, wins
  n = 2 -> true   first takes 2, wins
  n = 3 -> false  first takes 1 or 2, second takes the rest
  n = 4 -> true
*/

/*
DP definition:
  dp[i] = whether the current player can force a win with i coins remaining.

Transition:
  Current player can take 1 or 2 coins.
  If any move leaves the opponent in a losing state, current state is winning:

      dp[i] = !dp[i - 1] || !dp[i - 2]

Base:
  dp[0] = false  // no coin to take, current player loses
  dp[1] = true
  dp[2] = true

Pattern:
  Losing states are multiples of 3.

Complexity:
  Time:  O(n)
  Space: O(n)
*/
public class CoinsInLine {

    public boolean firstWillWin(int n) {
        if (n <= 0) return false;
        if (n <= 2) return true;

        boolean[] dp = new boolean[n + 1];
        dp[0] = false;
        dp[1] = true;
        dp[2] = true;

        for (int i = 3; i <= n; i++) {
            dp[i] = !dp[i - 1] || !dp[i - 2];
        }
        return dp[n];
    }

    /** Recursive version with memoization. */
    public boolean firstWillWinRecursive(int n) {
        if (n <= 0) return false;
        Boolean[] memo = new Boolean[n + 1];
        return canWin(n, memo);
    }

    private boolean canWin(int coinsLeft, Boolean[] memo) {
        if (coinsLeft <= 0) return false;
        if (coinsLeft <= 2) return true;
        if (memo[coinsLeft] != null) return memo[coinsLeft];

        boolean takeOne = !canWin(coinsLeft - 1, memo);
        boolean takeTwo = !canWin(coinsLeft - 2, memo);
        memo[coinsLeft] = takeOne || takeTwo;
        return memo[coinsLeft];
    }

    /** O(1) version from the pattern: multiples of 3 are losing. */
    public boolean firstWillWinMath(int n) {
        return n > 0 && n % 3 != 0;
    }

    public static void main(String[] args) {
        CoinsInLine solver = new CoinsInLine();

        check(solver, 0, false);
        check(solver, 1, true);
        check(solver, 2, true);
        check(solver, 3, false);
        check(solver, 4, true);
        check(solver, 5, true);
        check(solver, 6, false);
        check(solver, 7, true);
        check(solver, 8, true);
        check(solver, 9, false);

        System.out.println("All tests passed.");
    }

    private static void check(CoinsInLine solver, int n, boolean expected) {
        boolean actual = solver.firstWillWin(n);
        boolean recursive = solver.firstWillWinRecursive(n);
        boolean math = solver.firstWillWinMath(n);
        if (actual != expected || recursive != expected || math != expected) {
            throw new AssertionError("n=" + n + " expected " + expected
                    + " but got dp=" + actual + " recursive=" + recursive + " math=" + math);
        }
        System.out.println("n=" + n + " -> " + actual);
    }
}
