package snap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ShortestDistanceFromAllBuildings {
    public int shortestDistance(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            return 0;
        }
        int rows = grid.length, cols = grid[0].length;
        int[][] dist = new int[rows][cols];
        int[][] reachMap = new int[rows][cols];

        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    count++;
                    boolean[][] visited = new boolean[rows][cols];
                    bfsFillAll(i, j, 0, dist, reachMap, grid, visited);
                }
            }
        }
        System.out.println(Arrays.deepToString(dist));
        System.out.println(Arrays.deepToString(reachMap));
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 0 && reachMap[i][j] == count && dist[i][j] < min) {
                    min = dist[i][j];
                }
            }
        }
        return min == Integer.MAX_VALUE ? -1 : min;
    }

    private void bfsFillAll(int r, int c, int dist, int[][] distMap, int[][] reachMap, int[][] grid, boolean[][] visited) {
        Queue<int[]> queue = new LinkedList<>();
        int rows = grid.length, cols = grid[0].length;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        queue.add(new int[]{r, c});
        while (!queue.isEmpty()) {
            int size = queue.size();
            dist++;
            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();
                for (int[] dir : dirs) {
                    fill(cur[0] + dir[0], cur[1] + dir[1], grid, dist, reachMap, distMap, visited, queue);
                }
            }
        }
    }

    private void fill(int r, int c, int[][] grid, int dist, int[][] reachMap, int[][] distMap,
                      boolean[][] visited, Queue<int[]> queue) {
        int rows = grid.length, cols = grid[0].length;

        if (r >= rows || r < 0 || c >= cols || c < 0 || visited[r][c] || grid[r][c] != 0) return;
        visited[r][c] = true;
        reachMap[r][c]++;
        distMap[r][c] += dist;
        queue.add(new int[]{r, c});
    }

    public static void main(String[] args) {
        int[][] grid = {{1,0,2,0,1},{0,0,0,0,0},{0,0,1,0,0}};
        ShortestDistanceFromAllBuildings solver = new ShortestDistanceFromAllBuildings();
        System.out.println(solver.shortestDistance(grid));
    }
}
