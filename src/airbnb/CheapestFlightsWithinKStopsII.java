package airbnb;

import java.util.*;

public class CheapestFlightsWithinKStopsII {
    public int findCheapestPriceBFS(int n, int[][] flights, int src, int dst, int K) {
        if (flights == null || flights.length == 0 || flights[0] == null || flights[0].length == 0) {
            return -1;
        }
        int result = Integer.MAX_VALUE;
        Map<Integer, List<Place>> map = new HashMap<>();
        for (int[] flight : flights) {
            int from = flight[0], to = flight[1], cost = flight[2];
            if (!map.containsKey(from)) {
                map.put(from, new ArrayList<Place>());
            }
            map.get(from).add(new Place(to, cost));
        }

        Queue<Place> queue = new LinkedList<>();
        boolean[] visited = new boolean[n];

        visited[src] = true;
        queue.add(new Place(src, 0));
        int count = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Place cur = queue.poll();
                int curPlace = cur.place;
                if (cur.place == dst) {
                    result = Math.min(cur.cost, result);
                }
                if (map.containsKey(curPlace)) {
                    for (Place nextPlace : map.get(curPlace)) {
                        if (visited[nextPlace.place] && nextPlace.cost > result) {
                            continue;
                        }
                        queue.add(new Place(nextPlace.place, cur.cost + nextPlace.cost));
                        visited[nextPlace.place] = true;
                    }
                }
            }
            if (count > K) {
                break;
            }
            count++;
        }

        return result == Integer.MAX_VALUE ? -1 : result;
    }

    private class Place {
        int place;
        int cost;
        public Place (int des, int cost) {
            this.place = des;
            this.cost = cost;
        }
    }

    public static void main(String[] args) {
        int n = 3, src = 0, dst = 2, K = 1;
        int[][] flights ={{0,1,100},{1,2,100},{0,2,500}};
        CheapestFlightsWithinKStopsII solver = new CheapestFlightsWithinKStopsII();
        int num = solver.findCheapestPriceBFS(n, flights, src, dst, K);
        System.out.println("num " + num);
    }
}