package airbnb;

import java.util.*;


//Time Complexity: O(E+nlogn), where E is the total number of flights.
//Space Complexity: O(n), the size of the heap.
public class CheapestFlightsWithinKStopsDijkstra {
    public int findCheapestPriceDijkstra(int n, int[][] flights, int src, int dst, int K) {
        int[][] costMap = new int[n][n];
        Map<Integer, Integer> lowestPriceMap = new HashMap<>();
        for (int[] flight : flights) {
            costMap[flight[0]][flight[1]] = flight[2];

        }

        PriorityQueue<Place> pq = new PriorityQueue<>(n, new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                return o1.cost - o2.cost;
            }
        });

        pq.add(new Place(0, 0, src));

        while (!pq.isEmpty()) {
            Place cur = pq.poll();
            int k = cur.k, cost = cur.cost, place = cur.name;
            if (k > K + 1 || cost > lowestPriceMap.getOrDefault(k * 1000 + place, Integer.MAX_VALUE)) {
                continue;
            }
            if (place == dst) {
                return cost;
            }
            for (int next = 0; next < n; next++) {
                if (costMap[place][next] > 0) {
                    int newCost = costMap[place][next] + cost;
                    if (newCost < lowestPriceMap.getOrDefault((k + 1) * 1000 + next, Integer.MAX_VALUE)) {
                        pq.offer(new Place(newCost, k + 1, next));
                        lowestPriceMap.put((k + 1) * 1000 + next, newCost);
                    }
                }
            }
        }

        return -1;
    }

    private class Place{
        int cost, name, k;
        public Place(int cost, int k , int name) {
            this.cost = cost;
            this.k = k;
            this.name = name;
        }
    }

    public static void main(String[] args) {
        int n = 3, src = 0, dst = 2, K = 1;
        int[][] flights ={{0,1,100},{1,2,100},{0,2,500}};
        CheapestFlightsWithinKStopsDijkstra solver = new CheapestFlightsWithinKStopsDijkstra();
        int num = solver.findCheapestPriceDijkstra(n, flights, src, dst, K);
        System.out.println("num " + num);
    }
}
