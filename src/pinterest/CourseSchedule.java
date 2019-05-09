package pinterest;

import java.util.*;

public class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        if (prerequisites == null || prerequisites.length == 0 ||
                prerequisites[0] == null || prerequisites[0].length == 0) {
            return false;
        }
        int[] inDegree = new int[numCourses];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] arr : prerequisites) {
            inDegree[arr[0]]++;
            if (!map.containsKey(arr[1])) {
                map.put(arr[1], new ArrayList<>());
            }
            map.get(arr[1]).add(arr[0]);
        }
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
                list.add(i);
            }
        }
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    inDegree[next]--;
                    if (inDegree[next] == 0) {
                        queue.add(next);
                        list.add(next);
                    }
                }
            }
        }
        System.out.println(list.toString());
        return list.size() == numCourses;
    }

    public static void main(String[] args) {
        int[][] nums = {{1,0},{2,6},{1,7},{6,4},{7,0},{0,5}};
        int n = 8;
        CourseSchedule solver = new CourseSchedule();
        System.out.println(solver.canFinish(n, nums));
    }
}
