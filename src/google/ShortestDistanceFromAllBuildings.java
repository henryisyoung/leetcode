package google;

import java.util.Arrays;

public class ShortestDistanceFromAllBuildings {
//    Each 0 marks an empty land which you can pass by freely.
//    Each 1 marks a building which you cannot pass through.
//    Each 2 marks an obstacle which you cannot pass through.
    public int shortestDistance(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        int rows = grid.length, cols = grid[0].length;
        int[][] distMap = new int[rows][cols];
        int[][] levelMap = new int[rows][cols];
        for (int[] arr : distMap) {
            Arrays.fill(arr, Integer.MAX_VALUE);
        }
        int level = 0, sum = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    level++;
                    sum += level;
                    bfsReach(i, j, grid, distMap, levelMap, level, 0);
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 0 && levelMap[i][j] == sum) {
                    min = Math.min(min, distMap[i][j]);
                }
            }
        }
        return min == Integer.MAX_VALUE ? -1 : min;
    }

    private void bfsReach(int r, int c, int[][] grid, int[][] distMap, int[][] levelMap, int level, int dist) {
        int rows = grid.length, cols = grid[0].length;
        int[][] dirs = {{1, 0}, {-1, 0}, {0,1}, {0,-1}};
        levelMap[r][c] += level;
        distMap[r][c] = dist;
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 0 && distMap[nr][nc] > dist) {
                bfsReach(nr, nc, grid, distMap, levelMap, level, dist + 1);
            }
        }
    }

    public static void main(String[] args) {
        int[][] grid = {
                {1,0,2,0,1},
                {0,0,0,0,0},
                {0,0,1,0,0}};
        ShortestDistanceFromAllBuildings solver = new ShortestDistanceFromAllBuildings();
        System.out.println(solver.shortestDistance(grid));
    }
}
