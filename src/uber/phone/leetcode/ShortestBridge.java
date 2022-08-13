package uber.phone.leetcode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ShortestBridge {
    public int shortestBridge(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int color = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1 && !visited[i][j]) {
                    dfsFill(color, grid, i, j, visited);
                    color++;
                }
            }
        }
        Queue<int[]> queue = new LinkedList<>();
        Set<Integer> target = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = i * cols + j;
                if (grid[i][j] == 1) {
                    queue.add(new int[]{i, j, 0});
                } else if (grid[i][j] == 2) {
                    target.add(index);
                }
            }
        }
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1], dist = cur[2];
            int index = r * cols + c;
            
            if (target.contains(index)) {
                return dist - 1;
            }
            
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || grid[nr][nc] == 1) {
                    continue;
                }
                grid[nr][nc] = 1;
                queue.add(new int[]{nr, nc, dist + 1});
            }
        }
        return -1;
    }

    private void dfsFill(int color, int[][] grid, int r, int c, boolean[][] visited) {
        int rows = grid.length, cols = grid[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || visited[r][c] || grid[r][c] != 1) {
            return;
        }
        visited[r][c] = true;
        grid[r][c] = color;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};

        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            dfsFill(color, grid, nr, nc, visited);
        }
    }
}
