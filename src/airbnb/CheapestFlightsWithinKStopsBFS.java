package airbnb;

import java.util.*;

public class CheapestFlightsWithinKStopsBFS {
    public int findCheapestPriceBFS(int n, int[][] flights, int src, int dst, int K) {
        int result = Integer.MAX_VALUE;
        if (flights == null || flights.length == 0) {
            return -1;
        }

        Map<Integer, List<Place>> map = new HashMap<>();
        for (int[] flight : flights) {
            int from = flight[0], to = flight[1], cost = flight[2];
            if (!map.containsKey(from)) {
                map.put(from, new ArrayList<Place>());
            }
            map.get(from).add(new Place(to, cost));
        }

        Queue<Place> queue = new LinkedList<>();
        queue.add(new Place(src, 0));
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Place cur = queue.poll();
                if (cur.dest == dst) {
                    result = Math.min(result, cur.cost);
                }
                if (map.containsKey(cur.dest)) {
                    for (Place next : map.get(cur.dest)) {
                        if (cur.cost + next.cost > result) {
                            continue;
                        }
                        queue.add(new Place(next.dest, cur.cost + next.cost));
                    }
                }
            }
            if (K < 0) {
                break;
            }
            K--;
        }
        return result == Integer.MAX_VALUE ? -1 : result;
    }


    private class Place {
        int dest;
        int cost;
        public Place (int des, int cost) {
            this.dest = des;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return "des:" + dest + " cost:" + cost;
        }
    }

    public static void main(String[] args) {
        int n = 3, src = 0, dst = 2, K = 1;
        int[][] flights ={{0,1,100},{1,2,100},{0,2,500}};
        CheapestFlightsWithinKStopsBFS solver = new CheapestFlightsWithinKStopsBFS();
        int num = solver.findCheapestPriceBFS(n, flights, src, dst, K);
        System.out.println("num " + num);
    }
}
