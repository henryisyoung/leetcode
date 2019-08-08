package pinterest;

import java.util.*;

public class CourseScheduleII {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<Integer> result = new ArrayList<>();
        int len = prerequisites.length;
        if(len == 0){
            int[] res = new int[numCourses];
            for(int m=0; m<numCourses; m++){
                res[m]=m;
            }
            return res;
        }
        if(numCourses == 0){
            return new int[numCourses];
        }
        int[] inDegree = new int[numCourses];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] edge : prerequisites) {
            inDegree[edge[0]]++;
            map.putIfAbsent(edge[1], new ArrayList<>());
            map.get(edge[1]).add(edge[0]);
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                result.add(i);
                queue.add(i);
            }
        }
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    inDegree[next]--;
                    if (inDegree[next] == 0) {
                        queue.add(next);
                        result.add(next);
                    }
                }
            }
        }
        if (result.size() != numCourses) return new int[0];
        int[] arr = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            arr[i] = result.get(i);
        }
        return arr;
    }
}
