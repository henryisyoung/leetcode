package facebook;

import java.util.LinkedList;
import java.util.Queue;

public class IslandPerimeter {
    public int islandPerimeter(int[][] grid) {
        int count = 0;
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) return 0;
        int rows = grid.length, cols = grid[0].length;

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    queue.add(new int[]{i, j});
                    visited[i][j] = true;
                    break;
                }
            }
        }

        while (!queue.isEmpty()) {
            int cur = 4;
            int[] pos = queue.poll();
            int r = pos[0], c = pos[1];
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    if (grid[nr][nc] == 1) cur--;
                    if (!visited[nr][nc] && grid[nr][nc] == 1) {
                        queue.add(new int[]{nr, nc});
                        visited[nr][nc] = true;
                    }
                }
            }
            count += cur;
        }

        return count;
    }

    public static void main(String[] args) {
        int[][] grid = {{0,1,0,0},
                {1,1,1,0},
                {0,1,0,0},
                {1,1,0,0}};
        IslandPerimeter system = new IslandPerimeter();
        System.out.println(system.islandPerimeter(grid));
    }
}
