package google.vo;

import java.util.*;

public class TimeNeededInformAllEmployees {
    public int numOfMinutes(int n, int headID, int[] manager, int[] informTime) {
        Map<Integer, Set<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.putIfAbsent(manager[i], new HashSet<>());
            graph.get(manager[i]).add(i);
        }
        Queue<int[]> queue = new ArrayDeque<>();
        // the first value is the id, and the second is the time to reach the children of id
        queue.offer(new int[]{-1, 0});
        int max = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            while (size-- > 0) {
                int[] cur = queue.poll();
                max = Math.max(max, cur[1]);
                if (!graph.containsKey(cur[0])) continue;
                for (int next : graph.get(cur[0])) {
                    queue.offer(new int[]{next, cur[1] + informTime[next]});
                }
            }
        }
        return max;
    }
}
