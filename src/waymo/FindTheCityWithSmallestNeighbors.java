package waymo;
/*
LeetCode 1334: Find the City With the Smallest Number of Neighbors at a
Threshold Distance.

You are given n cities numbered 0..n-1 and an undirected weighted edge list
edges[i] = {from_i, to_i, weight_i}, plus an integer distanceThreshold.

For each city, count how many *other* cities are reachable via a path whose
total weight is <= distanceThreshold. Return the city with the smallest such
count.  Tie-break: return the city with the greatest index.

Examples
  n=4, edges=[[0,1,3],[1,2,1],[1,3,4],[2,3,1]], threshold=4   -> 3
  n=5, edges=[[0,1,2],[0,4,8],[1,2,3],[1,4,2],[2,3,1],[3,4,1]], threshold=2 -> 0

Constraints
  2 <= n <= 100
  1 <= edges.length <= n*(n-1)/2
  edges[i].length == 3
  0 <= from_i < to_i < n
  1 <= weight_i, distanceThreshold <= 10^4
  All (from_i, to_i) pairs are distinct.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/*
Algorithm: All-pairs shortest paths via Floyd-Warshall, then count.

  Why Floyd-Warshall:
    n <= 100, so n^3 = 10^6 ops — trivial. We need the *all-pairs*
    distance matrix, which is exactly what Floyd-Warshall produces in
    one O(n^3) sweep, with no heap, no adjacency list, and no per-source
    bookkeeping.

  dist[i][j]  shortest distance from i to j; INF if no path
              dist[i][i] = 0, dist[u][v] = dist[v][u] = w for each edge

  Relaxation:
    for k in 0..n-1:
      for i in 0..n-1:
        for j in 0..n-1:
          if dist[i][k] + dist[k][j] < dist[i][j]:
            dist[i][j] = dist[i][k] + dist[k][j]

    Order matters: k is the *outer* loop. Intuition: after iteration k,
    dist[i][j] holds the shortest path that only uses intermediate
    vertices from {0..k}.

  Overflow guard:
    Use Integer.MAX_VALUE as INF and skip additions when either side
    is INF. Adding 10^4 + 10^4 fits in int comfortably; the only risk
    is INF + finite, hence the explicit check.

  Tie-break:
    "Smallest count, greatest index on tie" -> iterate cities in order
    and use `<=` when updating the best, so a later (larger-index) city
    with an equal count wins.

Complexity
  Time:   O(n^3)
  Memory: O(n^2)
*/
public class FindTheCityWithSmallestNeighbors {

    /** Floyd-Warshall solution. */
    public int findTheCity(int n, int[][] edges, int distanceThreshold) {
        if (n <= 0) return -1;

        final int INF = Integer.MAX_VALUE;
        int[][] dist = new int[n][n];
        for (int[] row : dist) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) dist[i][i] = 0;

        // Undirected: set both directions. Inputs are guaranteed unique pairs.
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            dist[u][v] = w;
            dist[v][u] = w;
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == INF) continue;        // no path i -> k yet, nothing to relax through k
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == INF) continue;
                    int nd = dist[i][k] + dist[k][j];   // safe: both finite, weights up to 10^4, n <= 100
                    if (nd < dist[i][j]) dist[i][j] = nd;
                }
            }
        }

        int bestCity = -1, bestCount = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (j != i && dist[i][j] <= distanceThreshold) count++;
            }
            // `<=` so larger index wins ties (matches the tie-break rule).
            if (count <= bestCount) {
                bestCount = count;
                bestCity = i;
            }
        }
        return bestCity;
    }

    /* --------------------------- Dijkstra-per-source reference --------------------------- */

    /** Run Dijkstra from each source; same answer, used to cross-check Floyd-Warshall. */
    int findTheCityDijkstra(int n, int[][] edges, int distanceThreshold) {
        List<int[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) {
            graph[e[0]].add(new int[]{e[1], e[2]});
            graph[e[1]].add(new int[]{e[0], e[2]});
        }

        int bestCity = -1, bestCount = Integer.MAX_VALUE;
        for (int src = 0; src < n; src++) {
            int[] dist = new int[n];
            Arrays.fill(dist, Integer.MAX_VALUE);
            dist[src] = 0;

            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
            pq.offer(new int[]{src, 0});
            while (!pq.isEmpty()) {
                int[] cur = pq.poll();
                int u = cur[0], d = cur[1];
                if (d > dist[u]) continue;
                if (d > distanceThreshold) break;       // nothing further can be within threshold
                for (int[] nb : graph[u]) {
                    int v = nb[0], nd = d + nb[1];
                    if (nd < dist[v]) {
                        dist[v] = nd;
                        pq.offer(new int[]{v, nd});
                    }
                }
            }

            int count = 0;
            for (int j = 0; j < n; j++) {
                if (j != src && dist[j] <= distanceThreshold) count++;
            }
            if (count <= bestCount) {
                bestCount = count;
                bestCity = src;
            }
        }
        return bestCity;
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        FindTheCityWithSmallestNeighbors solver = new FindTheCityWithSmallestNeighbors();

        // LC examples
        check(solver, 4, new int[][]{{0,1,3},{1,2,1},{1,3,4},{2,3,1}}, 4, 3);
        check(solver, 5, new int[][]{{0,1,2},{0,4,8},{1,2,3},{1,4,2},{2,3,1},{3,4,1}}, 2, 0);

        // 2-node, edge over threshold -> both reach 0 cities; tie -> larger index = 1.
        check(solver, 2, new int[][]{{0,1,5}}, 4, 1);
        // 2-node, edge within threshold -> both reach 1; tie -> 1.
        check(solver, 2, new int[][]{{0,1,5}}, 5, 1);

        // No edges: every city reaches 0 others, return n-1.
        check(solver, 4, new int[][]{}, 10, 3);

        // Disconnected components.
        // 0-1 (w=1), 2-3 (w=1), threshold=1. Each city reaches exactly 1 other -> tie -> 3.
        check(solver, 4, new int[][]{{0,1,1},{2,3,1}}, 1, 3);

        // ---------- Random fuzz: Floyd-Warshall vs Dijkstra ----------
        Random rnd = new Random(7);
        int trials = 200, mismatches = 0;
        for (int t = 0; t < trials; t++) {
            int n = 2 + rnd.nextInt(9);                     // 2..10
            int maxEdges = n * (n - 1) / 2;
            int m = rnd.nextInt(maxEdges + 1);
            boolean[][] used = new boolean[n][n];
            List<int[]> es = new ArrayList<>();
            int produced = 0, attempts = 0;
            while (produced < m && attempts < 500) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                attempts++;
                if (u == v) continue;
                int a = Math.min(u, v), b = Math.max(u, v);
                if (used[a][b]) continue;
                used[a][b] = true;
                es.add(new int[]{a, b, 1 + rnd.nextInt(20)});
                produced++;
            }
            int[][] edges = es.toArray(new int[0][]);
            int threshold = 1 + rnd.nextInt(50);
            int fw = solver.findTheCity(n, edges, threshold);
            int dj = solver.findTheCityDijkstra(n, edges, threshold);
            if (fw != dj) {
                mismatches++;
                System.out.println("MISMATCH n=" + n + " thr=" + threshold
                        + " fw=" + fw + " dj=" + dj
                        + " edges=" + Arrays.deepToString(edges));
            }
        }
        System.out.println("Random cross-check: " + (trials - mismatches) + "/" + trials + " ok");

        // ---------- Stress: LC max input size ----------
        int N = 100;
        int M = N * (N - 1) / 2;
        int[][] big = new int[M][3];
        int idx = 0;
        Random brnd = new Random(3);
        for (int i = 0; i < N; i++)
            for (int j = i + 1; j < N; j++)
                big[idx++] = new int[]{i, j, 1 + brnd.nextInt(10_000)};
        long t0 = System.nanoTime();
        int ans = solver.findTheCity(N, big, 5_000);
        long us = (System.nanoTime() - t0) / 1_000;
        System.out.println("Stress n=100 dense: ans=" + ans + " in " + us + " us");
    }

    private static void check(FindTheCityWithSmallestNeighbors solver,
                              int n, int[][] edges, int threshold, int expected) {
        int got = solver.findTheCity(n, edges, threshold);
        int dj = solver.findTheCityDijkstra(n, edges, threshold);
        boolean ok = got == expected && dj == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "n=" + n + " thr=" + threshold
                + " expected=" + expected + " fw=" + got + " dj=" + dj);
    }
}
