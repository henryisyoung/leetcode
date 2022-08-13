package roblox.onsite;

import java.util.Arrays;
import java.util.Random;

public class ConnectFour {
    // https://medium.com/analytics-vidhya/artificial-intelligence-at-play-connect-four-minimax-algorithm-explained-3b5fc32e4a4f
    char[][] board;
    int available;
    char emptyCell;
    int rows, cols;
    int lastR, lastC;
    public ConnectFour(int rows, int cols) {
        this.emptyCell = '#';
        this.board = new char[rows][cols];
        for (char[] chars : board) {
            Arrays.fill(chars, emptyCell);
        }
        available = rows * cols;
        this.cols = cols;
        this.rows = rows;
        this.lastC = -1;
        this.lastR = -1;
    }

    public boolean drop(int c, char player) {
        if (board[0][c] != emptyCell) {
            return false;
        }
        int r = rows - 1;
        while (r >= 0) {
            if (board[r][c] == emptyCell) {
                board[r][c] = player;
                available--;
                this.lastC = c;
                this.lastR = r;
                return true;
            }
            r--;
        }
        return false;
    }

    public boolean isWin(int r, int c, char player) {
        String rowCells = new String(board[r]);
        StringBuilder colCells = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            colCells.append(board[i][c]);
        }
        StringBuilder pattern = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pattern.append(player);
        }
        return rowCells.indexOf(pattern.toString()) != -1 || colCells.indexOf(pattern.toString()) != -1;
    }

    public boolean isFull() {
        return available == 0;
    }

    public void print() {
        for (char[] chars : board) {
            StringBuilder sb = new StringBuilder();
            String prefix = " ";
            for (char c : chars) {
                sb.append(prefix + c);
                prefix = " ";
            }
            System.out.println(sb.toString());
        }
        System.out.println("------");
    }

    public static void main(String[] args) {
        int index = 0;
        ConnectFour connectFour = new ConnectFour(10, 10);
        Random random = new Random();
        while (!connectFour.isFull()) {
            int c = random.nextInt(10);
            char player = index++ % 2 == 0 ? '0' : '1';
            while (!connectFour.drop(c, player)) {
                c = random.nextInt(10);
            }
            int r = connectFour.lastR;
            connectFour.print();
            if (connectFour.isWin(r, c, player)) {
                System.out.println(player + " wins!!!" + " r: " + r + " c " + c);
                break;
            }
        }
        if (connectFour.isFull()) {
            System.out.println("It's tie!");
        }
    }
}
