package snap;

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
                    bfsSearchAll(visited, grid, i, j, dist, reachMap);
                }
            }
        }
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 0 && reachMap[i][j] == count && min > dist[i][j]) {
                    min = dist[i][j];
                }
            }
        }
        return min;
    }

    private void bfsSearchAll(boolean[][] visited, int[][] grid, int r, int c, int[][] distMap, int[][] reachMap) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{r, c});
        visited[r][c] = true;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        int dist = 0;
        while (!queue.isEmpty()) {
            dist++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();
                for (int[] dir : dirs) {
                    int nr = cur[0] + dir[0], nc = cur[1] + dir[1];
                    fillNext(visited, grid, distMap, reachMap, nr, nc, queue, dist);
                }
            }
        }
    }

    private void fillNext(boolean[][] visited, int[][] grid, int[][] distMap,
                          int[][] reachMap, int nr, int nc, Queue<int[]> queue, int dist) {
        int rows = grid.length, cols = grid[0].length;
        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || visited[nr][nc] || grid[nr][nc] != 0) return;

        visited[nr][nc] = true;
        distMap[nr][nc] += dist;
        reachMap[nr][nc]++;
        queue.add(new int[]{nr, nc});
    }

    public static void main(String[] args) {
        int[][] grid = {{1,0,2,0,1},{0,0,0,0,0},{0,0,1,0,0}};
        ShortestDistanceFromAllBuildings solver = new ShortestDistanceFromAllBuildings();
        System.out.println(solver.shortestDistance(grid));
    }
}
