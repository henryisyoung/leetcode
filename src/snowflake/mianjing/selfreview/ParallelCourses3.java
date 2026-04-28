package snowflake.mianjing.selfreview;

import java.util.*;

public class ParallelCourses3 {
    public int minimumTime(int n, int[][] relations, int[] time) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        int[] indegree = new int[n + 1];
        int[] finish = new int[n + 1];
        int max = 0;

        for (int[] rela : relations) {
            int from = rela[0], to = rela[1];
            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);
            indegree[to]++;
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 1; i <= n; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
                finish[i] = time[i - 1];
                max = Math.max(finish[i], max);
            }
        }

        while (!queue.isEmpty()) {
            int cur =  queue.poll();
            if (graph.containsKey(cur)) {
                for (int next : graph.get(cur)) {
                    indegree[next]--;
                    finish[next] = Math.max(finish[next], finish[cur] + time[next - 1]);
                    max = Math.max(max, finish[next]);
                    if (indegree[next] == 0) {
                        queue.add(next);
                    }
                }
            }
        }

        return max;
    }
}
