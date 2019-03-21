package airbnb;

import java.util.*;

public class CheapestFlightsWithinKStopsBFS {
    public int findCheapestPriceBFS(int n, int[][] flights, int src, int dst, int K) {
        if (flights == null || flights.length == 0 ||
                flights[0] == null || flights[0].length == 0) {
            return -1;
        }
        int ans = Integer.MAX_VALUE;

        Map<Integer, List<Place>> map = new HashMap<>();
        for (int[] flight : flights) {
            int s = flight[0], d = flight[1], c = flight[2];
            Place p = new Place(d, c);
            if (map.containsKey(s)) {
                List<Place> list = map.get(s);
                list.add(p);
                map.put(s, list);
            } else {
                List<Place> list = new ArrayList<>();
                list.add(p);
                map.put(s, list);
            }
        }

        Queue<Place> pq = new LinkedList<>();
        pq.add(new Place(src, 0));
        Set<Integer> isVisited = new HashSet<>();
        isVisited.add(src);
        int count = 0;
        while (!pq.isEmpty()) {
            int size = pq.size();
            for (int i = 0; i < size; i++) {
                Place cur = pq.poll();
                int from = cur.des;
                int fee = cur.cost;
                if (from == dst) {
                    ans = Math.min(ans, fee);
                }
                List<Place> nexts = map.get(from);
                if (nexts != null) {
                    for (Place next : nexts) {
                        if (next.cost + fee > ans) {
                            continue;
                        }
                        pq.add(new Place(next.des, next.cost + fee));
                        isVisited.add(next.des);
                    }
                }
            }
            if (count > K) {
                break;
            }
            count++;
        }
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }


    private class Place {
        int des;
        int cost;
        public Place (int des, int cost) {
            this.des = des;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return "des:" + des + " cost:" + cost;
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
