package leetcode.graphAndSearch;

import java.util.LinkedList;
import java.util.Queue;

public class SurroundedRegions {
    public void solve(char[][] board) {
        if (board == null || board.length == 0 ||
                board[0] == null || board[0].length == 0) {
            return;
        }
        int rows = board.length, cols = board[0].length;
        int[][] visited = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            bfsFill(visited, i, 0, board);
            bfsFill(visited, i, cols - 1, board);
        }

        for (int i = 1; i < cols - 1; i++) {
            bfsFill(visited,0, i, board);
            bfsFill(visited,rows - 1, i, board);
        }

        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                if (board[i][j] == 'O' && visited[i][j] == 0) {
                    board[i][j] = 'X';
                }
            }
        }
    }

    private void bfsFill(int[][] visited, int r, int c, char[][] board) {
        if (board[r][c] == 'X') {
            return;
        }
        int rows = board.length, cols = board[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int[] cur = new int[2];
        cur[0] = r;
        cur[1] = c;
        queue.add(cur);
        visited[r][c] = 1;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] node = queue.poll();
                for (int[] dir : dirs) {
                    int nRow = node[0] + dir[0];
                    int nCol = node[1] + dir[1];
                    if (nRow >= 0 && nRow < rows && nCol >= 0 && nCol < cols && visited[nRow][nCol] == 0 &&
                            board[nRow][nCol] == 'O') {
                        int[] next = new int[2];
                        next[0] = nRow;
                        next[1] = nCol;
                        queue.add(next);
                        visited[nRow][nCol] = 1;
                    }
                }
            }

        }
    }
}
