package snowflake.mianjing.selfreview;

import java.util.*;

public class CheapestFlightsWithinKStops2 {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
        if (n <= 0 || K < 0) {
            return -1;
        }

        List<int[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] flight : flights) {
            int from = flight[0], to = flight[1], cost = flight[2];
            graph[from].add(new int[]{to, cost});
        }

        // 0: place, 1: costs, 2: stops
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{src, 0, 0});

        int[] minStops = new int[n];
        Arrays.fill(minStops, Integer.MAX_VALUE);

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int place = cur[0], costs = cur[1], stops = cur[2];
            if (place == dst) {
                return costs;
            }
            if (stops > K) continue;
            if (minStops[place] <= stops) continue;
            minStops[place] = stops;
            for (int[] next : graph[place]) {
                int nextPlace = next[0], nextCost = next[1];
                pq.add(new int[]{nextPlace, costs + nextCost, stops + 1});
            }
        }

        return -1;
    }
}