package databricks;

import java.util.LinkedList;
import java.util.Queue;

public class ShortestDistanceFromAllBuildings {
    public int shortestDistance(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            return -1;
        }
        int rows = grid.length, cols = grid[0].length;
        int[][] dist = new int[rows][cols];
        int[][] count = new int[rows][cols];
        int builds = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    builds++;
                    boolean[][] visited = new boolean[rows][cols];
                    bfsFillBuilding(visited, i, j , dist, count, grid);
                }
            }
        }
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 0 && count[i][j] == builds && dist[i][j] < min) {
                    min = dist[i][j];
                }
            }
        }
        return min == Integer.MAX_VALUE ? -1 : min;
    }

    private void bfsFillBuilding(boolean[][] visited, int r, int c, int[][] distMap, int[][] countMap, int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{r, c});
        visited[r][c] = true;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        int dist = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            dist++;
            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();

                for (int[] dir : dirs) {
                    int nr = dir[0] + cur[0], nc = dir[1] + cur[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc] && grid[nr][nc] == 0) {
                        distMap[nr][nc] += dist;
                        countMap[nr][nc]++;
                        visited[nr][nc] = true;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
        }
    }

}
