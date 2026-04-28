package recovery;

public class UniquePaths3 {
    private int count;

    public int uniquePathsIII(int[][] grid) {
        int startR = 0, startC = 0;
        int endR = 0, endC = 0;
        int block = 0;
        int m = grid.length, n = grid[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == -1) {
                    block++;
                } else if (grid[i][j] == 1) {
                    startR = i;
                    startC = j;
                } else if (grid[i][j] == 2) {
                    endR = i;
                    endC = j;
                }
            }
        }

        // moves needed = (non-obstacle cells) - 1, because we're already on start.
        int stepsRemaining = m * n - block - 1;
        count = 0;
        dfsFindAll(stepsRemaining, grid, startR, startC, endR, endC);
        return count;
    }

    private void dfsFindAll(int stepsRemaining, int[][] grid, int curR, int curC, int endR, int endC) {
        if (curR == endR && curC == endC) {
            if (stepsRemaining == 0) count++;
            return;
        }

        int[][] dirs = {{1, 0}, {0, 1}, {0, -1}, {-1, 0}};

        for (int[] dir : dirs) {
            int nr = curR + dir[0], nc = curC + dir[1];
            if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length
                    && grid[nr][nc] % 2 == 0) {
                int temp = grid[curR][curC];
                grid[curR][curC] = 3;
                dfsFindAll(stepsRemaining - 1, grid, nr, nc, endR, endC);
                grid[curR][curC] = temp;
            }
        }
    }
}
