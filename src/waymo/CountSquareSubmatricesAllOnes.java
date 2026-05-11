package waymo;
/*
LeetCode 1277. Count Square Submatrices with All Ones.

Given a 0/1 matrix, return how many square submatrices have ALL cells equal to 1.

Input
  An m x n matrix `matrix` whose entries are 0 or 1.

Output
  The total number of square submatrices (any side k >= 1) consisting entirely of 1's.

Examples
  matrix = [[0,1,1,1],
            [1,1,1,1],
            [0,1,1,1]]
  Output: 15
    Explanation: 10 squares of side 1 + 4 squares of side 2 + 1 square of side 3 = 15.

  matrix = [[1,0,1],
            [1,1,0],
            [1,1,0]]
  Output: 7
    Explanation: 6 squares of side 1 + 1 square of side 2 = 7.

Constraints
  1 <= m, n <= 300 (LeetCode's bound; the DP scales to several thousand).
  matrix[i][j] is 0 or 1.
 */

import java.util.Arrays;
import java.util.Random;

/*
Algorithm: classic "largest all-ones square ending at (r, c)" DP.

  Let dp[r][c] = side length of the largest all-ones square whose
  bottom-right corner is at (r, c).

  Base:  dp[r][c] = matrix[r][c]   for r == 0 or c == 0
                                   (boundary: only a 1x1 fits there)

  Recurrence (r > 0, c > 0, matrix[r][c] == 1):
      dp[r][c] = 1 + min(dp[r-1][c], dp[r][c-1], dp[r-1][c-1])

  If matrix[r][c] == 0, dp[r][c] = 0 (no square ends at a 0 cell).

  Counting:
    A cell with dp[r][c] = k is the bottom-right corner of exactly k
    all-ones squares (sizes 1, 2, ..., k).  So the answer is
    sum of dp[r][c] over all cells.

  Why min works (no diagonal-color check needed):
    There is only ONE valid color (1).  Any neighbor with dp >= k-1 is
    automatically reporting a (k-1)x(k-1) block of 1's; we don't have
    to verify the color matches.  This is the simpler binary cousin of
    CountSameColorSquares.java in this same package — that file adds a
    diagonal equality check because each color is independent.

  Memory:  O(m * n).  Easy to drop to O(n) with a rolling row + a
           single "northwest" scalar (implemented in countLowMem).

  Time:    O(m * n).
*/
public class CountSquareSubmatricesAllOnes {

    /* ----------------------------- O(m*n) memory ----------------------------- */

    /** Standard 2D DP.  Returns 0 for an empty matrix. */
    public int countSquares(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return 0;
        int m = matrix.length, n = matrix[0].length;
        int[][] dp = new int[m][n];
        long total = 0;
        for (int r = 0; r < m; r++) {
            if (matrix[r].length != n) {
                throw new IllegalArgumentException("ragged matrix at row " + r);
            }
            for (int c = 0; c < n; c++) {
                int v = matrix[r][c];
                if (v == 1 && r > 0 && c > 0) {
                    v = 1 + Math.min(dp[r - 1][c],
                            Math.min(dp[r][c - 1], dp[r - 1][c - 1]));
                }
                dp[r][c] = v;
                total += v;
            }
        }
        // LeetCode signature returns int; the bound m,n <= 300 keeps it well within int range,
        // but cast defensively in case the method is reused on larger matrices.
        return Math.toIntExact(total);
    }

    /* ----------------------------- O(n) memory ----------------------------- */

    /**
     * Same answer using a single rolling row.  Useful when m, n are large
     * enough that the full dp grid is wasteful.
     *
     * Trick: when overwriting curr[c], we still need the OLD curr[c] (which
     * is dp[r-1][c]) before it gets clobbered.  We stash it in `topLeftNext`
     * so the next iteration can use it as the "diagonal" (dp[r-1][c-1]).
     */
    public int countSquaresLowMem(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return 0;
        int m = matrix.length, n = matrix[0].length;
        int[] curr = new int[n];
        long total = 0;
        for (int r = 0; r < m; r++) {
            if (matrix[r].length != n) {
                throw new IllegalArgumentException("ragged matrix at row " + r);
            }
            int diag = 0; // dp[r-1][c-1] for the cell we are about to compute
            for (int c = 0; c < n; c++) {
                int oldTop = curr[c]; // dp[r-1][c] before we overwrite
                int v = matrix[r][c];
                if (v == 1 && r > 0 && c > 0) {
                    v = 1 + Math.min(oldTop, Math.min(curr[c - 1], diag));
                }
                curr[c] = v;
                total += v;
                diag = oldTop; // for the next column, the diag is the old top
            }
        }
        return Math.toIntExact(total);
    }

    /* ----------------------------- Brute-force reference ----------------------------- */

    /** O((m*n) * min(m,n)^2) brute force, used to cross-check the DP on small inputs. */
    int countSquaresBrute(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        long total = 0;
        int maxK = Math.min(m, n);
        for (int k = 1; k <= maxK; k++) {
            for (int r = 0; r + k <= m; r++) {
                outer:
                for (int c = 0; c + k <= n; c++) {
                    for (int dr = 0; dr < k; dr++) {
                        for (int dc = 0; dc < k; dc++) {
                            if (matrix[r + dr][c + dc] != 1) continue outer;
                        }
                    }
                    total++;
                }
            }
        }
        return Math.toIntExact(total);
    }

    /* ----------------------------- Demo + tests ----------------------------- */

    public static void main(String[] args) {
        CountSquareSubmatricesAllOnes solver = new CountSquareSubmatricesAllOnes();

        check(solver, new int[][]{
                {0, 1, 1, 1},
                {1, 1, 1, 1},
                {0, 1, 1, 1}}, 15);

        check(solver, new int[][]{
                {1, 0, 1},
                {1, 1, 0},
                {1, 1, 0}}, 7);

        check(solver, new int[][]{{1}}, 1);
        check(solver, new int[][]{{0}}, 0);

        // 1x4 and 4x1 strips: only 1x1 squares possible.
        check(solver, new int[][]{{1, 1, 0, 1}}, 3);
        check(solver, new int[][]{{1}, {1}, {0}, {1}}, 3);

        // 3x3 all 1's: 9 + 4 + 1 = 14.
        check(solver, new int[][]{
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}}, 14);

        // Random fuzz vs brute force.
        Random rnd = new Random(7);
        int mismatches = 0;
        for (int t = 0; t < 200; t++) {
            int m = 1 + rnd.nextInt(6);
            int n = 1 + rnd.nextInt(6);
            int[][] g = new int[m][n];
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < n; c++) g[r][c] = rnd.nextInt(2);
            }
            int a = solver.countSquares(g);
            int b = solver.countSquaresBrute(g);
            int c2 = solver.countSquaresLowMem(g);
            if (a != b || a != c2) {
                mismatches++;
                System.out.println("MISMATCH " + m + "x" + n + ": dp=" + a
                        + " brute=" + b + " lowMem=" + c2);
                printGrid(g);
            }
        }
        System.out.println("Random cross-check: " + (200 - mismatches) + "/200 ok");

        // Big-grid sanity: NxN all 1's -> sum_{k=1..N} (N-k+1)^2.
        int big = 1000;
        int[][] bigGrid = new int[big][big];
        for (int r = 0; r < big; r++) Arrays.fill(bigGrid[r], 1);
        long expected = 0;
        for (int k = 1; k <= big; k++) {
            long side = big - k + 1;
            expected += side * side;
        }
        long got = solver.countSquaresLowMem(bigGrid);
        System.out.println("1000x1000 all-ones: got=" + got + ", expected=" + expected
                + (got == expected ? "  OK" : "  FAIL"));
    }

    private static void check(CountSquareSubmatricesAllOnes solver, int[][] grid, int expected) {
        int got = solver.countSquares(grid);
        int gotLowMem = solver.countSquaresLowMem(grid);
        int gotBrute = solver.countSquaresBrute(grid);
        boolean ok = got == expected && gotLowMem == expected && gotBrute == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + dims(grid) + " expected=" + expected
                + " dp=" + got + " lowMem=" + gotLowMem + " brute=" + gotBrute);
        if (!ok) printGrid(grid);
    }

    private static String dims(int[][] g) {
        return g.length + "x" + (g.length == 0 ? 0 : g[0].length);
    }

    private static void printGrid(int[][] g) {
        for (int[] row : g) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < row.length; i++) {
                if (i > 0) sb.append(' ');
                sb.append(row[i]);
            }
            System.out.println(sb);
        }
    }
}
