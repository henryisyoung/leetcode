package roblox.onsite;

import java.util.*;

public class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        int[] indegree = new int[numCourses];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] pair : prerequisites) {
            int from = pair[1], to = pair[0];
            indegree[to]++;
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
        }
        Queue<Integer> queue = new LinkedList<>();
        int count = 0;
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                count++;
                queue.add(i);
            }
        }
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    indegree[next]--;
                    if (indegree[next] == 0) {
                        queue.add(next);
                        count++;
                    }
                }
            }
        }

        return count == numCourses;
    }

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        int[] indegree = new int[numCourses];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] pair : prerequisites) {
            int from = pair[1], to = pair[0];
            indegree[to]++;
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
        }
        Queue<Integer> queue = new LinkedList<>();
        int count = 0;
        int[] result = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                result[count++] = i;
                queue.add(i);
            }
        }
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    indegree[next]--;
                    if (indegree[next] == 0) {
                        queue.add(next);
                        result[count++] = next;
                    }
                }
            }
        }

        return count == numCourses ? result : new int[0];
    }
}
