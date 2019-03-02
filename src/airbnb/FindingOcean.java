package airbnb;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class FindingOcean {
    public char[][] floodFillDFS(char[][] board, int i, int j, char oldColor, char newColor) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) {
            return null;
        }
        dfsFill(board, i, j, oldColor, newColor);
        return board;
    }

    private void dfsFill(char[][] board, int i, int j, char oldColor, char newColor) {
        int rows = board.length, cols = board[0].length;
        if (i < 0 || i >= rows || j < 0 || j >= cols) {
            return;
        }
        if (board[i][j] == 'L' || board[i][j] == newColor) {
            return;
        }
        if (board[i][j] == oldColor) {
            board[i][j] = newColor;
        }
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nRow = i + dir[0], nCol = j + dir[1];
            dfsFill(board, nRow, nCol, oldColor, newColor);
        }
    }

    public char[][] floodFillBFS(char[][] board, int i, int j, char oldColor, char newColor) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) {
            return board;
        }
        int rows = board.length, cols = board[0].length;
        if (board[i][j] == 'L') {
            return board;
        }
        Queue<int[]> pq = new LinkedList<>();
        int[] start = {i , j};
        pq.add(start);
        board[i][j] = newColor;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!pq.isEmpty()) {
            int size = pq.size();
            for (int s = 0; s < size; s++) {
                int[] cur = pq.poll();
                int r = cur[0], c = cur[1];
                for (int[] dir : dirs) {
                    int nRow = r + dir[0], nCol = c + dir[1];
                    if (nRow >= 0 && nRow < rows && nCol >= 0 && nCol < cols && board[nRow][nCol] == oldColor) {
                        int[] next = {nRow, nCol};
                        pq.add(next);
                        board[nRow][nCol] = newColor;
                    }
                }
            }
        }
        return board;
    }

    public static void main(String[] args) {
        String[] s1 = {"WWWLLLW", "WWLLLWW", "WLLLLWW"};
        char[][] board = new char[3][7];
        for (int i = 0; i < 3; i++) {
            String s = s1[i];
            for (int j = 0; j < 7; j++) {
                board[i][j] = s.charAt(j);
            }
        }
        int i = 1, j = 3;
        FindingOcean solver = new FindingOcean();
        char[][] arr = solver.floodFillDFS(board, i, j, 'W', 'O');
        char[][] arr2 = solver.floodFillBFS(board, i, j, 'W', 'O');
        for (char[] level : arr2) {
            System.out.println(Arrays.toString(level));
        }
    }
}
