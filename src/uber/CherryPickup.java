package uber;

import java.util.Arrays;

public class CherryPickup {
    public int cherryPickup(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][][] memo = new int[rows][cols][rows];
        for (int i = 0;i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Arrays.fill(memo[i][j], Integer.MIN_VALUE);
            }
        }
        return Math.max(0, memoSearchAll(grid, memo, rows - 1, cols - 1, rows - 1));
    }

    private int memoSearchAll(int[][] grid, int[][][] memo, int r1, int c1, int r2) {
        int c2 = r1 + c1 - r2;
        if (r1 < 0 || r2 < 0 || c1 < 0 || c2 < 0) return -1;
        if (grid[r1][c1] < 0 || grid[r2][c2] < 0) return -1;
        if (r1 == 0 && c1 == 0) return grid[0][0];
        if (memo[r1][c1][r2] != Integer.MIN_VALUE) return memo[r1][c1][r2];
        memo[r1][c1][r2] = Math.max(
                Math.max(memoSearchAll(grid, memo, r1 - 1, c1, r2 - 1), memoSearchAll(grid, memo, r1, c1 - 1, r2)),
                Math.max(memoSearchAll(grid, memo, r1, c1 - 1, r2 - 1), memoSearchAll(grid, memo, r1 - 1, c1, r2)));
        if (memo[r1][c1][r2] >= 0) {
            memo[r1][c1][r2] += grid[r1][c1];
            if (r1 != r2) memo[r1][c1][r2] += grid[r2][c2];
        }
        return memo[r1][c1][r2];
    }

    public static void main(String[] args) {
        CherryPickup solver = new CherryPickup();
        int[][] grid = {{0, 1, -1},
            {1, 0, -1},
            {1, 1,  1}};
        System.out.println(solver.cherryPickup(grid));
    }
}
