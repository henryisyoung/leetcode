package leetcode.graphAndSearch;

import java.util.Arrays;

public class SudokuSolver {
    public void solveSudoku(char[][] board) {
        boolean[][] map1 = new boolean[9][9];
        boolean[][] map2 = new boolean[9][9];
        boolean[][] map3 = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++){
                if (board[i][j] != '.') {
                    int num = board[i][j] - '0' - 1;
                    map1[i][num] = true;
                    map2[j][num] = true;
                    map3[(i/3) * 3 + j/3][num] = true;
                }
            }
        }
        fillAll(0, map1, map2, map3, board);
    }

    private boolean fillAll(int pos, boolean[][] map1, boolean[][] map2, boolean[][] map3, char[][] board) {
        if (pos >= 81) {
            return true;
        }
        for (int i = pos; i < 81; i++) {
            int r = i / 9, c = i % 9;
            if (board[r][c] != '.') {
                continue;
            }
            for (int k = 0; k < 9; k++) {
                if (map1[r][k] || map2[c][k] || map3[(r/3) * 3 + c / 3][k]) {
                    continue;
                }
                map1[r][k] = true;
                map2[c][k] = true;
                map3[(r/3) * 3 + c / 3][k] = true;
                board[r][c] = (char)(k+49);
                if (fillAll(i + 1, map1, map2, map3, board)) {
                    return true;
                }
                map1[r][k] = false;
                map2[c][k] = false;
                map3[(r/3) * 3 + c / 3][k] = false;
                board[r][c] = '.';
            }
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println((char) (1 + '0'));
        char[][] board = {
                {'5','3','.','.','7','.','.','.','.'},
                {'6','.','.','1','9','5','.','.','.'},
                {'.','9','8','.','.','.','.','6','.'},
                {'8','.','.','.','6','.','.','.','3'},
                {'4','.','.','8','.','3','.','.','1'},
                {'7','.','.','.','2','.','.','.','6'},
                {'.','6','.','.','.','.','2','8','.'},
                {'.','.','.','4','1','9','.','.','5'},
                {'.','.','.','.','8','.','.','7','9'}
        };
        SudokuSolver solver = new SudokuSolver();
        solver.solveSudoku(board);
    }
}
