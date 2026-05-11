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
public class CountSameColorSquares {

    /** Count every monochromatic k×k submatrix (k ≥ 1).  Returns 0 for an empty grid. */
    public long count(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        long total = 0;
        for (int r = 0; r < m; r++) {
            if (grid[r].length != n) {
                throw new IllegalArgumentException("ragged grid at row " + r);
            }
            for (int c = 0; c < n; c++) {
                if (r == 0 || c == 0) {
                    dp[r][c] = 1;
                } else if (grid[r][c] == grid[r - 1][c]
                        && grid[r][c] == grid[r][c - 1]
                        && grid[r][c] == grid[r - 1][c - 1]) {
                    dp[r][c] = 1 + Math.min(dp[r - 1][c],
                            Math.min(dp[r][c - 1], dp[r - 1][c - 1]));
                } else {
                    dp[r][c] = 1;
                }
                total += dp[r][c];
            }
        }
        return total;
    }

    /**
     * Same answer as {@link #count(char[][])} using O(n) extra memory.
     * Useful when m, n are both large (e.g. 2000 × 2000 = 4M ints = 16 MB
     * which is fine, but at 50K × 50K the full dp array no longer fits).
     */
    public long countLowMem(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;
        int m = grid.length, n = grid[0].length;
        int[] prev = new int[n];
        int[] curr = new int[n];
        long total = 0;
        for (int r = 0; r < m; r++) {
            if (grid[r].length != n) {
                throw new IllegalArgumentException("ragged grid at row " + r);
            }
            for (int c = 0; c < n; c++) {
                int v;
                if (r == 0 || c == 0) {
                    v = 1;
                } else if (grid[r][c] == grid[r - 1][c]
                        && grid[r][c] == grid[r][c - 1]
                        && grid[r][c] == grid[r - 1][c - 1]) {
                    v = 1 + Math.min(prev[c], Math.min(curr[c - 1], prev[c - 1]));
                } else {
                    v = 1;
                }
                curr[c] = v;
                total += v;
            }
            int[] swap = prev;
            prev = curr;
            curr = swap;
        }
        return total;
    }

    /* ----------------------------- Brute-force reference (for tests) ----------------------------- */

    /** O((m*n)^2) brute force, used to cross-check the DP on small inputs. */
    long countBrute(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        long total = 0;
        int maxK = Math.min(m, n);
        for (int k = 1; k <= maxK; k++) {
            for (int r = 0; r + k <= m; r++) {
                outer:
                for (int c = 0; c + k <= n; c++) {
                    char want = grid[r][c];
                    for (int dr = 0; dr < k; dr++) {
                        for (int dc = 0; dc < k; dc++) {
                            if (grid[r + dr][c + dc] != want) continue outer;
                        }
                    }
                    total++;
                }
            }
        }
        return total;
    }

    /* ----------------------------- IO + demo ----------------------------- */

    public static void main(String[] args) {
        // If stdin has data, run as the spec describes.  Otherwise run demos + tests.
        if (args.length == 0 && System.console() == null && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try {
            return System.in.available() > 0;
        } catch (java.io.IOException e) {
            return false;
        }
    }

    private static void runFromStdin() {
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        int n = sc.nextInt();
        char[][] grid = new char[m][n];
        for (int r = 0; r < m; r++) {
            String row = sc.next();
            for (int c = 0; c < n; c++) grid[r][c] = row.charAt(c);
        }
        System.out.println(new CountSameColorSquares().count(grid));
    }

    private static void runDemos() {
        CountSameColorSquares solver = new CountSameColorSquares();

        // Spec example.  Note: the spec claims the answer is 13, but the
        // natural reading of "count every monochromatic k×k submatrix"
        // yields 15 (12 size-1 + 3 size-2).  We print both expectations.
        char[][] ex = grid(
                "AABB",
                "AABB",
                "AAAA");
        long expDp = solver.count(ex);
        long expBrute = solver.countBrute(ex);
        System.out.println("Spec example  →  dp=" + expDp + ", brute=" + expBrute
                + "  (spec says 13; natural reading gives 15)");

        // Trivial 1×1.
        check(solver, grid("A"), 1);

        // 2×2 same.
        check(solver, grid("AA", "AA"), 5);   // four 1×1 + one 2×2

        // 2×2 different.
        check(solver, grid("AB", "CD"), 4);

        // Diagonal-mismatch trap (the case the diag check catches).
        //   A A
        //   A B   →  4 cells, no 2×2.
        check(solver, grid("AA", "AB"), 4);

        // 3×3 fully uniform: 9 + 4 + 1 = 14.
        check(solver, grid("AAA", "AAA", "AAA"), 14);

        // 3×3 with one off-color in the middle: only 1×1's count.
        check(solver, grid("AAA", "ABA", "AAA"), 9);

        // Cross-check DP vs brute on 100 random small grids.
        java.util.Random rnd = new java.util.Random(7);
        int mismatches = 0;
        for (int t = 0; t < 100; t++) {
            int m = 1 + rnd.nextInt(6);
            int n = 1 + rnd.nextInt(6);
            char[][] g = new char[m][n];
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < n; c++) g[r][c] = (char) ('A' + rnd.nextInt(3));
            }
            long a = solver.count(g);
            long b = solver.countBrute(g);
            long c2 = solver.countLowMem(g);
            if (a != b || a != c2) {
                mismatches++;
                System.out.println("MISMATCH on " + m + "x" + n + ": dp=" + a
                        + " brute=" + b + " lowMem=" + c2);
                printGrid(g);
            }
        }
        System.out.println("Random cross-check: " + (100 - mismatches) + "/100 ok");

        // Big-grid sanity: 1000 × 1000 all same color → expected = sum_{k=1..1000} (1001-k)^2.
        int big = 1000;
        char[][] bigGrid = new char[big][big];
        for (int r = 0; r < big; r++) java.util.Arrays.fill(bigGrid[r], 'X');
        long expected = 0;
        for (int k = 1; k <= big; k++) {
            long side = big - k + 1;
            expected += side * side;
        }
        long got = solver.countLowMem(bigGrid);
        System.out.println("1000x1000 uniform: got=" + got + ", expected=" + expected
                + (got == expected ? "  ✓" : "  ✗"));
    }

    private static void check(CountSameColorSquares solver, char[][] grid, long expected) {
        long got = solver.count(grid);
        long gotLowMem = solver.countLowMem(grid);
        long gotBrute = solver.countBrute(grid);
        boolean ok = got == expected && gotLowMem == expected && gotBrute == expected;
        System.out.print((ok ? "OK  " : "FAIL ") + dims(grid) + " expected=" + expected
                + " dp=" + got + " lowMem=" + gotLowMem + " brute=" + gotBrute);
        if (!ok) {
            System.out.println();
            printGrid(grid);
        } else {
            System.out.println();
        }
    }

    private static char[][] grid(String... rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) g[i] = rows[i].toCharArray();
        return g;
    }

    private static String dims(char[][] g) {
        return g.length + "x" + (g.length == 0 ? 0 : g[0].length);
    }

    private static void printGrid(char[][] g) {
        for (char[] row : g) System.out.println(new String(row));
    }
}
