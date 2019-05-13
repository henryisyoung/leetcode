package pinterest;

import java.util.*;

public class PossibleBipartition {
    public boolean possibleBipartition(int N, int[][] dislikes) {
        int[] colors = new int[N + 1];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] edge : dislikes) {
            int a = edge[0], b = edge[1];
            if (!map.containsKey(a)) {
                map.put(a, new ArrayList<>());
            }
            map.get(a).add(b);
            if (!map.containsKey(b)) {
                map.put(b, new ArrayList<>());
            }
            map.get(b).add(a);
        }
        for (int i = 0; i < N; i++){
            if (colors[i] == 0 && !isValidBip(colors, map, i, 1)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidBip(int[] colors, Map<Integer, List<Integer>> map, int v, int color) {
        if (colors[v] != 0) {
            return colors[v] == color;
        }
        colors[v] = color;
        if (map.containsKey(v)) {
            for (int next : map.get(v)) {
                if (!isValidBip(colors, map, next, -1 * color)) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean possibleBipartitionBFS(int N, int[][] dislikes) {
        int[] colors = new int[N + 1];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] edge : dislikes) {
            int a = edge[0], b = edge[1];
            if (!map.containsKey(a)) {
                map.put(a, new ArrayList<>());
            }
            map.get(a).add(b);
            if (!map.containsKey(b)) {
                map.put(b, new ArrayList<>());
            }
            map.get(b).add(a);
        }
        for (int i = 0; i < N; i++) {
            if (colors[i] != 0) {
                continue;
            }
            colors[i] = 1;
            Queue<Integer> queue = new LinkedList<>();
            queue.add(i);
            while (!queue.isEmpty()) {
                int cur = queue.poll();
                int curColor = colors[cur];
                if (map.containsKey(cur)) {
                    for (int next : map.get(cur)) {
                        if (colors[next] == curColor) {
                            return false;
                        }
                        if (colors[next] == 0) {
                            colors[next] = -1 * curColor;
                            queue.add(next);
                        }
                    }
                }
            }
        }
        return true;
    }

}
