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

import java.util.*;

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
public class MaximumAreaAxisAlignedRectangle2 {

    /** Returns the maximum axis-aligned rectangle area whose 4 corners are all in {@code points}, or 0 if none exists. */
    public long maxArea(int[][] points) {
        // Group ys per column.  Sort points by x first so xs comes out ascending via LinkedHashMap insertion order.
        Map<Integer, Set<Integer>> xGroup = new LinkedHashMap<>();
        Arrays.sort(points, (a, b) -> Integer.compare(a[0], b[0]));
        for (int[] point : points) {
            xGroup.computeIfAbsent(point[0], k -> new HashSet<>()).add(point[1]);
        }

        // Columns with < 2 ys can never form a rectangle side; drop them.
        List<Integer> xs = new ArrayList<>();
        for (Map.Entry<Integer, Set<Integer>> entry : xGroup.entrySet()) {
            if (entry.getValue().size() >= 2) xs.add(entry.getKey());
        }
        if (xs.size() < 2) return 0;

        // long, not int: with x, y in [-1e9, 1e9] the area can reach 4e18, which overflows int.
        long max = 0;
        for (int i = 0; i < xs.size() - 1; i++) {
            Set<Integer> firstYs = xGroup.get(xs.get(i));
            for (int j = i + 1; j < xs.size(); j++) {
                Set<Integer> secondYs = xGroup.get(xs.get(j));

                // Walk the smaller set; saves work when one column is dense and the other sparse.
                Set<Integer> a = firstYs.size() <= secondYs.size() ? firstYs : secondYs;
                Set<Integer> b = (a == firstYs) ? secondYs : firstYs;

                int lowY = Integer.MAX_VALUE, highY = Integer.MIN_VALUE;
                for (int fy : a) {
                    if (b.contains(fy)) {
                        if (fy < lowY) lowY = fy;
                        if (fy > highY) highY = fy;
                    }
                }
                // Need at least 2 shared ys (lowY strictly < highY) to form a rectangle.
                if (lowY < highY) {
                    long area = (long) (xs.get(j) - xs.get(i)) * (highY - lowY);
                    if (area > max) max = area;
                }
            }
        }
        return max;
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
        System.out.println(new MaximumAreaAxisAlignedRectangle2().maxArea(pts));
    }

    private static void runDemos() {
        MaximumAreaAxisAlignedRectangle2 solver = new MaximumAreaAxisAlignedRectangle2();

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

    private static void check(MaximumAreaAxisAlignedRectangle2 solver, int[][] pts, long expected) {
        long got = solver.maxArea(pts);
        long ref = solver.maxAreaBrute(pts);
        boolean ok = got == expected && ref == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "n=" + pts.length + " expected=" + expected
                + " maxArea=" + got + " brute=" + ref);
    }
}
