package apple;

public class BombEnemy {
    public int maxKilledEnemies(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            return 0;
        }
        int rows = grid.length, cols = grid[0].length;

        int rowCount = 0;
        int[] colCount = new int[cols];
        int max = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j == 0 || grid[i][j - 1] == 'W') {
                    rowCount = 0;
                    for (int k = j; k < cols; k++) {
                        if (grid[i][k] == 'W') {
                            break;
                        } else if (grid[i][k] == 'E') {
                            rowCount++;
                        }
                    }
                }

                if (i == 0 || grid[i - 1][j] == 'W') {
                    colCount[j] = 0;
                    for (int k = i; k < rows; k++) {
                        if (grid[k][j] == 'W') {
                            break;
                        } else if (grid[k][j] == 'E') {
                            colCount[j]++;
                        }
                    }
                }

                if (grid[i][j] == '0') {
                    max = Math.max(max, rowCount + colCount[j]);
                }
            }
        }
        return max;
    }
}
