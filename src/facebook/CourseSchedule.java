package facebook;

import java.util.*;

public class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        if (prerequisites == null || prerequisites.length == 0) return true;
        int[] indegree = new int[numCourses];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] course : prerequisites) {
            int from = course[1], to = course[0];
            indegree[to]++;
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
        }

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
                visited.add(i);
            }
        }

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    if (visited.contains(next)) continue;
                    indegree[next]--;
                    if (indegree[next] == 0) {
                        queue.add(next);
                        visited.add(next);
                    }
                }
            }
        }

        return visited.size() == numCourses;
    }

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        if (prerequisites == null || prerequisites.length == 0) return new int[0];
        int[] indegree = new int[numCourses];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] course : prerequisites) {
            int from = course[1], to = course[0];
            indegree[to]++;
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
        }
        int count = 0 ;
        Queue<Integer> queue = new LinkedList<>();
        int[] result = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
                result[count] = i;
                count++;
            }
        }

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    indegree[next]--;
                    if (indegree[next] == 0) {
                        result[count] = next;
                        count++;
                        queue.add(next);
                    }
                }
            }
        }
        return count == numCourses ? result : new int[0];
    }
}
