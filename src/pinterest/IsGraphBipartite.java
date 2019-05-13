package pinterest;

import java.util.LinkedList;
import java.util.Queue;

public class IsGraphBipartite {
    public boolean isBipartite(int[][] graph) {
        int n = graph.length;
        int[] colors = new int[n];
        for (int i = 0; i < n; i++) {
            if (colors[i] == 0 && !isValidBip(graph, colors, 1, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidBip(int[][] graph, int[] colors, int color, int v) {
        if (colors[v] != 0) {
            return colors[v] == color;
        }
        colors[v] = color;
        for (int next : graph[v]) {
            if (!isValidBip(graph, colors, -1 * color, next)) {
                return false;
            }
        }
        return true;
    }

    public boolean isBipartiteBFS(int[][] graph) {
        int n = graph.length;
        int[] colors = new int[n];
        for (int i = 0; i < n; i++){
            if (colors[i] != 0) {
                continue;
            }
            colors[i] = 1;
            Queue<Integer> queue = new LinkedList<>();
            queue.add(i);
            while (!queue.isEmpty()) {
                int cur = queue.poll();
                int curColor = colors[cur];
                for (int next : graph[cur]) {
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
        return true;
    }
}
