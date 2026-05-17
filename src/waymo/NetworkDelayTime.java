package waymo;
/*
LeetCode 743: Network Delay Time.

You are given a network of n nodes labelled 1..n and a list of directed travel
times  times[i] = {u, v, w}  meaning a signal takes w time to travel from u to v.

We send a signal from a starting node k.  Return the minimum time for the
signal to reach *every* node, or -1 if some node is unreachable.

  answer = max over all nodes v of dist[v],  or -1 if any dist[v] is infinity.

Stdin format (for the demo driver)
  Line 1:        n m k                 // nodes 1..n, m directed edges, source k
  Next m lines:  u v w                 // directed edge u -> v with weight w >= 0
  Output:        the answer, or -1.

LC constraints
  1 <= k <= n <= 100
  1 <= times.length <= 6000
  0 <= w <= 100
  (ui, vi) pairs are unique; no self-loops.

Examples
  times = [[2,1,1],[2,3,1],[3,4,1]], n=4, k=2  ->  2
  times = [[1,2,1]],                  n=2, k=1  ->  1
  times = [[1,2,1]],                  n=2, k=2  -> -1   (node 1 unreachable from 2)
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
Algorithm: Dijkstra from k, then max over the final dist[] array.

  dist[v]   initialized to +infinity, dist[k] = 0
  pq        min-heap of (currentDist, node) pairs, ordered by currentDist
  pop       (d, u) — if d > dist[u]: skip (stale).  Otherwise relax edges.
  relax     for each edge (u, v, w): if d + w < dist[v]: dist[v] = d + w; push (dist[v], v)
  answer    max(dist[1..n]); if any is still infinity, return -1.

  Why "lazy stale skip" rather than decrease-key:
    Java's PriorityQueue has no O(log n) decrease-key, so the canonical
    trick is to allow duplicate (dist, node) entries and skip the stale
    ones when popped (`d > dist[u]`).  Same correctness, heap size O(m).

  Why arrays are sized n + 1, not n:
    Nodes are 1-indexed (1..n).  Using size n with 0-based indexing
    would AIOOBE on dist[n] and silently skip node n / always read
    dist[0] (which is never written).

  Adjacency-list representation:
    Map<Integer, List<int[]>>  graph.get(u) -> list of {v, w} pairs.
    Plain, idiomatic, easy to read.  At LC scale (n <= 100, m <= 6000)
    the constant-factor difference vs. a primitive int[] CSR is
    irrelevant; DijkstraEta.java keeps the CSR version for the
    larger-graph case (n up to 2 * 10^5).

Complexity
  Time:   O((n + m) log m)
  Memory: O(n + m)
*/
public class NetworkDelayTime {

    /** Returns the minimum time for the signal to reach every node from k, or -1 if any node is unreachable. */
    public int networkDelayTime(int[][] times, int n, int k) {
        if (n <= 0) return 0;
        if (k < 1 || k > n) return -1;        // nodes are 1..n; both ends inclusive

        // Build adjacency: u -> list of {v, w}.  computeIfAbsent only allocates on a miss.
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] e : times) {
            graph.computeIfAbsent(e[0], x -> new ArrayList<>()).add(new int[]{e[1], e[2]});
        }

        // Size n + 1 because nodes are 1..n; index 0 is unused.
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        // PQ entry: {dist, node}.  Use Integer.compare to avoid subtraction-overflow.
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.offer(new int[]{k, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0], d = cur[1];

            // Lazy-deletion skip: this entry was superseded by a shorter path already settled.
            if (d > dist[u]) continue;

            List<int[]> nbrs = graph.get(u);
            if (nbrs == null) continue;       // sink with no outgoing edges
            for (int[] nb : nbrs) {
                int v = nb[0], wt = nb[1];
                int nd = d + wt;              // LC: w <= 100, n <= 100 → nd <= 10^4, no overflow
                if (nd < dist[v]) {
                    dist[v] = nd;
                    pq.offer(new int[]{v, nd});
                }
            }
        }

        int max = 0;
        for (int v = 1; v <= n; v++) {        // walk 1..n (node ids), NOT 0..n-1
            if (dist[v] == Integer.MAX_VALUE) return -1;
            if (dist[v] > max) max = dist[v];
        }
        return max;
    }

    /* --------------------------- Bellman-Ford reference for tests --------------------------- */

    /** O(n*m) Bellman-Ford.  Used only to cross-check the Dijkstra implementation. */
    int networkDelayTimeBellmanFord(int[][] times, int n, int k) {
        if (n <= 0) return 0;
        if (k < 1 || k > n) return -1;
        long[] dist = new long[n + 1];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[k] = 0;
        for (int iter = 0; iter < n - 1; iter++) {
            boolean changed = false;
            for (int[] e : times) {
                int u = e[0], v = e[1], wt = e[2];
                if (dist[u] != Long.MAX_VALUE && dist[u] + wt < dist[v]) {
                    dist[v] = dist[u] + wt;
                    changed = true;
                }
            }
            if (!changed) break;
        }
        long max = 0;
        for (int v = 1; v <= n; v++) {
            if (dist[v] == Long.MAX_VALUE) return -1;
            if (dist[v] > max) max = dist[v];
        }
        return (int) max;
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
        int k = Integer.parseInt(st.nextToken());

        int[][] times = new int[m][3];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            times[i][0] = Integer.parseInt(st.nextToken());
            times[i][1] = Integer.parseInt(st.nextToken());
            times[i][2] = Integer.parseInt(st.nextToken());
        }
        System.out.println(new NetworkDelayTime().networkDelayTime(times, n, k));
    }

    private static void runDemos() {
        NetworkDelayTime solver = new NetworkDelayTime();

        // LC examples
        check(solver, new int[][]{{2,1,1},{2,3,1},{3,4,1}}, 4, 2,  2);
        check(solver, new int[][]{{1,2,1}},                  2, 1,  1);
        check(solver, new int[][]{{1,2,1}},                  2, 2, -1);

        // Single-node graph (the test that exposes the k>=n / off-by-one bugs)
        check(solver, new int[][]{}, 1, 1, 0);

        // k == n with a path reaching all nodes — would FAIL the buggy `k >= n` guard
        check(solver, new int[][]{{4,1,1},{1,2,1},{2,3,1}}, 4, 4, 3);

        // Two nodes, both directions but only one reachable from k
        check(solver, new int[][]{{1,2,5}}, 2, 1, 5);
        check(solver, new int[][]{{1,2,5}}, 2, 2, -1);

        // Multi-hop chain: 1 -> 2 -> 3 -> 4 -> 5
        check(solver, new int[][]{{1,2,1},{2,3,1},{3,4,1},{4,5,1}}, 5, 1, 4);

        // Diamond: 1->2 = 4, 1->3 = 2, 2->4 = 1, 3->4 = 5
        // dist[1]=0, dist[2]=4, dist[3]=2, dist[4]=min(4+1, 2+5)=5 → max = 5.
        check(solver, new int[][]{
                {1,2,4}, {1,3,2}, {2,4,1}, {3,4,5}
        }, 4, 1, 5);

        // Zero-weight edges are valid (LC allows w = 0)
        check(solver, new int[][]{{1,2,0},{2,3,0}}, 3, 1, 0);

        // Multiple paths, ensure we pick the shortest
        check(solver, new int[][]{
                {1,2,10},{1,3,1},{3,2,2}     // direct 1->2 = 10, via 3 = 1+2 = 3
        }, 3, 1, 3);

        // Disconnected component
        check(solver, new int[][]{{1,2,1},{3,4,1}}, 4, 1, -1);

        // ---------- Random fuzz against Bellman-Ford ----------
        Random rnd = new Random(13);
        int mismatches = 0, trials = 200;
        for (int t = 0; t < trials; t++) {
            int n = 2 + rnd.nextInt(8);                  // 2..9 nodes
            int maxEdges = Math.min(n * (n - 1), 20);
            int m = rnd.nextInt(maxEdges + 1);
            // Build edge set with unique (u,v) pairs, no self-loops.
            boolean[][] used = new boolean[n + 1][n + 1];
            int[][] times = new int[m][3];
            int produced = 0;
            int tries = 0;
            while (produced < m && tries < 200) {
                int u = 1 + rnd.nextInt(n);
                int v = 1 + rnd.nextInt(n);
                tries++;
                if (u == v || used[u][v]) continue;
                used[u][v] = true;
                times[produced][0] = u;
                times[produced][1] = v;
                times[produced][2] = rnd.nextInt(101);   // 0..100
                produced++;
            }
            times = Arrays.copyOf(times, produced);
            int k = 1 + rnd.nextInt(n);
            int a = solver.networkDelayTime(times, n, k);
            int b = solver.networkDelayTimeBellmanFord(times, n, k);
            if (a != b) {
                mismatches++;
                System.out.println("MISMATCH n=" + n + " k=" + k
                        + " dijkstra=" + a + " bf=" + b
                        + " times=" + Arrays.deepToString(times));
            }
        }
        System.out.println("Random cross-check: " + (trials - mismatches) + "/" + trials + " ok");

        // ---------- Stress: LC max input size ----------
        int N = 100, M = 6000;
        int[][] big = new int[M][3];
        boolean[][] used = new boolean[N + 1][N + 1];
        Random brnd = new Random(3);
        int filled = 0;
        while (filled < M) {
            int u = 1 + brnd.nextInt(N);
            int v = 1 + brnd.nextInt(N);
            if (u == v || used[u][v]) continue;
            used[u][v] = true;
            big[filled][0] = u;
            big[filled][1] = v;
            big[filled][2] = brnd.nextInt(101);
            filled++;
        }
        long t0 = System.nanoTime();
        int ans = solver.networkDelayTime(big, N, 1);
        long us = (System.nanoTime() - t0) / 1_000;
        System.out.println("Stress n=100 m=6000: ans=" + ans + " in " + us + " us");
    }

    private static void check(NetworkDelayTime solver, int[][] times, int n, int k, int expected) {
        int got = solver.networkDelayTime(times, n, k);
        int bf = solver.networkDelayTimeBellmanFord(times, n, k);
        boolean ok = got == expected && bf == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "n=" + n + " k=" + k + " expected=" + expected
                + " dijkstra=" + got + " bellman=" + bf);
    }
}
