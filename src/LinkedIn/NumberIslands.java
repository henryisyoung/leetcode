package LinkedIn;

public class NumberIslands {
    public int numIslands(char[][] grid) {
        int r = grid.length, c = grid[0].length;
        int cnt = 0;

        for (int i = 0; i < r; i ++) {
            for (int j = 0; j < c; j++) {
                if (grid[i][j] == '1') {
                    cnt++;
                    dfsFill(i, j, grid);
                }
            }
        }
        return cnt;
    }

    private void dfsFill(int r, int c, char[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        if (r >= rows || c >= cols || r < 0 || c < 0 || grid[r][c] != '1') return;
        grid[r][c] = '0';
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            dfsFill(nr, nc, grid);
        }
    }
}
