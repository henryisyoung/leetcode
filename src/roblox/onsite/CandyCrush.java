package roblox.onsite;

import java.util.Arrays;

public class CandyCrush {
    public static int[][] candyCrush(int[][] board) {
        int rows = board.length, cols = board[0].length;
        boolean hasCrush = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 2; j++) {
                int v1 = Math.abs(board[i][j]), v2 = Math.abs(board[i][j + 1]), v3 = Math.abs(board[i][j + 2]);
                if (v1 != 0 && v1 == v2 && v2 == v3) {
                    hasCrush = true;
                    board[i][j] = board[i][j + 1] = board[i][j + 2] = -v1;
                }
            }
        }

        for (int i = 0; i < rows  - 2; i++) {
            for (int j = 0; j < cols; j++) {
                int v1 = Math.abs(board[i][j]), v2 = Math.abs(board[i  + 1][j]), v3 = Math.abs(board[i + 2][j ]);
                if (v1 != 0 && v1 == v2 && v2 == v3) {
                    hasCrush = true;
                    board[i][j] = board[i  + 1][j] = board[i + 2][j ] = -v1;
                }
            }
        }
        for (int c = 0; c < cols; c++) {
            int index = rows - 1;
            for (int r = rows - 1; r >= 0; r--) {
                if (board[r][c] > 0) {
                    board[index--][c] = board[r][c];
                }
            }
            while (index >= 0) {
                board[index--][c] = 0;
            }
        }
        return hasCrush ? candyCrush(board) : board;
    }

    public static void main(String[] args) {
        int[][] board = {
                {110,   5,    112,  113,    114},
                {210,   211,  5,    213,    214},
                {310,   311,  3,    313,    314},
                {410,   411,  412,  5,      414},
                {5,     1,    512,  3,      3},
                {610,   4,    1,    613,    614},
                {710,   1,    2,    713,    714},
                {810,   1,    2,    1,      1},
                {1,     1,    2,    2,      2},
                {4,     1,    4,    4,      1014}
        };
        int[][] result = candyCrush(board);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }
}
