package uber.phone.leetcode;

import java.util.*;

public class BusRoutes {
    public int numBusesToDestination(int[][] routes, int source, int target) {
        if (source == target) {
            return 0;
        }
        Map<Integer, List<Integer>> map = new HashMap<>();

        for (int bus = 0; bus < routes.length; bus++) {
            int[] route = routes[bus];
            for (int r : route) {
                map.putIfAbsent(r, new ArrayList<>());
                map.get(r).add(bus);
            }
        }
        int dist = 0;
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        Set<Integer> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int cur = queue.poll();
                if (cur == target) {
                    return dist;
                }
                if (map.containsKey(cur)) {
                    for (int nextBus : map.get(cur)) {
                        if (visited.contains(nextBus)) {
                            continue;
                        }
                        visited.add(nextBus);
                        int[] route = routes[nextBus];
                        for (int nextRoute : route) {
                            queue.add(nextRoute);
                        }
                    }
                }
            }
            dist++;
        }

        return -1;
    }
}
