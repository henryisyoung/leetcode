package datastructure.graph;

import java.util.*;

public class MinimumHeightTrees {
    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        if(n<=1) {
            return Arrays.asList(0);
        }
        Map<Integer, List<Integer>> map = new HashMap<>();
        int[] neighbors = new int[n];
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            neighbors[a]++;
            neighbors[b]++;
            map.putIfAbsent(a, new ArrayList<>());
            map.get(a).add(b);
            map.putIfAbsent(b, new ArrayList<>());
            map.get(b).add(a);
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (neighbors[i] == 1) {
                queue.add(i);
            }
        }

        while (n > 2) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int cur = queue.poll();
                n--;
                if (map.containsKey(cur)) {
                    for (int child : map.get(cur)) {
                        neighbors[child]--;
                        if (neighbors[child] == 1) {
                            queue.add(child);
                        }
                    }
                }
            }
        }
        List<Integer> list = new ArrayList<>();
        for (int i : queue) list.add(i);
        return list;
    }
}
