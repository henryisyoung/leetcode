package datastructure.graph;

import java.util.*;

public class CourseScheduleII {
    public int[] findOrder(int n, int[][] prerequisites) {
        List<Integer> list = new ArrayList<>();
        Map<Integer, List<Integer>> map = new HashMap<>();
        int[] indegree = new int[n];

        for (int[] pair : prerequisites) {
            int from = pair[1], to = pair[0];
            indegree[to]++;
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if(indegree[i] == 0) {
                queue.add(i);
                list.add(i);
            }
        }
        while (!queue.isEmpty()) {
            int cur = queue.poll();

            if (map.containsKey(cur)) {
                for (int child : map.get(cur)) {
                    indegree[child]--;
                    if (indegree[child] == 0) {
                        queue.add(child);
                        list.add(child);
                    }
                }
            }
        }
        if (list.size() != n) return new int[0];
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = list.get(i);
        return result;
    }
}
