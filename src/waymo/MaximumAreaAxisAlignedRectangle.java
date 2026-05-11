package waymo;
/*
Given a set of points points on a 2D plane, where each point has integer coordinates (x, y).

Find the maximum area of an axis-aligned rectangle whose four vertices all appear in points. If no such rectangle exists, return 0.

Rules:

All four corners must be present in points.
Area is (x2 - x1) * (y2 - y1) with x1 != x2 and y1 != y2.
Input (stdin)

Line 1: integer n, number of points.
Next n lines: two integers x y.
Output (stdout)

One integer: the maximum rectangle area, or 0 if none exists.
Constraints (practice scale)

1 <= n <= 2 * 10^4
-10^9 <= x, y <= 10^9
Example input:

6
1 1
1 3
3 1
3 3
2 2
3 2
Example output:

4
Example
Input
6
1 1
1 3
3 1
3 3
2 2
3 2
Output
4
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/*
Algorithm: group points by x-coordinate, then for every pair of columns find
the min and max y they share — that's the tallest rectangle spanning the two
columns.

  Why "group by x":
    An axis-aligned rectangle has 2 distinct x-coords and 2 distinct y-coords.
    The 4 corners pair up into 2 columns of 2 points each.  So columns with
    fewer than 2 ys can never contribute and are filtered out.

  For each column pair (x1, x2):
    Walk the smaller column's y-set; for each y, ask "is y also at x2?" via
    the other column's HashSet.  Track min and max common y.  If at least 2
    ys are shared, area = (x2 - x1) * (maxY - minY) and we update the answer.

  Complexity:
    Columns sum to n distinct (x, y) entries, so n_i = column sizes obey
    Σ n_i = n.  Per pair work is O(min(n_i, n_j)).  Standard rearrangement
    bound gives  Σ_{i<j} min(n_i, n_j) ≤ O(n √n)  in the worst case.
    For n = 2·10^4 that's ≈ 3·10^6 ops — well under a second.

  Pruning:
    Pre-compute yRange[i] = maxY[i] - minY[i] per column.  Upper bound on
    any rectangle through columns (i, j) is (x_j - x_i) * min(yRange[i],
    yRange[j]); skip the pair if that's already ≤ best.  Iterating the
    inner loop from rightmost column inward grows the best answer fast,
    making the prune kick in early.

  Overflow:
    x, y can each be 10^9, so (x_j − x_i) and (maxY − minY) are each up to
    2·10^9 — fits in int.  Their product can be up to 4·10^18, so the area
    is computed as long.  long max is 9.2·10^18, safe.
*/
public class MaximumAreaAxisAlignedRectangle {

    /** Returns the maximum axis-aligned rectangle area whose 4 corners are all in {@code points}, or 0 if none exists. */
    public long maxArea(int[][] points) {
        if (points == null || points.length < 4) return 0;

        // Group ys by x; HashSet auto-dedupes duplicate input points.
        Map<Integer, Set<Integer>> ysByX = new HashMap<>();
        for (int[] p : points) {
            ysByX.computeIfAbsent(p[0], k -> new HashSet<>()).add(p[1]);
        }

        // Keep only columns with ≥ 2 ys; sort by x.
        List<int[]> colMeta = new ArrayList<>(); // entries: {x, size, yRange}
        Map<Integer, Set<Integer>> setByX = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> e : ysByX.entrySet()) {
            Set<Integer> ys = e.getValue();
            if (ys.size() < 2) continue;
            int x = e.getKey();
            int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
            for (int y : ys) {
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
            setByX.put(x, ys);
            colMeta.add(new int[]{x, ys.size(), maxY - minY});
        }
        if (colMeta.size() < 2) return 0;
        colMeta.sort((a, b) -> Integer.compare(a[0], b[0]));

        int k = colMeta.size();
        int[] xs = new int[k];
        int[] yRange = new int[k];
        int[][] ysArr = new int[k][];
        for (int i = 0; i < k; i++) {
            int[] meta = colMeta.get(i);
            xs[i] = meta[0];
            yRange[i] = meta[2];
            Set<Integer> s = setByX.get(xs[i]);
            int[] arr = new int[s.size()];
            int idx = 0;
            for (int y : s) arr[idx++] = y;
            ysArr[i] = arr;
        }

        long best = 0;
        for (int i = 0; i < k - 1; i++) {
            // Inner loop runs from rightmost column inward so xDiff is largest
            // first — best grows quickly and the upper-bound prune kicks in.
            for (int j = k - 1; j > i; j--) {
                int xDiff = xs[j] - xs[i];
                long upper = (long) xDiff * Math.min(yRange[i], yRange[j]);
                if (upper <= best) continue;

                int[] smallArr;
                Set<Integer> bigSet;
                if (ysArr[i].length <= ysArr[j].length) {
                    smallArr = ysArr[i];
                    bigSet = setByX.get(xs[j]);
                } else {
                    smallArr = ysArr[j];
                    bigSet = setByX.get(xs[i]);
                }

                int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
                for (int y : smallArr) {
                    if (bigSet.contains(y)) {
                        if (y < minY) minY = y;
                        if (y > maxY) maxY = y;
                    }
                }
                if (maxY > minY) {
                    long area = (long) xDiff * (long) (maxY - minY);
                    if (area > best) best = area;
                }
            }
        }
        return best;
    }

    /* --------------------------- Brute-force reference (tests) --------------------------- */

    /** O(n^4) reference: try every 4-subset.  Used to cross-check on small inputs. */
    long maxAreaBrute(int[][] points) {
        // dedupe first
        Set<Long> set = new HashSet<>();
        List<int[]> uniq = new ArrayList<>();
        for (int[] p : points) {
            long code = encode(p[0], p[1]);
            if (set.add(code)) uniq.add(p);
        }
        long best = 0;
        int n = uniq.size();
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                int[] pa = uniq.get(a), pb = uniq.get(b);
                if (pa[0] == pb[0] || pa[1] == pb[1]) continue; // need diagonal pair
                // The other two corners of any rectangle with pa, pb as opposite
                // corners are (pa.x, pb.y) and (pb.x, pa.y) — independent of any
                // min/max choice (those would land back on pa/pb itself).
                if (set.contains(encode(pa[0], pb[1])) && set.contains(encode(pb[0], pa[1]))) {
                    long area = (long) Math.abs(pa[0] - pb[0]) * (long) Math.abs(pa[1] - pb[1]);
                    if (area > best) best = area;
                }
            }
        }
        return best;
    }

    private static long encode(int x, int y) {
        return (((long) x) << 32) | (y & 0xFFFFFFFFL);
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
        int[][] pts = new int[n][2];
        for (int i = 0; i < n; i++) {
            pts[i][0] = sc.nextInt();
            pts[i][1] = sc.nextInt();
        }
        System.out.println(new MaximumAreaAxisAlignedRectangle().maxArea(pts));
    }

    private static void runDemos() {
        MaximumAreaAxisAlignedRectangle solver = new MaximumAreaAxisAlignedRectangle();

        // Spec example: 6 points; the unit square at (1,1)-(3,3) is the only rectangle, area 4.
        check(solver, new int[][]{{1, 1}, {1, 3}, {3, 1}, {3, 3}, {2, 2}, {3, 2}}, 4);

        // No rectangle: only 3 points.
        check(solver, new int[][]{{0, 0}, {1, 1}, {2, 2}}, 0);

        // No rectangle: 4 points but they don't form one.
        check(solver, new int[][]{{0, 0}, {1, 1}, {2, 0}, {3, 1}}, 0);

        // Single small rectangle.
        check(solver, new int[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}}, 1);

        // Multiple rectangles, take the biggest (4×3 = 12).
        check(solver, new int[][]{
                {0, 0}, {0, 3}, {4, 0}, {4, 3},     // big 4×3
                {1, 1}, {1, 2}, {2, 1}, {2, 2},     // small 1×1
        }, 12);

        // Duplicate points should not cause double-counting or affect answer.
        check(solver, new int[][]{
                {0, 0}, {0, 0}, {0, 1}, {1, 0}, {1, 1}
        }, 1);

        // Negative coords + large coords: check no overflow.
        check(solver, new int[][]{
                {-1_000_000_000, -1_000_000_000},
                {-1_000_000_000, 1_000_000_000},
                {1_000_000_000, -1_000_000_000},
                {1_000_000_000, 1_000_000_000},
        }, 4_000_000_000_000_000_000L);

        // Cross-check against brute force on 50 random small inputs.
        Random rnd = new Random(11);
        int mismatches = 0;
        for (int t = 0; t < 50; t++) {
            int n = 4 + rnd.nextInt(20);
            int range = 1 + rnd.nextInt(6); // 1..6 → coords in [-3..3]
            int[][] pts = new int[n][2];
            for (int i = 0; i < n; i++) {
                pts[i][0] = rnd.nextInt(2 * range + 1) - range;
                pts[i][1] = rnd.nextInt(2 * range + 1) - range;
            }
            long got = solver.maxArea(pts);
            long ref = solver.maxAreaBrute(pts);
            if (got != ref) {
                mismatches++;
                System.out.println("MISMATCH: got=" + got + " ref=" + ref);
                System.out.println("points=" + Arrays.deepToString(pts));
            }
        }
        System.out.println("Random cross-check: " + (50 - mismatches) + "/50 ok");

        // Performance: 20K points uniformly random in a 1000 × 1000 box.  Many
        // collisions ⇒ many rectangles.  Should run in well under a second.
        int N = 20_000;
        Random big = new Random(3);
        int[][] pts = new int[N][2];
        for (int i = 0; i < N; i++) {
            pts[i][0] = big.nextInt(1000);
            pts[i][1] = big.nextInt(1000);
        }
        long t0 = System.nanoTime();
        long ans = solver.maxArea(pts);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress 20K random in [0..1000)^2: ans=" + ans + " in " + ms + " ms");
    }

    private static void check(MaximumAreaAxisAlignedRectangle solver, int[][] pts, long expected) {
        long got = solver.maxArea(pts);
        long ref = solver.maxAreaBrute(pts);
        boolean ok = got == expected && ref == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "n=" + pts.length + " expected=" + expected
                + " maxArea=" + got + " brute=" + ref);
    }
}
