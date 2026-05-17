package waymo;
/*
Problem: ETA on a Semantic Map (shortest-path on a weighted directed graph)

Given a directed graph (n nodes, m edges) with non-negative edge weights w,
plus a source s and target t, compute the minimum total weight of any s→t
path.  If t is unreachable from s, return -1.

Stdin format
  Line 1:   n m                 // nodes 0..n-1, m directed edges
  Lines 2..m+1:  u v w          // directed edge u -> v, weight w >= 0
  Last line: s t                // source and target

Output: the minimum ETA, or -1 if unreachable.

Constraints
  1 <= n     <= 2 * 10^5
  0 <= m     <= 3 * 10^5
  0 <= w     <= 10^9    (so a path sum can reach ~ 2 * 10^14 — long, not int)

Example
  5 6
  0 1 2
  0 2 5
  1 2 1
  1 3 2
  2 3 1
  3 4 3
  0 4
  → 7
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
Algorithm: Dijkstra with a min-heap and lazy stale-entry skipping.

  dist[v]  initial-infinity, dist[s] = 0
  pq       min-heap of (currentDist, node) pairs
  pop      until empty; if (d, u) is popped with d > dist[u], skip (stale)
  relax    for each edge (u, v, w):  if d + w < dist[v]:  dist[v] = d + w;  push (dist[v], v)
  early-exit  return d as soon as u == t (Dijkstra guarantees popped == final)

  Why lazy skipping is safe:  Dijkstra normally requires a decrease-key
  operation, but Java's heap doesn't support one in O(log n).  Pushing a
  duplicate and skipping on pop is equivalent in correctness and keeps
  heap size to O(m).

  Why long, not int:  worst-case path is n - 1 edges of weight 10^9, sum
  up to ~ 2 * 10^14 — comfortably outside int.

Complexity
  Time:   O((n + m) log m)
  Memory: O(n + m)
*/
public class DijkstraEta2 {

    /**
     * Returns the minimum total edge weight along any s → t path, or -1 if t is unreachable.
     *
     * @param n     number of nodes (0..n-1)
     * @param edges directed edges; each is {u, v, w} with w >= 0
     */
    public long eta(int n, int[][] edges, int s, int t) {
        if(n == 0) {
            return 0;
        }

        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[s] = 0;
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] edge : edges) {
            int from = edge[0], to = edge[1], len = edge[2];
            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(new int[]{to, len});
        }

        // from s to cur node dist
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> (a[0] - b[0]));

        pq.add(new int[]{0, s});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int curDist = curr[0];
            int from = curr[1];
            if (from == t) {
                return curDist;
            }
            if (graph.containsKey(from)) {
                for (int[] next : graph.get(from)) {
                    int to = next[0], len = next[1];
                    if (curDist + len >= dist[to]) continue;
                    dist[to] = curDist + len;
                    pq.add(new int[]{dist[to], to});
                }
            }
        }

        return dist[t] == Integer.MAX_VALUE ? -1 : dist[t];
    }

    /* --------------------------- Brute reference for tests --------------------------- */

    /** O(n * m) Bellman-Ford for cross-checking the Dijkstra result on small inputs. */
    long etaBellmanFord(int n, int[][] edges, int s, int t) {
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[s] = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean changed = false;
            for (int[] e : edges) {
                if (dist[e[0]] == Long.MAX_VALUE) continue;
                long nd = dist[e[0]] + e[2];
                if (nd < dist[e[1]]) {
                    dist[e[1]] = nd;
                    changed = true;
                }
            }
            if (!changed) break;
        }
        return dist[t] == Long.MAX_VALUE ? -1 : dist[t];
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
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        int[][] edges = new int[m][3];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            edges[i][0] = Integer.parseInt(st.nextToken());
            edges[i][1] = Integer.parseInt(st.nextToken());
            edges[i][2] = Integer.parseInt(st.nextToken());
        }
        st = new StringTokenizer(br.readLine());
        int s = Integer.parseInt(st.nextToken());
        int t = Integer.parseInt(st.nextToken());
        System.out.println(new DijkstraEta2().eta(n, edges, s, t));
    }

    /* --------------------------- Demo + tests --------------------------- */

    private static void runDemos() {
        DijkstraEta2 solver = new DijkstraEta2();

        // Spec example: 0 -> 1 -> 3 -> 4  costs 2 + 2 + 3 = 7.
        check(solver, 5, new int[][]{
                {0, 1, 2}, {0, 2, 5}, {1, 2, 1}, {1, 3, 2}, {2, 3, 1}, {3, 4, 3}
        }, 0, 4, 7);

        // Source == target.
        check(solver, 3, new int[][]{{0, 1, 5}, {1, 2, 5}}, 1, 1, 0);

        // Unreachable target.
        check(solver, 4, new int[][]{{0, 1, 1}, {1, 2, 1}}, 0, 3, -1);

        // No edges at all.
        check(solver, 3, new int[][]{}, 0, 2, -1);
        check(solver, 3, new int[][]{}, 0, 0, 0);   // source == target with empty graph

        // Direct edge is NOT the shortest — must detour.
        check(solver, 4, new int[][]{
                {0, 3, 100},                 // direct but expensive
                {0, 1, 1}, {1, 2, 1}, {2, 3, 1}
        }, 0, 3, 3);

        // Multiple edges between the same nodes — Dijkstra naturally takes the cheapest.
        check(solver, 2, new int[][]{
                {0, 1, 10}, {0, 1, 3}, {0, 1, 7}
        }, 0, 1, 3);

        // Self-loop is harmless.
        check(solver, 2, new int[][]{
                {0, 0, 5}, {0, 1, 4}
        }, 0, 1, 4);

        // Zero-weight edges: shortest can have length 0.
        check(solver, 3, new int[][]{
                {0, 1, 0}, {1, 2, 0}
        }, 0, 2, 0);

        // Long-arithmetic check: a chain of 10 edges with weight 10^9 sums to 10^10, breaking int.
        int[][] chain = new int[10][3];
        for (int i = 0; i < 10; i++) chain[i] = new int[]{i, i + 1, 1_000_000_000};
        check(solver, 11, chain, 0, 10, 10_000_000_000L);

        // ---------- Cross-check against Bellman-Ford on 100 random small graphs ----------
        Random rnd = new Random(42);
        int mismatches = 0;
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(8);             // 2..9 nodes
            int m = rnd.nextInt(n * (n - 1) + 1);   // up to a dense graph
            int[][] edges = new int[m][3];
            for (int i = 0; i < m; i++) {
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                int w = rnd.nextInt(50);
                edges[i] = new int[]{u, v, w};
            }
            int s = rnd.nextInt(n), tgt = rnd.nextInt(n);
            long a = solver.eta(n, edges, s, tgt);
            long b = solver.etaBellmanFord(n, edges, s, tgt);
            if (a != b) {
                mismatches++;
                System.out.println("MISMATCH n=" + n + " s=" + s + " t=" + tgt
                        + " dij=" + a + " bf=" + b + " edges=" + Arrays.deepToString(edges));
            }
        }
        System.out.println("Random cross-check: " + (100 - mismatches) + "/100 ok");

        // ---------- Performance: n = 200K, m = 300K ----------
        int n = 200_000, m = 300_000;
        Random big = new Random(7);
        int[][] bigEdges = new int[m][3];
        for (int i = 0; i < m; i++) {
            int u = big.nextInt(n), v = big.nextInt(n);
            int w = big.nextInt(1_000_000_000);
            bigEdges[i] = new int[]{u, v, w};
        }
        int s = 0, tgt = n - 1;
        long t0 = System.nanoTime();
        long ans = solver.eta(n, bigEdges, s, tgt);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress n=" + n + " m=" + m + ": ans=" + ans + " in " + ms + " ms");
    }

    private static void check(DijkstraEta2 solver, int n, int[][] edges, int s, int t, long expected) {
        long got = solver.eta(n, edges, s, t);
        long bf = solver.etaBellmanFord(n, edges, s, t);
        boolean ok = got == expected && bf == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "n=" + n + " m=" + edges.length + " s=" + s + " t=" + t
                + " expected=" + expected + " dij=" + got + " bf=" + bf);
    }
}
