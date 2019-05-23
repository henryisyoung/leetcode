package twitter;

import java.util.*;

public class ShortestDistanceFromAllBuildings {
    private int[][] dir = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    public int shortestDistance(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;

        int[][] reach = new int[m][n];
        int[][] distance = new int[m][n];
        int numBuildings = 0;
        Queue<Integer> queue = new LinkedList<>();

        // Find the minimum distance from all buildings
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    boolean[][] visited = new boolean[m][n];
                    shortestDistanceHelper(i, j, 0, grid, reach, distance, visited, queue);
                    numBuildings++;
                }
            }
        }

        // step 2: check the min distance reachable by all buildings
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0 && reach[i][j] == numBuildings && distance[i][j] < minDistance) {
                    minDistance = distance[i][j];
                }
            }
        }

        if (minDistance == Integer.MAX_VALUE) {
            return -1;
        } else {
            return minDistance;
        }
    }

    private void shortestDistanceHelper(int row, int col, int dist, int[][] grid,
                                        int[][] reach, int[][] minDistance,
                                        boolean[][] visited, Queue<Integer> queue) {
        fill(row, col, dist, grid, reach, minDistance, visited, queue);

        int n = grid[0].length;

        while (!queue.isEmpty()) {
            dist++;
            int sz = queue.size();
            for (int j = 0; j < sz; j++) {
                int cord = queue.poll();
                int x = cord / n;
                int y = cord % n;
                for (int i = 0; i < 4; i++) {
                    fill(dir[i][0] + x, dir[i][1] + y, dist, grid, reach, minDistance, visited, queue);
                }
            }
        }
    }

    private void fill(int row, int col, int dist, int[][] grid, int[][] reach, int[][] minDistance,
                      boolean[][] visited, Queue<Integer> queue) {
        int m = grid.length;
        int n = grid[0].length;

        if (row < 0 || row >= m || col < 0 || col >= n || visited[row][col] || grid[row][col] == 2) {
            return;
        }

        // We need to handle the starting building separately
        if (dist != 0 && grid[row][col] == 1) {
            return;
        }

        visited[row][col] = true;
        minDistance[row][col] += dist;
        reach[row][col] += 1;

        queue.offer(row * n + col);

    }
}
