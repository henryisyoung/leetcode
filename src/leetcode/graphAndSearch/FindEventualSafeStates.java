package leetcode.graphAndSearch;

import java.util.*;

public class FindEventualSafeStates {
    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        Map<Integer, List<Integer>> map = new HashMap<>();
        int[] toDegree = new int[n];
        for (int i = 0; i < n; i++) {
            toDegree[i] += graph[i].length;
            for (int k : graph[i]) {
                if (!map.containsKey(k)) map.put(k, new ArrayList<>());
                map.get(k).add(i);
            }
        }
        List<Integer> result = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (toDegree[i] == 0) queue.add(i);
        }
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            result.add(cur);
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    toDegree[next]--;
                    if (toDegree[next] == 0) {
                        queue.add(next);
                    }
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<Integer> eventualSafeNodesMemo(int[][] graph) {
        int n = graph.length;
        int[] memo = new int[n];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (dfsSearchAll(i, memo, graph)) {
                result.add(i);
            }
        }
        return result;
    }

    private boolean dfsSearchAll(int i, int[] memo, int[][] graph) {
        if (memo[i] > 0) {
            // 2 means todegree = 0 node
            return memo[i] == 2;
        }
        // 1 means visited
        memo[i] = 1;
        for (int next : graph[i]) {
            if (memo[next] == 2) {
                // can remove this next
                continue;
            }
            if (memo[next] == 1 || !dfsSearchAll(next, memo, graph)) {
                // next is visited but cannot removed
                return false;
            }
        }
        // all next can be removed
        memo[i] = 2;
        return true;
    }
}
