package snowflake.mianjing;

import java.util.*;

public class CourseSchedule2 {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        int count = 0;
        int[] result = new int[numCourses];
        int[] inDegree = new int[numCourses];
        Map<Integer, List<Integer>> graph = new HashMap<>();

        for (int[] pair : prerequisites) {
            int first = pair[1], second = pair[0];

            inDegree[second]++;
            graph.putIfAbsent(first, new ArrayList<>());
            graph.get(first).add(second);
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
                result[count++] = i;
            }
        }

        while (!queue.isEmpty()) {
            int cur = queue.poll();

            if (graph.containsKey(cur)) {
                for (int next : graph.get(cur)) {
                    inDegree[next]--;
                    if (inDegree[next] == 0) {
                        queue.add(next);
                        result[count++] = next;
                    }
                }
            }
        }

        return count == numCourses ? result : new int[0];
    }
}
