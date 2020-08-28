package apple;

import java.util.*;

public class PossibleBipartition {
    public boolean possibleBipartition(int N, int[][] dislikes) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] pair : dislikes) {
            int a = pair[0], b = pair[1];
            map.putIfAbsent(a, new ArrayList<>());
            map.get(a).add(b);
            map.putIfAbsent(b, new ArrayList<>());
            map.get(b).add(a);
        }

        int[] color = new int[N + 1];

        for (int i = 1; i <= N; i++) {
            if (color[i] == 0) {
                Queue<Integer> queue = new LinkedList<>();
                queue.add(i);
                color[i] = 1;
                while (!queue.isEmpty()) {
                    int cur = queue.poll();
                    if (map.containsKey(cur)) {
                        for (int child : map.get(cur)) {
                            if (color[child] == color[cur]) {
                                return false;
                            }
                            if (color[child] == 0) {
                                color[child] = -1 * color[cur];
                                queue.add(child);
                            }
                        }
                    }

                }
            }
        }
        return true;
    }
}
