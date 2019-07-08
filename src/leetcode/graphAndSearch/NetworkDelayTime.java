package leetcode.graphAndSearch;

import java.util.*;

public class NetworkDelayTime {
    public static int networkDelayTime(int[][] times, int N, int K) {
        int max = 0;
        Map<Integer, List<Integer>> map = new HashMap<>();
        int[][] edges = new int[N + 1][N + 1];
        for (int[] edge : times) {
            int from = edge[0], to = edge[1], cost = edge[2];
            edges[from][to] = cost;
            if (!map.containsKey(from)) map.put(from, new ArrayList<>());
            map.get(from).add(to);
        }
        PriorityQueue<int[]> pq = new PriorityQueue<>(N, (a, b) -> (a[1] - b[1]));
        pq.add(new int[]{K, 0});
        int[] dists = new int[N + 1];
        Arrays.fill(dists, Integer.MAX_VALUE);
        dists[K] = 0;
        int count = 0;

        while (!pq.isEmpty()) {
            int[] node = pq.poll();
            int from = node[0], dist = node[1];
            if (dist > dists[from]) continue;
            max = Math.max(max, dist);
            count++;
            if (map.containsKey(from)) {
                for (int to : map.get(from)) {
                    if (dists[to] > dist + edges[from][to]) {
                        dists[to] = dist + edges[from][to];
                        pq.add(new int[]{to, dists[to]});
                    }
                }
            }
        }
        return count == N ? max : -1;
    }

    public static void main(String[] args) {
        int[][] times = {{2,1,1},{2,3,1},{3,4,1}};
        int[][] times2 = {{1,2,1},{2,1,3}};
        int[][] times3 = {{2,1,1},{2,3,1},{3,4,1}};
        int[][] times4 = {{1,2,1},{2,3,2},{1,3,4}};
        int N = 4, K = 2;
//        System.out.println(networkDelayTime(times, N, K));
//        System.out.println(networkDelayTime(times2, 2, 2));
//        System.out.println(networkDelayTime(times3, 4, 2));
        System.out.println(networkDelayTime(times4, 3, 1));
    }
}
