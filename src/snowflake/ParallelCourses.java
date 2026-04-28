package snowflake;

import java.util.*;

public class ParallelCourses {
    public int minimumSemesters(int n, int[][] relations) {
        int count  = 0;
        int level = 0;
        int[] inDegree = new int[n + 1];
        Map<Integer, Set<Integer>> graph = new HashMap<>();

        for (int[] rela : relations) {
            int from = rela[0], to = rela[1];
            inDegree[to]++;
            graph.putIfAbsent(from, new HashSet<>());
            graph.get(from).add(to);
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 1; i <= n; i++) {
            if (inDegree[i] == 0) {
                count++;
                queue.add(i);
            }
        }

        while (!queue.isEmpty()) {
            int size = queue.size();
            level++;
            for (int i = 0; i < size; i++) {
                int cur = queue.poll();
                if (graph.containsKey(cur)) {
                    for (int next : graph.get(cur)) {
                        inDegree[next]--;
                        if (inDegree[next] == 0) {
                            queue.add(next);
                            count++;
                        }
                    }
                }
            }
        }

        return count == n ? level : -1;
    }
}
