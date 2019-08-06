package uber;

import java.util.Map;

public class BombEnemy {
    public int maxKilledEnemies(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) return 0;
        int rows = grid.length, cols = grid[0].length;
        int[][] v1 = new int[rows][cols], v2 = new int[rows][cols], v3 = new int[rows][cols], v4 = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int em = (j == 0 || grid[i][j] == 'W') ? 0 : v1[i][j - 1];
                v1[i][j] = grid[i][j] == 'E' ? em + 1 : em;
            }
            for (int k = cols - 1; k >= 0; k--) {
                int em = (k == cols - 1 || grid[i][k] == 'W') ? 0 : v2[i][k + 1];
                v2[i][k] = grid[i][k] == 'E' ? em + 1 : em;
            }
        }
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                int em = (i == 0 || grid[i][j] == 'W') ? 0 : v3[i - 1][j];
                v3[i][j] = grid[i][j] == 'E' ? em + 1 : em;
            }
            for (int i = rows - 1; i >= 0; i--) {
                int em = (i == rows - 1 || grid[i][j] == 'W') ? 0 : v4[i + 1][j];
                v4[i][j] = grid[i][j] == 'E' ? em + 1 : em;
            }
        }
        int max = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(grid[i][j] == '0'){
                    max = Math.max(max, v1[i][j] + v2[i][j] + v3[i][j] + v4[i][j]);
                }
            }
        }
        return max;
    }

    public int maxKilledEnemies2(char[][] grid) {
        int m = grid.length;
        int n = m > 0 ? grid[0].length : 0;

        int result = 0, rows = 0;
        int[] cols = new int[n];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                if (j == 0 || grid[i][j-1] == 'W') {
                    rows = 0;
                    for (int k = j; k < n && grid[i][k] != 'W'; ++k)
                        if (grid[i][k] == 'E')
                            rows += 1;
                }
                if (i == 0 || grid[i-1][j] == 'W') {
                    cols[j] = 0;
                    for (int k = i; k < m && grid[k][j] != 'W'; ++k)
                        if (grid[k][j] == 'E')
                            cols[j] += 1;
                }

                if (grid[i][j] == '0' && rows + cols[j] > result)
                    result = rows + cols[j];
            }
        }
        return result;
    }
}
