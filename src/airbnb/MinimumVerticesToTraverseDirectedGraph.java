package airbnb;

import java.util.*;

public class MinimumVerticesToTraverseDirectedGraph {
    public static void main(String args[]) {
        int[][] edges = {{2,9},{3,3},{3,5},{3,7},{4,8},{5,8},{6,6},{7,4},{8,7},{9,3},{9,6}};
        List<Integer> result = getMin(edges, 10);
        for (int num : result) {
            System.out.println(num);
        }
    }

    public static List<Integer> getMin(int[][] edges, int n) {
        List<Integer> result = new ArrayList<>();
        if (edges == null || edges.length == 0) {
            return result;
        }
        int[] indegree = new int[n];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] edge : edges) {
            indegree[edge[1]]++;
            if (!map.containsKey(edge[0])) {
                map.put(edge[0], new ArrayList<Integer>());
            }
            map.get(edge[0]).add(edge[1]);
        }
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                result.add(i);
                dfsSearch(visited, map, i);
            }
        }
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                result.add(i);
                dfsSearch(visited, map, i);
            }
        }
        return result;
    }

    private static void dfsSearch(boolean[] visited, Map<Integer, List<Integer>> map, int i) {
        visited[i] = true;
        if (map.containsKey(i)) {
            for (int next : map.get(i)) {
                if (visited[next]) {
                    continue;
                }
                dfsSearch(visited, map, next);
            }
        }
    }
}
