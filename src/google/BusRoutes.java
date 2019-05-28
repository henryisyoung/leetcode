package google;


import java.util.*;

// https://www.cnblogs.com/grandyang/p/10293947.html
public class BusRoutes {
    public int numBusesToDestination(int[][] routes, int S, int T) {
        if (S == T) {
            return 0;
        }
        int result = 0;
        Set<Integer> visited = new HashSet<>();
        Map<Integer, List<Integer>> stop2bus = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(S);
        for (int i = 0; i < routes.length; ++i) {
            for (int j : routes[i]) {
                if (!stop2bus.containsKey(i)) {
                    stop2bus.put(i, new ArrayList<>());
                }
                stop2bus.get(i).add(j);
            }
        }
        while (!queue.isEmpty()) {
            ++result;
            for (int i = queue.size(); i > 0; --i) {
                int t = queue.poll();
                for (int bus : stop2bus.get(t)) {
                    if (visited.contains(bus)) {
                        continue;
                    }
                    visited.add(bus);
                    for (int stop : routes[bus]) {
                        if (stop == T) return result;
                        queue.add(stop);
                    }
                }
            }
        }
        return -1;
    }
}
