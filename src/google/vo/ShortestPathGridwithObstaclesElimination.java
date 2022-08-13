package google.vo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ShortestPathGridwithObstaclesElimination {
    public static int shortestPath(int[][] grid, int k) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            return 0;
        }
        int steps = 0;
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0, 0});
        int[][] minObs = new int[rows][cols];
        for (int[] arr : minObs) {
            Arrays.fill(arr, Integer.MAX_VALUE);
        }
        minObs[0][0] = 0;

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};

        while (!queue.isEmpty()) {
            int size = queue.size();
            while (size-- > 0) {
                int[] cur = queue.poll();
                int r = cur[0], c = cur[1], ob = cur[2];
                if (r == rows - 1 && c == cols - 1) {
                    return steps;
                }
                for (int[] dir : dirs) {
                    int nr = r + dir[0], nc = c + dir[1];
                    if (nr >= rows || nc >= cols || nr < 0 || nc < 0) {
                        continue;
                    }
                    int nObs = ob + grid[nr][nc];
                    if (nObs > k || nObs >= minObs[nr][nc]) {
                        continue;
                    }
                    minObs[nr][nc] = nObs;
                    queue.add(new int[]{nr, nc, nObs});
                }
            }
            steps++;
        }

        return -1;
    }

    public static void main(String[] args) {
        int[][] grid = {{0,0,0},{1,1,0},{0,0,0},{0,1,1},{0,0,0}};
        int k = 1;
        System.out.println(shortestPath(grid, k));
    }
}
