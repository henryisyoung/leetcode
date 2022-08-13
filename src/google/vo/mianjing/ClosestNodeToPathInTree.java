package google.vo.mianjing;

import java.util.*;

public class ClosestNodeToPathInTree {
    // https://leetcode.com/problems/closest-node-to-path-in-tree/
    public int[] closestNode(int n, int[][] edges, int[][] query) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            map.putIfAbsent(a, new ArrayList<>());
            map.get(a).add(b);
            map.putIfAbsent(b, new ArrayList<>());
            map.get(b).add(a);
        }
        int size = query.length;
        int[] result = new int[size];
        int index = 0;
        for (int[] q : query) {
            int start = q[0], end = q[1], node = q[2];
            Set<Integer> set = new HashSet<>();
            dfsFindPath(set, start, end, map);
            result[index++] = bfsFind(node, map, set);
        }
        return result;
    }

    private int bfsFind(int node, Map<Integer, List<Integer>> map, Set<Integer> set) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.add(node);
        visited.add(node);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int cur = queue.poll();
                if (set.contains(cur)) {
                    return cur;
                }

                if (map.containsKey(cur)) {
                    for (int next : map.get(cur)) {
                        if (visited.contains(next)) {
                            continue;
                        }
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }
        return -1;
    }

    private boolean dfsFindPath(Set<Integer> set, int cur, int end, Map<Integer, List<Integer>> map) {
        if (cur == end) {
            set.add(cur);
            return true;
        }
        set.add(cur);
        if (map.containsKey(cur)) {
            for (int next : map.get(cur)) {
                if (set.contains(next)) {
                    continue;
                }
                if (dfsFindPath(set, next, end, map)) {
                    return true;
                }
            }
        }
        set.remove(cur);
        return false;
    }
}
