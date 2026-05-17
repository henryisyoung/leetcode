package waymo;

import java.util.*;

public class PathWithMaximumProbability {

    public double maxProbability(int n, int[][] edges, double[] succProb, int start_node, int end_node) {
        if (n <= 0) return 0;

        List<double[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int i = 0; i < edges.length; i++) {
            int u = edges[i][0], v = edges[i][1];
            double w = succProb[i];
            graph[u].add(new double[]{v, w});
            graph[v].add(new double[]{u, w});
        }

        double[] prob = new double[n];
        prob[start_node] = 1.0;

        PriorityQueue<double[]> pq = new PriorityQueue<>((a, b) -> Double.compare(b[1], a[1]));
        pq.add(new double[]{start_node, 1.0});

        while (!pq.isEmpty()) {
            double[] cur = pq.poll();
            int u = (int) cur[0];
            double p = cur[1];
            if (u == end_node) return p;
            if (p < prob[u]) continue;

            for (double[] next : graph[u]) {
                int v = (int) next[0];
                double np = p * next[1];
                if (np > prob[v]) {
                    prob[v] = np;
                    pq.add(new double[]{v, np});
                }
            }
        }
        return prob[end_node];
    }
}
