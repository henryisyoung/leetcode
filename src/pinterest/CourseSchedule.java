package pinterest;

import java.util.*;

public class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        if (prerequisites == null || prerequisites.length == 0) return false;
        int[] inDegree = new int[numCourses];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] edge : prerequisites) {
            inDegree[edge[1]]++;
            if (!map.containsKey(edge[0])) map.put(edge[0], new ArrayList<>());
            map.get(edge[0]).add(edge[1]);
        }
        Queue<Integer> queue = new LinkedList<>();
        int count = 0;
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.add(i);
        }

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            count++;
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    inDegree[next]--;
                    if (inDegree[next] == 0) {
                        queue.add(next);
                    }
                }
            }
        }
        return count == numCourses;
    }

    public static void main(String[] args) {
        int[][] nums = {{1,0},{2,6},{1,7},{6,4},{7,0},{0,5}};
        int n = 8;
        CourseSchedule solver = new CourseSchedule();
        System.out.println(solver.canFinish(n, nums));
    }
}
