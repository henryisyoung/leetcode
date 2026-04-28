package snowflake;

import java.util.*;

public class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        int[] inDegree = new int[numCourses];

        int count = 0;
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int[] pair : prerequisites) {
            int first = pair[1], second = pair[0];
            inDegree[second]++;
            graph.putIfAbsent(first, new ArrayList<>());
            graph.get(first).add(second);
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0 ; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                count++;
                queue.add(i);
            }
        }

        while (!queue.isEmpty()) {
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

        return count == numCourses;
    }
}
