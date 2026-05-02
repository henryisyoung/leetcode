package snowflake.mianjing.selfreview;

import java.util.*;

public class CheapestFlightsWithinKStops {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
        if (n <= 0) return -1;
        List<int[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] f : flights) graph[f[0]].add(new int[]{f[1], f[2]});
        // PriorityQueue of {cost, node, edgesUsed}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.add(new int[]{0, src, 0});
        // Best edges-used to reach a node at any cost we've enqueued.
        int[] minEdges = new int[n];
        Arrays.fill(minEdges, Integer.MAX_VALUE);
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int cost = cur[0], u = cur[1], edges = cur[2];
            if (u == dst) return cost;
            if (edges > K) continue;                 // K stops = at most K+1 edges
            if (edges >= minEdges[u]) continue;      // already reached u with fewer edges
            minEdges[u] = edges;
            for (int[] e : graph[u]) {
                pq.add(new int[]{cost + e[1], e[0], edges + 1});
            }
        }
        return -1;
    }
}