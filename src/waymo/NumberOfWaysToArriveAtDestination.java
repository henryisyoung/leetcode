package waymo;
/*
LeetCode 1976: Number of Ways to Arrive at Destination.

You're in a city with n intersections (labelled 0 .. n-1) and a list of bi-
directional roads.  roads[i] = {u, v, w} means there's an undirected edge
between u and v that takes w minutes.  Return the number of distinct ways to
travel from 0 to n-1 in the shortest possible time, modulo 1_000_000_007.

  - Graph is connected (any node reachable from any other).
  - At most one edge between any pair of nodes.

Stdin format (for the demo driver)
  Line 1:        n m                  // intersections 0..n-1, m undirected roads
  Next m lines:  u v w
  Output:        the number of shortest-time paths from 0 to n-1, mod 1e9+7.

LC constraints
  1 <= n               <= 200
  n - 1 <= roads.length <= n*(n-1)/2
  1 <= w               <= 10^9    (so path sum can reach ~ 2 * 10^11 → must use long)

Examples
  n=7, roads = [[0,6,7],[0,1,2],[1,2,3],[1,3,3],[6,3,3],[3,5,1],
                [6,5,1],[2,5,1],[0,4,5],[4,6,2]]
       -> 4   (four distinct shortest-time paths from 0 to 6, each of length 7)
  n=2, roads = [[1,0,10]]
       -> 1
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.StringTokenizer;

/*
Algorithm: Dijkstra with a parallel "ways" counter.

  dist[v]  shortest time from source (0) to v
  ways[v]  number of distinct shortest paths from source to v, mod 1e9+7

  Invariant maintained during relaxation of edge (u, v, w):

    let nd = dist[u] + w
    if nd  <  dist[v]:           // strictly better -> reset
        dist[v] = nd
        ways[v] = ways[u]        // inherit the count from u
        push (nd, v)
    elif nd == dist[v]:          // tie -> merge
        ways[v] = (ways[v] + ways[u]) % MOD
        // no need to push v again — its dist didn't change

  Correctness sketch:
    Dijkstra pops nodes in non-decreasing dist order.  When we pop (d, u)
    with d == dist[u], every shorter-or-equal path into u has already
    contributed to ways[u] (because each such path goes through some
    predecessor that was popped earlier).  So ways[u] is final by the
    time u is processed, and any v reachable from u with dist[u] + w ==
    dist[v] can safely absorb ways[u].

  Why we DON'T push (nd, v) on a tie:
    Tying means dist[v] is unchanged, so v will be (or already was)
    popped with the right dist.  Pushing again would just create stale
    entries that the lazy-deletion skip filters out.

  Why long for dist:
    LC: w up to 10^9 and path can have up to n-1 = 199 edges → worst
    sum ≈ 2 * 10^11.  That overflows int.  ways[] stays in int because
    we take it mod 1e9+7 every step.

Complexity
  Time:    O((n + m) log m)  Dijkstra dominated.
  Memory:  O(n + m)

Reference cross-check (countPathsRef):
  Compute dist[] with Dijkstra, build the shortest-path DAG (edges u->v
  whose dist[u] + w(u,v) == dist[v]; weights are strictly positive so
  this is acyclic), topologically sort by ascending dist, and count
  paths via DP on the DAG.  Different code path; same answer in fuzz.
*/
public class NumberOfWaysToArriveAtDestination {

    private static final int MOD = 1_000_000_007;

    /** Returns the number of shortest-time paths from 0 to n-1, mod 1e9+7. */
    public int countPaths(int n, int[][] roads) {
        if (n <= 0) return 0;
        if (n == 1) return 1;                 // single node — exactly one "trivial" path

        // Build undirected adjacency: u -> list of {v, w}.
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] r : roads) {
            int u = r[0], v = r[1], w = r[2];
            graph.computeIfAbsent(u, x -> new ArrayList<>()).add(new int[]{v, w});
            graph.computeIfAbsent(v, x -> new ArrayList<>()).add(new int[]{u, w});
        }

        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[0] = 0;

        int[] ways = new int[n];
        ways[0] = 1;

        // PQ entry: {dist, node}, ordered by dist.  Long comparator avoids subtraction-overflow.
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.offer(new long[]{0L, 0L});

        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            long d = top[0];
            int u = (int) top[1];

            // Lazy-deletion skip: stale (a shorter path to u was already settled).
            if (d > dist[u]) continue;

            List<int[]> nbrs = graph.get(u);
            if (nbrs == null) continue;
            for (int[] nb : nbrs) {
                int v = nb[0];
                long nd = d + nb[1];
                if (nd < dist[v]) {
                    dist[v] = nd;
                    ways[v] = ways[u];                  // inherit the count
                    pq.offer(new long[]{nd, v});
                } else if (nd == dist[v]) {
                    ways[v] = (ways[v] + ways[u]) % MOD;
                    // do NOT push — dist[v] unchanged; the existing PQ entry is still valid
                }
            }
        }

        return ways[n - 1];
    }

    /* --------------------------- Reference: DAG-of-shortest-paths DP --------------------------- */

    /**
     * Independent implementation used by tests:
     *   1) compute dist[] with vanilla Dijkstra
     *   2) build the shortest-path DAG (u -> v iff dist[u] + w(u,v) == dist[v])
     *   3) DP over the DAG in ascending-dist order to count paths
     *
     * Same answer as countPaths.  Useful as a sanity check because it doesn't
     * touch ways[] during the Dijkstra pop loop — the entire counting logic
     * lives in step 3.
     */
    int countPathsRef(int n, int[][] roads) {
        if (n <= 0) return 0;
        if (n == 1) return 1;

        List<int[]>[] adj = buildAdj(n, roads);

        // Step 1: shortest distances via Dijkstra.
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[0] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.offer(new long[]{0L, 0L});
        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            long d = top[0];
            int u = (int) top[1];
            if (d > dist[u]) continue;
            for (int[] nb : adj[u]) {
                long nd = d + nb[1];
                if (nd < dist[nb[0]]) {
                    dist[nb[0]] = nd;
                    pq.offer(new long[]{nd, nb[0]});
                }
            }
        }
        if (dist[n - 1] == Long.MAX_VALUE) return 0;

        // Step 2 + 3: traverse nodes in ascending-dist order, DP ways[v]
        // by summing ways[u] over every predecessor u with dist[u] + w == dist[v].
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Long.compare(dist[a], dist[b]));

        int[] ways = new int[n];
        ways[0] = 1;
        for (int v : order) {
            if (dist[v] == Long.MAX_VALUE) continue;   // unreachable; ways stays 0
            for (int[] nb : adj[v]) {
                int u = nb[0];
                long w = nb[1];
                if (dist[u] != Long.MAX_VALUE && dist[u] + w == dist[v]) {
                    ways[v] = (int) ((ways[v] + (long) ways[u]) % MOD);
                }
            }
        }
        return ways[n - 1];
    }

    @SuppressWarnings("unchecked")
    private static List<int[]>[] buildAdj(int n, int[][] roads) {
        List<int[]>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] r : roads) {
            adj[r[0]].add(new int[]{r[1], r[2]});
            adj[r[1]].add(new int[]{r[0], r[2]});
        }
        return adj;
    }

    /* --------------------------- IO + demo --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try { return System.in.available() > 0; } catch (IOException e) { return false; }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        int[][] roads = new int[m][3];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            roads[i][0] = Integer.parseInt(st.nextToken());
            roads[i][1] = Integer.parseInt(st.nextToken());
            roads[i][2] = Integer.parseInt(st.nextToken());
        }
        System.out.println(new NumberOfWaysToArriveAtDestination().countPaths(n, roads));
    }

    private static void runDemos() {
        NumberOfWaysToArriveAtDestination solver = new NumberOfWaysToArriveAtDestination();

        // LC example 1: expected 4
        check(solver, 7, new int[][]{
                {0,6,7},{0,1,2},{1,2,3},{1,3,3},{6,3,3},
                {3,5,1},{6,5,1},{2,5,1},{0,4,5},{4,6,2}
        }, 4);

        // LC example 2: trivial
        check(solver, 2, new int[][]{{1,0,10}}, 1);

        // n == 1: single node — exactly one "stay put" path.
        check(solver, 1, new int[][]{}, 1);

        // Direct edge vs. detour of higher cost — only direct wins.
        check(solver, 3, new int[][]{{0,1,1},{1,2,1},{0,2,5}}, 1);

        // Two equal-cost paths (diamond).
        //   0 -1- a -1- 2
        //   0 -1- b -1- 2
        check(solver, 4, new int[][]{{0,1,1},{0,2,1},{1,3,1},{2,3,1}}, 2);

        // Three equal-cost paths (broader diamond).
        check(solver, 5, new int[][]{
                {0,1,1},{0,2,1},{0,3,1},{1,4,1},{2,4,1},{3,4,1}
        }, 3);

        // Equal-cost paths multiply through stacked diamonds: 2 * 2 = 4 paths.
        //   0 -- {1,2} -- 3 -- {4,5} -- 6
        check(solver, 7, new int[][]{
                {0,1,1},{0,2,1},{1,3,1},{2,3,1},
                {3,4,1},{3,5,1},{4,6,1},{5,6,1}
        }, 4);

        // Heavy-weight test: weights near LC max.  Path 0 -> 1 -> 2 = 2 * 1e9
        // ties with the direct 0 -> 2 = 2 * 1e9 ⇒ 2 shortest paths.
        check(solver, 3, new int[][]{
                {0,1,1_000_000_000},{1,2,1_000_000_000},{0,2,2_000_000_000}
        }, 2);

        // ---------- Random fuzz against the DAG-DP reference ----------
        Random rnd = new Random(11);
        int trials = 300, fails = 0;
        for (int t = 0; t < trials; t++) {
            int n = 2 + rnd.nextInt(7);                       // 2..8 nodes
            // Build a random connected graph (start with a spanning tree, then sprinkle extras).
            List<int[]> edges = new ArrayList<>();
            boolean[][] used = new boolean[n][n];
            // Spanning tree via random parents.
            Integer[] order = new Integer[n];
            for (int i = 0; i < n; i++) order[i] = i;
            shuffle(order, rnd);
            for (int i = 1; i < n; i++) {
                int u = order[i], v = order[rnd.nextInt(i)];
                int a = Math.min(u, v), b = Math.max(u, v);
                used[a][b] = true;
                edges.add(new int[]{a, b, 1 + rnd.nextInt(5)});
            }
            // Add up to 5 random extra edges.
            int extra = rnd.nextInt(6);
            for (int e = 0; e < extra; e++) {
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                if (u == v) continue;
                int a = Math.min(u, v), b = Math.max(u, v);
                if (used[a][b]) continue;
                used[a][b] = true;
                edges.add(new int[]{a, b, 1 + rnd.nextInt(5)});
            }
            int[][] roads = edges.toArray(new int[0][]);

            int got = solver.countPaths(n, roads);
            int ref = solver.countPathsRef(n, roads);
            if (got != ref) {
                fails++;
                System.out.println("MISMATCH n=" + n + " got=" + got + " ref=" + ref
                        + " roads=" + Arrays.deepToString(roads));
            }
        }
        System.out.println("Fuzz: " + (trials - fails) + "/" + trials + " ok");

        // ---------- Stress: LC max size ----------
        int N = 200;
        int M = N * (N - 1) / 2;                              // dense (complete) graph
        int[][] big = new int[M][3];
        int idx = 0;
        Random brnd = new Random(7);
        for (int u = 0; u < N; u++) {
            for (int v = u + 1; v < N; v++) {
                big[idx][0] = u;
                big[idx][1] = v;
                big[idx][2] = 1 + brnd.nextInt(1_000_000_000);
                idx++;
            }
        }
        long t0 = System.nanoTime();
        int ans = solver.countPaths(N, big);
        long us = (System.nanoTime() - t0) / 1_000;
        System.out.println("Stress n=" + N + " m=" + M + ": ans=" + ans + " in " + us + " us");
    }

    private static void check(NumberOfWaysToArriveAtDestination solver, int n, int[][] roads, int expected) {
        int got = solver.countPaths(n, roads);
        int ref = solver.countPathsRef(n, roads);
        boolean ok = got == expected && ref == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "n=" + n + " expected=" + expected
                + " dijkstra=" + got + " ref=" + ref);
    }

    private static <T> void shuffle(T[] a, Random r) {
        for (int i = a.length - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            T tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
    }
}
