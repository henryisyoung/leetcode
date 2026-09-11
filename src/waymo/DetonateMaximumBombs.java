package waymo;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/*
LeetCode 2101: Detonate the Maximum Bombs.

Given bombs[i] = [x, y, r], bomb i can directly detonate bomb j if
distance(i, j) <= r_i. A detonation can trigger more detonations.
Return the maximum number of bombs that can be detonated by choosing one
initial bomb.

Key point:
  The graph is directed. If i can reach j, it does not mean j can reach i,
  because their radii may differ.

Algorithm:
  1. Build directed graph i -> j when j is inside i's blast radius.
  2. Try each bomb as the starting bomb.
  3. DFS/BFS count how many nodes are reachable.

Use long for squared distance to avoid integer overflow:
  dx * dx + dy * dy <= r * r

Complexity:
  Build graph: O(n^2)
  DFS from every node: O(n * (n + e)), worst-case O(n^3)
  Space: O(n + e)
*/
public class DetonateMaximumBombs {

    public int maximumDetonation(int[][] bombs) {
        if (bombs == null || bombs.length == 0) return 0;

        List<List<Integer>> graph = buildGraph(bombs);
        int n = bombs.length;
        int best = 0;
        for (int i = 0; i < n; i++) {
            boolean[] visited = new boolean[n];
            best = Math.max(best, dfs(i, graph, visited));
        }
        return best;
    }

    public int maximumDetonationBfs(int[][] bombs) {
        if (bombs == null || bombs.length == 0) return 0;

        List<List<Integer>> graph = buildGraph(bombs);
        int n = bombs.length;
        int best = 0;
        for (int i = 0; i < n; i++) {
            best = Math.max(best, bfs(i, graph, n));
        }
        return best;
    }

    private List<List<Integer>> buildGraph(int[][] bombs) {
        int n = bombs.length;
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            long x1 = bombs[i][0];
            long y1 = bombs[i][1];
            long r = bombs[i][2];
            long radiusSquared = r * r;

            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                long dx = x1 - bombs[j][0];
                long dy = y1 - bombs[j][1];
                long distanceSquared = dx * dx + dy * dy;
                if (distanceSquared <= radiusSquared) {
                    graph.get(i).add(j);
                }
            }
        }
        return graph;
    }

    private int dfs(int cur, List<List<Integer>> graph, boolean[] visited) {
        if (visited[cur]) return 0;
        visited[cur] = true;

        int count = 1;
        for (int next : graph.get(cur)) {
            count += dfs(next, graph, visited);
        }
        return count;
    }

    private int bfs(int start, List<List<Integer>> graph, int n) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.offer(start);

        int count = 0;
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            count++;
            for (int next : graph.get(cur)) {
                if (visited[next]) continue;
                visited[next] = true;
                queue.offer(next);
            }
        }
        return count;
    }

    public static void main(String[] args) {
        DetonateMaximumBombs solver = new DetonateMaximumBombs();

        check("chain two",
                solver.maximumDetonation(new int[][]{{2, 1, 3}, {6, 1, 4}}),
                solver.maximumDetonationBfs(new int[][]{{2, 1, 3}, {6, 1, 4}}),
                2);

        check("separate bombs",
                solver.maximumDetonation(new int[][]{{1, 1, 5}, {10, 10, 5}}),
                solver.maximumDetonationBfs(new int[][]{{1, 1, 5}, {10, 10, 5}}),
                1);

        check("directed reachability",
                solver.maximumDetonation(new int[][]{{1, 1, 1}, {2, 1, 1}, {3, 1, 1}, {4, 1, 1}, {5, 1, 1}}),
                solver.maximumDetonationBfs(new int[][]{{1, 1, 1}, {2, 1, 1}, {3, 1, 1}, {4, 1, 1}, {5, 1, 1}}),
                5);

        check("overflow-safe distance",
                solver.maximumDetonation(new int[][]{{100000, 100000, 1}, {-100000, -100000, 1}}),
                solver.maximumDetonationBfs(new int[][]{{100000, 100000, 1}, {-100000, -100000, 1}}),
                1);

        check("empty input", solver.maximumDetonation(new int[][]{}), solver.maximumDetonationBfs(new int[][]{}), 0);

        System.out.println("All tests passed.");
    }

    private static void check(String label, int dfs, int bfs, int expected) {
        if (dfs != expected || bfs != expected) {
            throw new AssertionError(label + " expected " + expected + " but got dfs=" + dfs + ", bfs=" + bfs);
        }
        System.out.println(label + ": dfs=" + dfs + ", bfs=" + bfs);
    }
}
