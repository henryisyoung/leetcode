package contest;

import java.util.Arrays;

public class DetectCycles2DGrid {
    public boolean containsCycle(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) return false;
        int rows = grid.length, cols = grid[0].length;
        if (rows < 2 || cols < 2 ) return false;
        boolean[][] visited = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(!visited[i][j] && findCycle(i, j, -1,-1,visited,grid , grid[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findCycle(int curX, int curY, int lastX, int lastY,  boolean[][] vis, char[][] grid, char startChar) {
        vis[curX][curY] = true;

        int[] DIR_X = {1, -1, 0, 0};
        int[] DIR_Y = {0, 0, 1, -1};
        int n = grid.length, m = grid[0].length;
        // Visit all directions
        for (int i = 0; i < 4; ++i) {
            int newX = curX + DIR_X[i];
            int newY = curY + DIR_Y[i];
            // Valid point?
            if (newX >= 0 && newX < n && newY >= 0 && newY < m) {
                // Don't visit last visited point
                if (!(newX == lastX && newY == lastY)) {
                    // Only visit nodes that equal start character
                    if (grid[newX][newY] == startChar) {
                        if (vis[newX][newY]) {
                            // Still visited? There is a cycle.
                            return true;
                        } else {
                            if (findCycle(newX, newY, curX, curY, vis, grid, startChar)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
    }

}
