package airbnb.New2026;

import java.util.Arrays;
import java.util.Random;

public class ConnectFour {
    char[][] board;
    int rows, cols;
    int left;
    int[] rowsLeft;
    int[][] dirs;
    int lastR;
    int lastC;

    public ConnectFour(int rows, int cols) {
        this.left = rows * cols;
        this.board = new char[rows][cols];
        this.rows = rows;
        this.cols = cols;
        this.rowsLeft = new int[cols];
        for (char[] arr : board) Arrays.fill(arr, '.');
        Arrays.fill(rowsLeft, rows);
        this.dirs = new int[][]{{1,0},{0,1},{0,-1}, {-1,0}};
    }

    public boolean drop(char player, int c) {
        if (rowsLeft[c] == 0) return false;
        rowsLeft[c]--;
        int r = rowsLeft[c];
        board[r][c] = player;
        lastR = r;
        lastC = c;
        left--;
        return true;
    }

    public boolean isWinning(char player) {
        for (int[] dir : dirs) {
            int count = 1 + countRun(dir[0], dir[1], lastR, lastC, player) + countRun(-dir[0], -dir[1], lastR, lastC, player);
            if (count >= 4) return true;
        }

        return false;
    }

    public boolean isFull() {
        return left == 0;
    }

    private int countRun(int dr, int dc, int r, int c, char player) {
        int count  = 0;
        int nr = r + dr, nc = c + dc;
        while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc] == player) {
            count++;
            nr += dr;
            nc += dc;
        }

        return count;
    }

    public void draw(){
        for (char[] arr : board) {
            for (char c : arr) {
                System.out.print(c + " ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");

    }

    public static void main(String[] args) {
        ConnectFour c4 = new ConnectFour(10, 10);
        Random random = new Random();
        int index = 0;

        while (!c4.isFull()) {
            int c = random.nextInt(10);
            char player = index++ % 2 == 0 ? 'e' : 'o';
            c4.drop(player, c);
            if (c4.isWinning(player)) {
                c4.draw();
                System.out.println(player + " : win at r " + c4.lastR + " c " + c4.lastC);
                break;
            }
        }

    }

}
