package waymo;
/*
You are implementing a pen-plotter robot that moves a pen on paper to draw a figure. You are given n strokes, each stroke being a straight line segment with two endpoints. The robot must draw every stroke.

The pen has two movement modes:

Pen-down (drawing): movement along a stroke leaves ink and contributes distance.
Pen-up (travel): movement without drawing still costs distance.
You may choose:

The order in which strokes are drawn.
The direction of each stroke (you may draw a stroke from either endpoint).
Your goal is to minimize the total traveled distance (pen-down drawing distance + pen-up travel distance).

Input
Integer n (number of strokes)
Next n lines: x1 y1 x2 y2 describing the endpoints of each stroke
The robot starts at (0,0) with pen up.
Output
Output the minimum possible total travel distance.

Constraints
1 <= n <= 15
Coordinates are integers, assume range [-1e4, 1e4]
Use Euclidean distance.
Example
Input:

2
0 0 1 0
2 0 3 0
Output:

3.0
Example
Input
1
0 0 3 4
Output
5.0000000000
 */

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/*
This is a small Traveling-Salesman variant ("TSP on edges").  The pen-down
component (sum of stroke lengths) is fixed regardless of order or direction,
so the only optimisation is over pen-up travel.  For convenience the DP
tracks total distance directly — it makes the recurrence and IO output
trivially correct without splitting into two terms.

State:  dp[mask][i][end] where
  - mask  = bitmask of strokes already drawn   (n bits, n ≤ 15)
  - i     = index of the most recently drawn stroke
  - end   = 0 or 1, identifies which endpoint of stroke i the pen
            currently sits on  (i.e. the *finishing* endpoint of stroke i)

Transitions:
  From (mask, i, end) → for every j not in mask and every starting endpoint
  s ∈ {0, 1} of stroke j:
        new_cost = dp[mask][i][end]
                 + dist( endpoints[i][end], endpoints[j][s] )       // pen-up
                 + length(j)                                         // pen-down
        relax dp[mask | (1<<j)][j][1-s]

Base:
  Pen starts at (0, 0).  For every first stroke i and every starting
  endpoint s of i:
        dp[1<<i][i][1-s] = dist((0,0), endpoints[i][s]) + length(i)

Answer:
  min over i, end of dp[(1<<n) - 1][i][end].

  (No return to origin required by the spec.)

Complexity:
  States: 2^n * n * 2.        Transitions: O(n) per state.
  Total : O(2^n * n^2).        For n = 15 that's ~7.4M ops.
  Memory: O(2^n * n).
*/
public class MinimumTravelPath {

    /** Returns the minimum total pen distance (pen-up + pen-down) to draw every stroke, starting from (0, 0). */
    public double minTravel(int[][] strokes) {
        int n = strokes.length;
        if (n == 0) return 0.0;

        double[][][] pts = new double[n][2][2];
        double[] len = new double[n];
        for (int i = 0; i < n; i++) {
            pts[i][0][0] = strokes[i][0];
            pts[i][0][1] = strokes[i][1];
            pts[i][1][0] = strokes[i][2];
            pts[i][1][1] = strokes[i][3];
            len[i] = dist(pts[i][0], pts[i][1]);
        }

        int full = 1 << n;
        double[][][] dp = new double[full][n][2];
        for (double[][] a : dp) for (double[] b : a) Arrays.fill(b, Double.POSITIVE_INFINITY);

        double[] origin = {0.0, 0.0};
        for (int i = 0; i < n; i++) {
            for (int s = 0; s < 2; s++) {
                int e = 1 - s;
                dp[1 << i][i][e] = dist(origin, pts[i][s]) + len[i];
            }
        }

        for (int mask = 1; mask < full; mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0) continue;
                for (int e = 0; e < 2; e++) {
                    double cur = dp[mask][i][e];
                    if (cur == Double.POSITIVE_INFINITY) continue;
                    for (int j = 0; j < n; j++) {
                        if ((mask & (1 << j)) != 0) continue;
                        int newMask = mask | (1 << j);
                        for (int sj = 0; sj < 2; sj++) {
                            int ej = 1 - sj;
                            double cost = cur + dist(pts[i][e], pts[j][sj]) + len[j];
                            if (cost < dp[newMask][j][ej]) {
                                dp[newMask][j][ej] = cost;
                            }
                        }
                    }
                }
            }
        }

        int finalMask = full - 1;
        double best = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            for (int e = 0; e < 2; e++) {
                if (dp[finalMask][i][e] < best) best = dp[finalMask][i][e];
            }
        }
        return best;
    }

    private static double dist(double[] a, double[] b) {
        double dx = a[0] - b[0], dy = a[1] - b[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    /* --------------------------- Brute-force reference --------------------------- */

    /** O(n! * 2^n) brute force: try every order × every direction.  Used to cross-check the DP for n ≤ 8. */
    double minTravelBrute(int[][] strokes) {
        int n = strokes.length;
        if (n == 0) return 0.0;
        double[][][] pts = new double[n][2][2];
        double[] len = new double[n];
        for (int i = 0; i < n; i++) {
            pts[i][0][0] = strokes[i][0]; pts[i][0][1] = strokes[i][1];
            pts[i][1][0] = strokes[i][2]; pts[i][1][1] = strokes[i][3];
            len[i] = dist(pts[i][0], pts[i][1]);
        }
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        double[] best = {Double.POSITIVE_INFINITY};
        permute(perm, 0, pts, len, best);
        return best[0];
    }

    private void permute(int[] perm, int start, double[][][] pts, double[] len, double[] best) {
        int n = perm.length;
        if (start == n) {
            // Try all 2^n direction assignments for this order.
            for (int dirMask = 0; dirMask < (1 << n); dirMask++) {
                double cur = 0.0;
                double[] pen = {0.0, 0.0};
                for (int k = 0; k < n; k++) {
                    int idx = perm[k];
                    int s = (dirMask >> k) & 1;     // starting endpoint
                    int e = 1 - s;
                    cur += dist(pen, pts[idx][s]);  // pen-up
                    cur += len[idx];                // pen-down
                    pen = pts[idx][e];
                    if (cur >= best[0]) break;       // pruning
                }
                if (cur < best[0]) best[0] = cur;
            }
            return;
        }
        for (int i = start; i < n; i++) {
            int tmp = perm[start]; perm[start] = perm[i]; perm[i] = tmp;
            permute(perm, start + 1, pts, len, best);
            tmp = perm[start]; perm[start] = perm[i]; perm[i] = tmp;
        }
    }

    /* --------------------------- IO + demo --------------------------- */

    public static void main(String[] args) {
        if (args.length == 0 && hasStdin()) {
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
        int n = sc.nextInt();
        int[][] strokes = new int[n][4];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < 4; k++) strokes[i][k] = sc.nextInt();
        }
        System.out.printf("%.10f%n", new MinimumTravelPath().minTravel(strokes));
    }

    private static void runDemos() {
        MinimumTravelPath solver = new MinimumTravelPath();

        // Spec example 1.
        check(solver, new int[][]{
                {0, 0, 1, 0},
                {2, 0, 3, 0}
        }, 3.0);

        // Spec example 2.
        check(solver, new int[][]{
                {0, 0, 3, 4}
        }, 5.0);

        // Single stroke that doesn't touch origin.
        check(solver, new int[][]{
                {1, 1, 4, 5}
        }, Math.sqrt(2) + 5.0);

        // Two strokes meeting at a vertex — best is to draw them back-to-back with no pen-up between.
        check(solver, new int[][]{
                {0, 0, 1, 0},   // length 1
                {1, 0, 1, 1}    // length 1, shares endpoint (1, 0)
        }, 2.0);

        // Triangle: 3 sides, can be drawn with no pen-up between (Eulerian).
        check(solver, new int[][]{
                {0, 0, 3, 0},   // length 3
                {3, 0, 0, 4},   // length 5
                {0, 4, 0, 0}    // length 4
        }, 12.0);

        // Two parallel disjoint strokes.  Best plan:
        //   pen down at (0,0), draw to (5,0)            [5]
        //   pen up to (4,1)                              [sqrt 2]
        //   pen down to (1,1)                            [3]
        // Total = 5 + sqrt(2) + 3 ≈ 9.41421356.
        check(solver, new int[][]{
                {0, 0, 5, 0},
                {1, 1, 4, 1}
        }, 5.0 + Math.sqrt(2) + 3.0);

        // Cross-check vs brute on 30 random small inputs.
        Random rnd = new Random(13);
        int mismatches = 0;
        for (int t = 0; t < 30; t++) {
            int n = 1 + rnd.nextInt(7); // n ≤ 7 to keep brute fast
            int[][] strokes = new int[n][4];
            for (int i = 0; i < n; i++) {
                for (int k = 0; k < 4; k++) strokes[i][k] = rnd.nextInt(21) - 10;
            }
            double a = solver.minTravel(strokes);
            double b = solver.minTravelBrute(strokes);
            if (Math.abs(a - b) > 1e-9) {
                mismatches++;
                System.out.println("MISMATCH dp=" + a + " brute=" + b);
                System.out.println("strokes=" + Arrays.deepToString(strokes));
            }
        }
        System.out.println("Random cross-check: " + (30 - mismatches) + "/30 ok");

        // Performance: n=15 random strokes.  ~7M ops; should run in well under a second.
        int n = 15;
        int[][] big = new int[n][4];
        Random bigRnd = new Random(2);
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < 4; k++) big[i][k] = bigRnd.nextInt(2001) - 1000;
        }
        long t0 = System.nanoTime();
        double ans = solver.minTravel(big);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("Stress n=15: ans=%.4f in %d ms%n", ans, ms);
    }

    private static void check(MinimumTravelPath solver, int[][] strokes, double expected) {
        double got = solver.minTravel(strokes);
        double brute = strokes.length <= 7 ? solver.minTravelBrute(strokes) : Double.NaN;
        boolean ok = Math.abs(got - expected) < 1e-6;
        if (!Double.isNaN(brute) && Math.abs(got - brute) > 1e-9) ok = false;
        System.out.printf("%s n=%d expected=%.6f dp=%.6f brute=%s%n",
                ok ? "OK  " : "FAIL", strokes.length, expected, got,
                Double.isNaN(brute) ? "—" : String.format("%.6f", brute));
    }
}
