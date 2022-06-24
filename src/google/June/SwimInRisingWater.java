package google.June;

import java.util.PriorityQueue;

public class SwimInRisingWater {
    public static int swimInWater(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) return 0;
        int min = 0;

        int rows = grid.length, cols = grid[0].length;

        PriorityQueue<Integer> pq = new PriorityQueue<>(rows * cols, (index1, index2) -> {
           int r1 = index1 / cols, r2 = index2/ cols;
           int c1 = index1 % cols, c2 = index2 % cols;
           return grid[r1][c1] - grid[r2][c2];
        });

        pq.add(0);

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        boolean[][] visited = new boolean[rows][cols];
        visited[0][0] = true;

        while (!pq.isEmpty()) {
            int index = pq.poll();
            int r = index / cols, c = index % cols;
            min = Math.max(min, grid[r][c]);

            if (r == rows - 1 && c == cols - 1) {
                return min;
            }
            for (int[] dir : dirs) {
                int nr = dir[0] + r, nc = dir[1] + c;
                if (nr >= rows || nr < 0 || nc >= cols || nc < 0 || visited[nr][nc]) continue;
                visited[nr][nc] = true;
                pq.add(nr * cols + nc);
            }
        }
        return min;
    }

    public static void main(String[] args) {
        int[][] grid = {
                {0,1,2,3,4},
                {24,23,22,21,5},
                {12,13,14,15,16},
                {11,17,18,19,20},
                {10,9,8,7,6},
        };
        System.out.println(swimInWater(grid));
    }
}
