package google;


import java.util.*;

// https://www.cnblogs.com/grandyang/p/10293947.html
public class BusRoutes {
    public static int numBusesToDestination(int[][] routes, int S, int T) {
        if(S == T) {
            return 0;
        }
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < routes.length; i++) {
            for (int r : routes[i]) {
                if (!map.containsKey(r)) {
                    map.put(r, new ArrayList<>());
                }
                map.get(r).add(i);
            }
        }
        Queue<Integer> queue = new LinkedList<>();
        queue.add(S);
        Set<Integer> visited = new HashSet<>();

        int result = 0;
        while (!queue.isEmpty()) {
            result++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int cur = queue.poll();
                List<Integer> buses = map.get(cur);
                if (buses != null) {
                    for (int bus : buses) {
                        if (visited.contains(bus)){
                            continue;
                        }
                        visited.add(bus);
                        for (int next : routes[bus]) {
                            if (next == T) return result;
                            queue.add(next);
                        }
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[][] routes = {{1, 2, 7}, {3, 6, 7}};
        int S = 1, T = 6;
        System.out.println(numBusesToDestination(routes, S, T));
    }
}
