package leetcode.graphAndSearch;

import java.util.LinkedList;
import java.util.Queue;

public class NumberOfIslands {
    public int numIslandsDFS(char[][] grid) {
        int count = 0;
        if (grid == null || grid.length == 0 ||
                grid[0] == null || grid[0].length == 0) {
            return 0;
        }
        int rows = grid.length, cols = grid[0].length;
        int[][] table = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1' && table[i][j] == 0) {
                    count++;
                    dfsFillTable(table, grid, i, j);
                }
            }
        }

        return count;
    }

    private void dfsFillTable(int[][] table, char[][] grid, int r, int c) {
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        table[r][c] = 1;
        for (int[] dir : dirs) {
            int nRow = r + dir[0];
            int nCol = c + dir[1];
            if (nRow >= 0 && nRow < table.length && nCol >= 0 && nCol < table[0].length
                    && grid[nRow][nCol] == '1' && table[nRow][nCol] == 0) {
                dfsFillTable(table, grid, nRow, nCol);
            }
        }
    }

    public int numIslandsBFS(char[][] grid) {
        int count = 0;
        if (grid == null || grid.length == 0 ||
                grid[0] == null || grid[0].length == 0) {
            return 0;
        }
        int rows = grid.length, cols = grid[0].length;
        int[][] table = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1' && table[i][j] == 0) {
                    count++;
                    bfsFillTable(table, grid, i, j);
                }
            }
        }

        return count;
    }

    private void bfsFillTable(int[][] table, char[][] grid, int r, int c) {
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        Queue<int[]> queue = new LinkedList<>();
        int[] cur = {r, c};
        queue.add(cur);
        table[r][c] = 1;

        while (!queue.isEmpty()) {
            int[] node = queue.poll();
            for (int[] dir : dirs) {
                int nRow = r + dir[0];
                int nCol = c + dir[1];
                if (nRow >= 0 && nRow < table.length && nCol >= 0 && nCol < table[0].length
                        && grid[nRow][nCol] == '1' && table[nRow][nCol] == 0) {
                    int[] next = {nRow, nCol};
                    table[nRow][nCol] = 1;
                    queue.add(next);
                }
            }
        }
    }

}
