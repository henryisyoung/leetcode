package facebook;

import java.util.LinkedList;
import java.util.Queue;

public class IsGraphBipartite {
    public boolean isBipartite(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n];
        for(int i = 0; i < n; i++) {
            if(color[i] == 0 && !isValid(color, 1, graph, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValid(int[] color, int cur, int[][] graph, int index) {
        if(color[index] != 0) return color[index] == cur;
        color[index] = cur;
        for(int next : graph[index]) {
            if(!isValid(color, cur * -1, graph, next)) return false;
        }
        return true;
    }
    public boolean isBipartiteBfs(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n];
        Queue<Integer> queue = new LinkedList<>();

        for(int i = 0; i < n; i++) {
            if(color[i] == 0) {
                color[i] = 1;
                queue.add(i);
            }

            while(!queue.isEmpty()) {
                int cur = queue.poll();
                for(int next : graph[cur]) {
                    if(color[next] == 0) {
                        color[next] = color[cur] * -1;
                        queue.add(next);
                    } else {
                        if(color[next] == color[cur]) return false;
                    }
                }
            }
        }
        return true;
    }
}
