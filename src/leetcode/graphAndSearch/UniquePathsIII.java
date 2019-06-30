package leetcode.graphAndSearch;

public class UniquePathsIII {
    int count = 0;
    public int uniquePathsIII(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        int rows = grid.length, cols = grid[0].length;
        int sr = 0, sc = 0, er = 0, ec = 0, obs = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    sr = i;
                    sc = j;
                } else if (grid[i][j] == 2) {
                    er = i;
                    ec = j;
                } else if (grid[i][j] == -1) {
                    obs++;
                }
            }
        }
        dfsSearchAll(er, ec, sr, sc, grid, 0, obs);
        return count;
    }

    private void dfsSearchAll(int er, int ec, int r, int c, int[][] grid, int steps, int obs) {
        int rows = grid.length, cols = grid[0].length, total = rows * cols - 1 - obs;
        if (r == er && c == ec && steps == total) {
            count++;
            return;
        }
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] % 2 == 0) {
                int temp = grid[nr][nc];
                grid[nr][nc] = 3;
                dfsSearchAll(er, ec, nr, nc, grid, steps + 1, obs);
                grid[nr][nc] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[][] grid = {{1,0,0,0},{0,0,0,0},{0,0,2,-1}};
        UniquePathsIII solver = new UniquePathsIII();
        System.out.println(solver.uniquePathsIII(grid));
    }
}
