package uber;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Minesweeper {
    public static char[][] updateBoard(char[][] board, int[] click) {
        if (board == null || board[0] == null || board.length == 0 || board[0].length == 0) return board;
        int rows = board.length, cols = board[0].length;
        if (board[click[0]][click[1]] == 'M') {
            board[click[0]][click[1]] = 'X';
            return board;
        }
        Queue<int[]> queue = new LinkedList<>();
        queue.add(click);
        int[][] dirs = {{1,1},{1,-1},{1,0},{0,1},{0,-1},{-1,0},{-1,1},{-1,-1}};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int count = 0, r = cur[0], c = cur[1];
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc] == 'M') {
                    count++;
                }
            }
            if (count == 0) {
                board[r][c] = 'B';
                for (int[] dir : dirs) {
                    int nr = r + dir[0], nc = c + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc] == 'E'){
                        queue.add(new int[]{nr, nc});
                        board[nr][nc] = 'B';
                    }
                }
            } else {
                board[r][c] = (char) (count + '0');
            }
        }
        return board;
    }

    public static void main(String[] args) {
        char[][] baord = {{'E', 'E', 'E', 'E', 'E'},
                {'E', 'E', 'M', 'E', 'E'},
                {'E', 'E', 'E', 'E', 'E'},
                {'E', 'E', 'E', 'E', 'E'}};
        int[] click = {3, 0};
        int[] click2 = {29, 2};

        char[][] result = updateBoard(baord, click);
//        char[][] result2 = updateBoard(board1, click2);
        System.out.println(Arrays.deepToString(result));
//        System.out.println(Arrays.deepToString(result2));
    }
}
