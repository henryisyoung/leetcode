package rippling.VO;

public class MaxAreaIsland {
    public int maxAreaOfIsland(int[][] grid) {
        int max = 0;
        int rows = grid.length, cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    int size = dfsFindIslandSize(i, j, grid);
                    max = Math.max(size, max);
                }
            }
        }

        return max;
    }

    private int dfsFindIslandSize(int r, int c, int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] == 0) {
            return 0;
        }
        int count = 1;
        grid[r][c] = 0;
        int[][] dirs = {{1,0},{0,1},{-1,0},{0,-1}};
        for (int[] dir : dirs) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            count += dfsFindIslandSize(nr, nc, grid);
        }
        return count;
    }
}
