package apple;

public class NumberIslands {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) return 0;
        int rows = grid.length, cols = grid[0].length;
        int count = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(grid[i][j] == '1') {
                    count++;
                    searchAll(i,j,grid);
                }
            }
        }

        return count;
    }

    private void searchAll(int r, int c, char[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] != '1') return;
        grid[r][c] = '0';
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};

        for (int[] dir : dirs) {
            searchAll(r + dir[0], c + dir[1], grid);
        }
    }
}
