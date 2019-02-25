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
        boolean[] visited = new boolean[n];
        Map<Integer, List<Integer>> map = new HashMap<>();
        int[] inDegree = new int[n];
        for (int[] edge : edges) {
            if (!map.containsKey(edge[0])) {
                List<Integer> list = new ArrayList<>();
                map.put(edge[0], list);
            }
            map.get(edge[0]).add(edge[1]);
            inDegree[edge[1]]++;
        }
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                result.add(i);
                dfs(i, map, visited);
            }
        }
        //如果用HashSet去重的话，本循环要注意concurrent modification error，边循环边修改， //会不会用HashSet的iterator安全一点
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                result.add(i);
                dfs(i, map, visited);
            }
        }
        return result;
    }

    private static void dfs(int crt, Map<Integer, List<Integer>> map, boolean[] visited) {
        visited[crt] = true;
        if (map.containsKey(crt)) {
            for (int next : map.get(crt)) {
                if (visited[next]) {
                    continue;
                }
                dfs(next, map, visited);
            }
        }
    }
}
