package airbnb.Phone;

import java.util.Arrays;
import java.util.PriorityQueue;

public class CheapestFlightsWithinKStops {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
        int[][] costMap = new int[n][n];
        for (int[] flight : flights) {
            int from = flight[0], to = flight[1];
            costMap[from][to] = flight[2];
        }
        int[] costs = new int[n];
        int[] stops = new int[n];
        Arrays.fill(costs, Integer.MAX_VALUE);
        Arrays.fill(stops, Integer.MAX_VALUE);

        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (a.cost - b.cost));
        pq.add(new Node(src, 0, 0));

        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            if (cur.place == dst) {
                return cur.cost;
            }
            if (cur.stops == K + 1) {
                continue;
            }
            int curPlace = cur.place, curCost = cur.cost, curStops = cur.stops;
            for (int nextPlace = 0; nextPlace < n; nextPlace++) {
                if (costMap[curPlace][nextPlace] > 0) {
                    int nextCost = curCost + costMap[curPlace][nextPlace];
                    if (nextCost < costs[nextPlace]) {
                        costs[nextPlace] = nextCost;
                        stops[nextPlace] = curStops + 1;
                        pq.add(new Node(nextPlace, nextCost, curStops + 1));
                    } else if (curStops + 1 < stops[nextPlace]) {
                        pq.add(new Node(nextPlace, nextCost, curStops + 1));
                    }
                }
            }
        }

        return -1;
    }

    class Node {
        int place;
        int cost, stops;
        public Node(int place, int cost, int stops) {
            this.cost = cost;
            this.place = place;
            this.stops = stops;
        }
    }
}
