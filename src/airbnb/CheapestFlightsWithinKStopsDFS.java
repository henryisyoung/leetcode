package airbnb;

import java.util.*;

public class CheapestFlightsWithinKStopsDFS {
    int result = Integer.MAX_VALUE;
    public int findCheapestPriceDFS(int n, int[][] flights, int src, int dst, int K) {
        if (flights == null || flights.length == 0 || flights[0] == null || flights[0].length == 0) {
            return -1;
        }
        Map<Integer, List<Place>> map = new HashMap<>();
        for (int[] flight : flights) {
            int from = flight[0], to = flight[1], cost = flight[2];
            if (!map.containsKey(from)) {
                map.put(from, new ArrayList<>());
            }
            map.get(from).add(new Place(to, cost));
        }
        boolean[] visited = new boolean[n];
        dfsSearch(src, dst, K, 0, map, visited);
        return result == Integer.MAX_VALUE ? -1 : result;
    }

    private void dfsSearch(int src, int dst, int k, int sum, Map<Integer, List<Place>> map, boolean[] visited) {
        if (src == dst) {
            result = Math.min(result, sum);
            return;
        }
        if (k < 0) {
            return;
        }
        if (map.containsKey(src)) {
            for (Place place : map.get(src)) {
                int dest = place.dest, cost = place.cost;
                if (visited[dest] || sum + cost > result) {
                    continue;
                }
                visited[dest] = true;
                dfsSearch(dest, dst, k - 1, sum + cost, map, visited);
                visited[dest] = false;
            }
        }
    }

    private class Place {
        int dest, cost;
        public Place (int dest, int cost) {
            this.dest = dest;
            this.cost = cost;
        }
    }

    public static void main(String[] args) {
        int n = 3, src = 0, dst = 2, K = 1;
        int[][] flights ={{0,1,100},{1,2,100},{0,2,500}};
        CheapestFlightsWithinKStopsDFS solver = new CheapestFlightsWithinKStopsDFS();
        int num = solver.findCheapestPriceDFS(n, flights, src, dst, K);
        System.out.println("num " + num);
    }
}
