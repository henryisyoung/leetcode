package airbnb;

import java.util.*;

public class CheapestFlightsWithinKStopsBFSPath {

    public int findCheapestPriceBFS(int n, int[][] flights, int src, int dst, int K) {
        int result = Integer.MAX_VALUE;
        Map<Integer, List<Place>> map = new HashMap<>();
        for (int[] flight : flights) {
            int from = flight[0], to = flight[1], cost = flight[2];
            if (!map.containsKey(from)) {
                map.put(from, new ArrayList<>());
            }
            map.get(from).add(new Place(to, from, cost));
        }
        Queue<Place> queue = new LinkedList<>();
        queue.add(new Place(src, src, 0));
        Map<Integer, Integer> path = new HashMap<>();

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Place cur = queue.poll();
                if (cur.place == dst) {
                    if (cur.cost < result) {
                        result = cur.cost;
                        path.put(dst, cur.fromPlace);
                    }
                }
                int curPlace = cur.place, curCost = cur.cost;
                if (map.containsKey(curPlace)) {
                    for (Place next : map.get(curPlace)) {
                        int nextPlace = next.place;
                        int newCost = curCost + next.cost;
                        if (newCost >= result) {
                            continue;
                        }
                        path.put(nextPlace, curPlace);
                        queue.add(new Place(nextPlace, curPlace, newCost));
                    }
                }
            }
            if (K < 0) {
                break;
            }
            K--;
        }
        List<Integer> list = new ArrayList<>();
        while (path.containsKey(dst)) {
            list.add(dst);
            dst = path.get(dst);
        }
        list.add(src);
        Collections.reverse(list);
        System.out.println(list.toString());
        return result == Integer.MAX_VALUE ? -1 : result;
    }
    private class Place {
        int place, cost, fromPlace;
        public Place(int place, int fromPlace, int cost) {
            this.place = place;
            this.fromPlace = fromPlace;
            this.cost = cost;
        }
    }

    public static void main(String[] args) {
        int n = 3, src = 0, dst = 2, K = 1;
        int[][] flights ={{0,1,100},{1,2,100},{0,2,500}};
        CheapestFlightsWithinKStopsBFSPath solver = new CheapestFlightsWithinKStopsBFSPath();
        int num = solver.findCheapestPriceBFS(n, flights, src, dst, K);
        System.out.println("num " + num);
    }
}
