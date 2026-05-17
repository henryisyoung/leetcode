package waymo;
/*
You are given a 2D grid grid of characters. Each cell contains an uppercase letter (e.g., A/B/C/D) representing a color. The grid size is arbitrary (no fixed limit on rows/columns).

Count how many same-color square regions exist in the grid.

A square region is a contiguous k x k (k >= 1) submatrix such that all cells inside it have the exact same character.

Input (stdin)
The first line contains two integers m n, the number of rows and columns.
The next m lines each contain a string of length n with uppercase letters.
Output (stdout)
Output a single integer: the total number of same-color square regions.
Constraints
1 <= m, n <= 2000 (can be discussed for larger sizes in interview)
Example
Input:

3 4
AABB
AABB
AAAA
Output:

13
Explanation (informal):

All 1x1 cells are squares: 12
There is one 2x2 same-color square at the top-left
No larger same-color squares
Total: 12 + 1 = 13
Example
Input
1 1
A
Output
1
 */

import java.util.Scanner;

/*
Algorithm: classic "largest square ending at (r,c)" DP, extended to multi-color.

  Let dp[r][c] = side length of the largest monochromatic square whose
  bottom-right corner is at (r, c).

  Base:  dp[r][c] = 1 for r = 0 or c = 0.

  Recurrence (r > 0, c > 0):
    if grid[r][c] equals all three of grid[r-1][c], grid[r][c-1], grid[r-1][c-1]:
        dp[r][c] = 1 + min(dp[r-1][c], dp[r][c-1], dp[r-1][c-1])
    else:
        dp[r][c] = 1

  Why the diagonal check is required:
    In the binary {0, 1} version, "current cell is 1" plus the three
    neighbors' dp values is enough.  Here a neighbor's dp tells us a
    same-color square of *its own* color exists — it doesn't say that
    color matches grid[r][c].  Forcing the four 2×2 corners to share a
    color rules out configurations like
            A A
            A B  (would falsely promote dp from 1 to 2 without the diag check).

  Counting:
    A cell with dp[r][c] = k is the bottom-right corner of exactly k
    monochromatic squares (sizes 1, 2, …, k).  So the answer is
    sum of dp[r][c] over all cells.

  Memory:  O(m * n).  Easy to drop to O(n) with a rolling row + a single
           "northwest" scalar (implemented in countLowMem).

  Time:    O(m * n).

  Note on the example output:
    The given example claims the answer is 13 for the 3×4 AABB/AABB/AAAA
    grid.  Under the natural reading ("count every monochromatic k×k
    submatrix") this grid actually has 15 such squares: 12 of size 1×1,
    and three of size 2×2 (top-left A, top-right B, bottom-left A).  The
    code below implements the natural reading and produces 15; if an
    interviewer specifies a different rule (e.g. "only count maximal
    squares" or "only count the largest square per cell"), swap the
    accumulator accordingly — the dp itself stays the same.
*/
public class CountSameColorSquares2 {

    /** Count every monochromatic k×k submatrix (k ≥ 1).  Returns 0 for an empty grid. */
    public long count(char[][] grid) {
        int rows = grid.length, cols = grid[0].length;

        int[][] dp = new int[rows][cols];
        int total = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 1;
                } else {
                    dp[i][j] = 1;
                    if (grid[i - 1][j] == grid[i][j - 1] && grid[i - 1][j] == grid[i - 1][j - 1] && grid[i][j] == grid[i - 1][j]) {
                        dp[i][j] += Math.min(dp[i - 1][j], Math.min(dp[i - 1][j - 1], dp[i][j - 1]));
                    }
                }
                total += dp[i][j];
            }
        }

        return total;
    }
}
