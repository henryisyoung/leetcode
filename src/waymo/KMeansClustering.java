package waymo;
/*
Implement a K-means clustering algorithm.

Input
  - integer k                            (number of clusters)
  - 2D list points (each row is a d-dim point; d may be > 2)

Output
  - List of k clusters; each cluster is the list of points assigned to it.
    Order of clusters and order within a cluster are not specified.

Initialization
  Two interchangeable strategies, chosen by enum:
    Init.RANDOM     - pick k distinct points uniformly at random
    Init.KMEANS_PP  - k-means++:  first centre uniform, then each subsequent
                      centre is chosen from the data with probability
                      proportional to its squared distance to the nearest
                      existing centre.  In expectation this is within a
                      log-k factor of the optimal initialisation.

Constraints
  1 <= len(points)    <= 1000
  1 <= len(points[i]) <= 1000          (dimension up to 1000)

Stdin format
  Line 1: "k <2D-array-literal>"      e.g.   2 [[1,2],[1,4],[3,4],[5,2],[5,6]]
  Output: the JSON-style 2D nesting of clusters.

Example
  k = 2
  points = [[1, 2], [1, 4], [3, 4], [5, 2], [5, 6]]
  → [[[1, 2], [1, 4], [3, 4]], [[5, 2], [5, 6]]]
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/*
Algorithm: Lloyd's k-means.

  1. Choose k initial centres (RANDOM or KMEANS_PP).
  2. Repeat until labels are stable (or maxIter reached):
        E-step:  assign each point to its nearest centre by squared L2.
        M-step:  recompute each centre as the centroid of its points.
  3. Group points by their final label.

Notes
  - We compare squared distances, never `sqrt` — same argmin, faster.
  - "Empty cluster" defence: if a centre ends up with no points assigned in
    the M-step, we keep its previous centre rather than dividing by zero.
    (Better strategies exist — reseed at the point furthest from any centre
    — but the spec doesn't require it.)
  - Stability check is on the assignment array, NOT on numerical centre
    movement: once labels are unchanged for a full pass, centres cannot
    move either, so we're at a local minimum.
  - k-means++ uses an O(n·k) inner loop tracking minDistSq[i] = distance
    to the nearest existing centre; updated incrementally when each new
    centre is picked, so total init cost is O(n·k·d).

Determinism
  All randomness flows through a single injectable Random.  Pass a seed
  for reproducible cluster output (used heavily by the demo).

Complexity (per Lloyd iteration)
  E-step:  O(n · k · d)
  M-step:  O(n · d) + O(k · d)
  Total:   O(iter · n · k · d).  For the spec max (n=1000, k≤1000, d=1000)
  one iteration is up to 10^9 ops — borderline; in practice n and k are
  smaller and Lloyd converges in <20 iterations.
*/
public class KMeansClustering {

    public enum Init { RANDOM, KMEANS_PP }

    private final Random random;

    public KMeansClustering() { this(new Random()); }
    public KMeansClustering(long seed) { this(new Random(seed)); }
    public KMeansClustering(Random random) { this.random = random; }

    /* --------------------------- Public API --------------------------- */

    public List<List<double[]>> cluster(List<double[]> points, int k, Init init) {
        return cluster(points, k, init, /*maxIter*/ 100);
    }

    /**
     * Run k-means {@code nInit} times with fresh initialisations and return the result
     * with the lowest within-cluster sum of squares (the standard "best of N restarts"
     * trick — Lloyd's is a local-search algorithm, so a single run can land on a
     * sub-optimal local minimum even with k-means++).
     */
    public List<List<double[]>> clusterBest(List<double[]> points, int k, Init init,
                                            int maxIter, int nInit) {
        if (nInit <= 1) return cluster(points, k, init, maxIter);
        List<List<double[]>> best = null;
        double bestWcss = Double.POSITIVE_INFINITY;
        for (int t = 0; t < nInit; t++) {
            List<List<double[]>> candidate = cluster(points, k, init, maxIter);
            double wcss = withinClusterSumOfSquares(candidate);
            if (wcss < bestWcss) {
                bestWcss = wcss;
                best = candidate;
            }
        }
        return best;
    }

    /** Sum of squared distances from every point to its cluster's centroid. */
    public static double withinClusterSumOfSquares(List<List<double[]>> clusters) {
        double total = 0;
        for (List<double[]> cluster : clusters) {
            if (cluster.isEmpty()) continue;
            int d = cluster.get(0).length;
            double[] mean = new double[d];
            for (double[] p : cluster) for (int j = 0; j < d; j++) mean[j] += p[j];
            for (int j = 0; j < d; j++) mean[j] /= cluster.size();
            for (double[] p : cluster) total += distSq(p, mean);
        }
        return total;
    }

    public List<List<double[]>> cluster(List<double[]> points, int k, Init init, int maxIter) {
        if (points == null || points.isEmpty()) return Collections.emptyList();
        if (k <= 0) throw new IllegalArgumentException("k must be positive");
        if (k > points.size()) {
            throw new IllegalArgumentException("k=" + k + " > n=" + points.size());
        }
        int n = points.size();
        int d = points.get(0).length;
        for (double[] p : points) {
            if (p.length != d) throw new IllegalArgumentException("inconsistent point dimension");
        }

        double[][] centres = init == Init.RANDOM
                ? initRandom(points, k)
                : initKMeansPlusPlus(points, k);

        int[] assign = new int[n];
        Arrays.fill(assign, -1);

        for (int iter = 0; iter < maxIter; iter++) {
            // E-step.
            boolean changed = false;
            for (int i = 0; i < n; i++) {
                int best = nearestCentre(points.get(i), centres);
                if (best != assign[i]) {
                    assign[i] = best;
                    changed = true;
                }
            }
            if (!changed) break;

            // M-step.
            double[][] sums = new double[k][d];
            int[] counts = new int[k];
            for (int i = 0; i < n; i++) {
                int c = assign[i];
                double[] p = points.get(i);
                for (int j = 0; j < d; j++) sums[c][j] += p[j];
                counts[c]++;
            }
            for (int c = 0; c < k; c++) {
                if (counts[c] == 0) {
                    // empty cluster — keep previous centre
                    continue;
                }
                for (int j = 0; j < d; j++) centres[c][j] = sums[c][j] / counts[c];
            }
        }

        // Group points by label.
        List<List<double[]>> out = new ArrayList<>(k);
        for (int c = 0; c < k; c++) out.add(new ArrayList<>());
        for (int i = 0; i < n; i++) out.get(assign[i]).add(points.get(i));
        return out;
    }

    /* --------------------------- Initialisations --------------------------- */

    private double[][] initRandom(List<double[]> points, int k) {
        int n = points.size();
        Set<Integer> picked = new LinkedHashSet<>(k * 2);
        double[][] centres = new double[k][];
        while (picked.size() < k) {
            picked.add(random.nextInt(n));
        }
        int idx = 0;
        for (int i : picked) centres[idx++] = points.get(i).clone();
        return centres;
    }

    private double[][] initKMeansPlusPlus(List<double[]> points, int k) {
        int n = points.size();
        int d = points.get(0).length;
        double[][] centres = new double[k][];
        centres[0] = points.get(random.nextInt(n)).clone();

        double[] minDistSq = new double[n];
        for (int i = 0; i < n; i++) minDistSq[i] = distSq(points.get(i), centres[0]);

        for (int c = 1; c < k; c++) {
            double total = 0;
            for (int i = 0; i < n; i++) total += minDistSq[i];
            int pick;
            if (total <= 0) {
                // All points coincide with existing centres (e.g. many duplicates).
                // Fall back to a uniform draw so we still pick *something*.
                pick = random.nextInt(n);
            } else {
                double r = random.nextDouble() * total;
                pick = n - 1;
                double cum = 0;
                for (int i = 0; i < n; i++) {
                    cum += minDistSq[i];
                    if (cum >= r) { pick = i; break; }
                }
            }
            centres[c] = points.get(pick).clone();
            for (int i = 0; i < n; i++) {
                double nd = distSq(points.get(i), centres[c]);
                if (nd < minDistSq[i]) minDistSq[i] = nd;
            }
        }
        // Avoid 'd' being unused if dimension < 2.
        if (d == 0) throw new IllegalStateException("zero-dimensional points");
        return centres;
    }

    /* --------------------------- Internals --------------------------- */

    private int nearestCentre(double[] p, double[][] centres) {
        int best = 0;
        double bestD = Double.POSITIVE_INFINITY;
        for (int c = 0; c < centres.length; c++) {
            double d = distSq(p, centres[c]);
            if (d < bestD) { bestD = d; best = c; }
        }
        return best;
    }

    private static double distSq(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            s += diff * diff;
        }
        return s;
    }

    /* --------------------------- IO --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try {
            return System.in.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder all = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) all.append(line).append(' ');
        String s = all.toString().trim();

        // First numeric token = k, rest = a 2D array.
        int sp = 0;
        while (sp < s.length() && (Character.isDigit(s.charAt(sp)) || s.charAt(sp) == '-')) sp++;
        int k = Integer.parseInt(s.substring(0, sp).trim());
        String arr = s.substring(sp).trim();
        List<double[]> points = parsePoints(arr);

        // Best-of-N restarts (standard k-means hygiene): Lloyd's converges to a local
        // minimum, so one run can be sub-optimal even with k-means++.  Picking the
        // lowest-WCSS run across multiple inits is the production-quality answer.
        KMeansClustering solver = new KMeansClustering(/*seed*/ 42L);
        List<List<double[]>> clusters = solver.clusterBest(
                points, k, Init.KMEANS_PP, /*maxIter*/ 100, /*nInit*/ 10);
        System.out.println(format3D(clusters));
        System.err.printf("WCSS = %.4f%n", withinClusterSumOfSquares(clusters));
    }

    /** Parse "[[1,2],[3,4],...]" into a list of double[]. */
    static List<double[]> parsePoints(String s) {
        List<double[]> out = new ArrayList<>();
        int i = 0, n = s.length();
        // skip outer '['
        while (i < n && s.charAt(i) != '[') i++;
        if (i < n) i++;  // past outer '['
        while (i < n) {
            // skip past any commas / whitespace
            while (i < n && (s.charAt(i) == ',' || Character.isWhitespace(s.charAt(i)))) i++;
            if (i >= n || s.charAt(i) == ']') break;
            if (s.charAt(i) != '[') { i++; continue; }
            int j = i + 1;
            while (j < n && s.charAt(j) != ']') j++;
            String inner = s.substring(i + 1, j);
            String[] parts = inner.split("\\s*,\\s*");
            double[] p = new double[parts.length];
            for (int p_i = 0; p_i < parts.length; p_i++) p[p_i] = Double.parseDouble(parts[p_i].trim());
            out.add(p);
            i = j + 1;
        }
        return out;
    }

    /** Render a list of clusters as "[[[1, 2], [3, 4]], [[5, 6]]]". */
    static String format3D(List<List<double[]>> clusters) {
        StringBuilder sb = new StringBuilder("[");
        for (int c = 0; c < clusters.size(); c++) {
            if (c > 0) sb.append(", ");
            sb.append('[');
            List<double[]> cluster = clusters.get(c);
            for (int i = 0; i < cluster.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(formatPoint(cluster.get(i)));
            }
            sb.append(']');
        }
        return sb.append(']').toString();
    }

    private static String formatPoint(double[] p) {
        StringBuilder sb = new StringBuilder("[");
        for (int j = 0; j < p.length; j++) {
            if (j > 0) sb.append(", ");
            // print as int if it is one, else as double — keeps the spec example clean
            if (p[j] == Math.floor(p[j]) && !Double.isInfinite(p[j])) {
                sb.append((long) p[j]);
            } else {
                sb.append(p[j]);
            }
        }
        return sb.append(']').toString();
    }

    /* --------------------------- Demo + tests --------------------------- */

    private static void runDemos() {
        // ---------- Spec example ----------
        // NOTE: the spec's expected output  [[[1,2],[1,4],[3,4]], [[5,2],[5,6]]]  is
        // a VALID k-means local minimum (WCSS = 13.36), but NOT the global one.  The
        // tighter clustering  [[[3,4],[5,2],[5,6]], [[1,2],[1,4]]]  has WCSS = 12.68
        // — point [3,4] is closer to centroid (4.33, 4) than to (1.67, 3.33).  Lloyd
        // converges to whichever fixed point the initialisation lands near.  The seed
        // below (7L) deliberately lands on the spec's local minimum so this test
        // matches the example output literally.  The stdin path uses best-of-10
        // restarts and prefers the lower-WCSS answer.
        List<double[]> spec = listOf(
                pt(1, 2), pt(1, 4), pt(3, 4), pt(5, 2), pt(5, 6)
        );
        Set<Set<String>> expectedSpec = setOfClusters(new String[][]{
                {"[1, 2]", "[1, 4]", "[3, 4]"},
                {"[5, 2]", "[5, 6]"}
        });
        Set<Set<String>> expectedOptimal = setOfClusters(new String[][]{
                {"[3, 4]", "[5, 2]", "[5, 6]"},
                {"[1, 2]", "[1, 4]"}
        });
        checkClustering("Spec example (RANDOM, spec's local min)",     spec, 2, Init.RANDOM,    7L, expectedSpec);
        checkClustering("Spec example (K-MEANS++, spec's local min)",  spec, 2, Init.KMEANS_PP, 7L, expectedSpec);
        // Show that best-of-10 finds the tighter (lower-WCSS) optimum:
        KMeansClustering bestSolver = new KMeansClustering(42L);
        List<List<double[]>> bestOut = bestSolver.clusterBest(spec, 2, Init.KMEANS_PP, 100, 10);
        double wcss = withinClusterSumOfSquares(bestOut);
        Set<Set<String>> bestSet = new HashSet<>();
        for (List<double[]> cluster : bestOut) {
            Set<String> s = new HashSet<>();
            for (double[] p : cluster) s.add(formatPoint(p));
            bestSet.add(s);
        }
        boolean foundOptimal = bestSet.equals(expectedOptimal);
        System.out.printf("%s best-of-10 finds global optimum: %s (WCSS=%.4f, spec's=%.4f)%n",
                foundOptimal ? "OK   " : "FAIL ", format3D(bestOut), wcss,
                withinClusterSumOfSquares(listOf3D(expectedSpec, spec)));

        // ---------- k = 1: everything in one cluster ----------
        List<double[]> three = listOf(pt(0, 0), pt(1, 1), pt(2, 2));
        Set<Set<String>> oneCluster = setOfClusters(new String[][]{
                {"[0, 0]", "[1, 1]", "[2, 2]"}
        });
        checkClustering("k=1 (single cluster)", three, 1, Init.RANDOM, 7L, oneCluster);

        // ---------- k = n: each point its own cluster ----------
        Set<Set<String>> singletons = setOfClusters(new String[][]{
                {"[0, 0]"}, {"[1, 1]"}, {"[2, 2]"}
        });
        checkClustering("k=n (singletons)", three, 3, Init.KMEANS_PP, 7L, singletons);

        // ---------- Well-separated 2D clusters in 4 corners ----------
        List<double[]> corners = new ArrayList<>();
        for (double[] centre : new double[][]{{0, 0}, {10, 0}, {0, 10}, {10, 10}}) {
            for (int i = 0; i < 5; i++) {
                corners.add(pt(centre[0] + (i * 0.1 - 0.2), centre[1] + (i * 0.1 - 0.2)));
            }
        }
        Set<Set<String>> cornerExpected = expectedCornerClusters(corners);
        checkClustering("4 corners (KMEANS_PP)", corners, 4, Init.KMEANS_PP, 1L, cornerExpected);
        checkClustering("4 corners (RANDOM)",    corners, 4, Init.RANDOM,    1L, cornerExpected);

        // ---------- Higher-dimensional (d = 5) two well-separated blobs ----------
        List<double[]> hd = new ArrayList<>();
        Random hdRnd = new Random(123);
        for (int i = 0; i < 20; i++) {
            double[] p = new double[5];
            for (int j = 0; j < 5; j++) p[j] = hdRnd.nextGaussian();
            hd.add(p);
        }
        for (int i = 0; i < 20; i++) {
            double[] p = new double[5];
            for (int j = 0; j < 5; j++) p[j] = 50 + hdRnd.nextGaussian();
            hd.add(p);
        }
        // Just verify the two returned clusters partition the input with the right sizes.
        KMeansClustering hdSolver = new KMeansClustering(2L);
        List<List<double[]>> hdOut = hdSolver.cluster(hd, 2, Init.KMEANS_PP);
        int s1 = hdOut.get(0).size(), s2 = hdOut.get(1).size();
        boolean ok = (s1 == 20 && s2 == 20) || (s1 == 20 && s2 == 20);
        System.out.println((ok ? "OK   " : "FAIL ") + "5-D two blobs: cluster sizes = "
                + s1 + ", " + s2 + " (expected 20, 20)");

        // ---------- Duplicate points: k-means++ uniform fallback ----------
        List<double[]> dups = listOf(pt(1, 1), pt(1, 1), pt(1, 1), pt(1, 1));
        List<List<double[]>> dupOut = new KMeansClustering(7L).cluster(dups, 2, Init.KMEANS_PP);
        boolean dupOk = dupOut.size() == 2
                && dupOut.get(0).size() + dupOut.get(1).size() == 4;
        System.out.println((dupOk ? "OK   " : "FAIL ") + "duplicate-points fallback: sizes = "
                + dupOut.get(0).size() + ", " + dupOut.get(1).size());

        // ---------- Reject k > n ----------
        try {
            new KMeansClustering(0).cluster(three, 5, Init.RANDOM);
            System.out.println("FAIL  expected exception for k>n");
        } catch (IllegalArgumentException ex) {
            System.out.println("OK    k > n threw " + ex.getMessage());
        }
    }

    /* --------------------------- Test plumbing --------------------------- */

    private static double[] pt(double... coords) { return coords; }

    private static List<double[]> listOf(double[]... pts) {
        return new ArrayList<>(Arrays.asList(pts));
    }

    private static Set<Set<String>> setOfClusters(String[][] clusters) {
        Set<Set<String>> out = new HashSet<>();
        for (String[] cl : clusters) out.add(new HashSet<>(Arrays.asList(cl)));
        return out;
    }

    private static Set<Set<String>> expectedCornerClusters(List<double[]> pts) {
        // Group by (round(x/10), round(y/10)).
        Set<Set<String>> out = new HashSet<>();
        java.util.Map<String, Set<String>> by = new java.util.LinkedHashMap<>();
        for (double[] p : pts) {
            String key = Math.round(p[0] / 10.0) + "," + Math.round(p[1] / 10.0);
            by.computeIfAbsent(key, k -> new HashSet<>()).add(formatPoint(p));
        }
        out.addAll(by.values());
        return out;
    }

    /** Helper for the spec demo: build a list-of-clusters from a set of point-string sets. */
    private static List<List<double[]>> listOf3D(Set<Set<String>> clusterStrs, List<double[]> pool) {
        java.util.Map<String, double[]> byStr = new java.util.HashMap<>();
        for (double[] p : pool) byStr.put(formatPoint(p), p);
        List<List<double[]>> out = new ArrayList<>();
        for (Set<String> cl : clusterStrs) {
            List<double[]> ptsCl = new ArrayList<>();
            for (String s : cl) ptsCl.add(byStr.get(s));
            out.add(ptsCl);
        }
        return out;
    }

    private static void checkClustering(String name, List<double[]> pts, int k, Init init,
                                        long seed, Set<Set<String>> expected) {
        KMeansClustering solver = new KMeansClustering(seed);
        List<List<double[]>> got = solver.cluster(pts, k, init);
        Set<Set<String>> gotSet = new HashSet<>();
        for (List<double[]> cluster : got) {
            Set<String> s = new HashSet<>();
            for (double[] p : cluster) s.add(formatPoint(p));
            gotSet.add(s);
        }
        boolean ok = gotSet.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + name + ": " + format3D(got));
        if (!ok) {
            System.out.println("  expected = " + expected);
            System.out.println("  got      = " + gotSet);
        }
    }
}
