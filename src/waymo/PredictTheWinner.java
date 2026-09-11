package waymo;

/*
LeetCode 486: Predict the Winner

Given an integer array nums, two players take turns picking either the leftmost
or rightmost number. Each player plays optimally. Return true if Player 1 can
win or tie.

Examples:
  nums = [1,5,2]     -> false
  nums = [1,5,233,7] -> true
*/

/*
Interval DP idea:
  Compute the maximum total score the current player can collect from each
  interval, assuming both players play optimally.

  dp[l][r] = maximum score current player can collect from nums[l..r].

Transition:
  If current player picks nums[l], the opponent then plays optimally on
  [l+1, r] and can collect dp[l+1][r]. The remaining score in that interval
  goes to the current player:
      nums[l] + (sum(l+1, r) - dp[l+1][r])

  If current player picks nums[r]:
      nums[r] + (sum(l, r-1) - dp[l][r-1])

  Take the better move.

Base:
  dp[i][i] = nums[i]

Answer:
  player1 = dp[0][n-1]
  player2 = total - player1
  Player 1 can win or tie iff player1 >= player2.

Complexity:
  Time:  O(n^2)
  Space: O(n^2)
*/
public class PredictTheWinner {

    public boolean predictTheWinner(int[] nums) {
        if (nums == null || nums.length == 0) return true;

        int n = nums.length;
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        int[][] dp = new int[n][n];

        for (int i = 0; i < n; i++) {
            dp[i][i] = nums[i];
        }

        for (int len = 2; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;
                int pickLeft = nums[l] + sum(prefix, l + 1, r) - dp[l + 1][r];
                int pickRight = nums[r] + sum(prefix, l, r - 1) - dp[l][r - 1];
                dp[l][r] = Math.max(pickLeft, pickRight);
            }
        }

        int player1 = dp[0][n - 1];
        int player2 = prefix[n] - player1;
        return player1 >= player2;
    }

    private int sum(int[] prefix, int l, int r) {
        if (l > r) return 0;
        return prefix[r + 1] - prefix[l];
    }

    public static void main(String[] args) {
        PredictTheWinner solver = new PredictTheWinner();

        check(solver, new int[]{1, 5, 2}, false);
        check(solver, new int[]{1, 5, 233, 7}, true);
        check(solver, new int[]{1}, true);
        check(solver, new int[]{1, 1}, true);
        check(solver, new int[]{0}, true);
        check(solver, new int[]{2, 4, 55, 6, 8}, false);
        check(solver, new int[]{20, 30, 2, 2, 2, 10}, true);

        System.out.println("All tests passed.");
    }

    private static void check(PredictTheWinner solver, int[] nums, boolean expected) {
        boolean actual = solver.predictTheWinner(nums);
        if (actual != expected) {
            throw new AssertionError(java.util.Arrays.toString(nums)
                    + " expected " + expected + " but got " + actual);
        }
        System.out.println(java.util.Arrays.toString(nums) + " -> " + actual);
    }
}
