package snowflake;

public class MaxAreaIsland {
    public int maxAreaOfIsland(int[][] grid) {
        if (grid == null || grid[0] == null) {
            return 0;
        }
        int rows = grid.length, cols = grid[0].length;
        if (rows == 0 || cols == 0) {
            return 0;
        }

        int max = 0;
        for (int  i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    max = Math.max(max, dfsFindAll(i, j, grid));
                }
            }
        }
        return max;
    }

    private int dfsFindAll(int r, int c, int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] != 1) {
            return 0;
        }
        int count = 1;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        grid[r][c] = -1;
        for (int[] dir : dirs) {
            count += dfsFindAll(r + dir[0], c + dir[1], grid);
        }
        return count;
    }
}
