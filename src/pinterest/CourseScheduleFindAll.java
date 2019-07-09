package pinterest;

import java.util.*;

public class CourseScheduleFindAll {
    public List<List<Integer>> canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> result  = new ArrayList<>();
        if (prerequisites == null || prerequisites.length == 0 ||
                prerequisites[0] == null || prerequisites[0].length == 0) {
            return result;
        }
        int[] inDegree = new int[numCourses];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] edge : prerequisites) {
            inDegree[edge[1]]++;
            if (!map.containsKey(edge[0])) map.put(edge[0], new ArrayList<>());
            map.get(edge[0]).add(edge[1]);
        }
        boolean[] visited = new boolean[numCourses];
        dfsSearchAll(result, new ArrayList<>(), visited, inDegree, numCourses, map);
        return result;
    }

    private void dfsSearchAll(List<List<Integer>> result, ArrayList<Integer> list, boolean[] visited, int[] inDegree,
                              int numCourses, Map<Integer, List<Integer>> map) {
        if (list.size() == numCourses) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = 0; i < numCourses; i++) {
            if (!visited[i] && inDegree[i] == 0) {
                list.add(i);
                visited[i] = true;
                if (map.containsKey(i)) {
                    for (int next : map.get(i)) {
                        inDegree[next]--;
                    }
                }
                dfsSearchAll(result, list, visited, inDegree, numCourses, map);
                list.remove(list.size() - 1);
                visited[i] = false;
                if (map.containsKey(i)) {
                    for (int next : map.get(i)) {
                        inDegree[next]++;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int[][] nums = {{0,1},{0,3},{1,2},{3,2}};
//        int[][] nums = {{1,0},{2,6},{1,7},{6,4},{7,0},{0,5}};
        int n = 4;
        CourseScheduleFindAll solver = new CourseScheduleFindAll();
        System.out.println(solver.canFinish(n, nums).toString());
    }
}
