package pinterest;

import java.util.Arrays;

public class SudokuSolver {
    public void solveSudoku(char[][] board) {
        boolean[][] map1 = new boolean[9][9], map2 = new boolean[9][9], map3 = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != '.') {
                    int val = board[i][j] - '1';
                    map1[i][val] = true;
                    map2[j][val] = true;
                    int block = (i / 3) * 3 + j / 3 ;
                    map3[block][val] = true;
                }
            }
        }
        dfsSearchAll(0, board, map1, map2, map3);
        System.out.println(Arrays.deepToString(board));
    }

    private boolean dfsSearchAll(int pos, char[][] board, boolean[][] map1, boolean[][] map2, boolean[][] map3) {
        if (pos == 81) {
            return true;
        }
        for (int i = pos; i < 81; i++) {
            int r = i / 9, c = i % 9;
            if (board[r][c] == '.') {
                for (int k = 0; k < 9; k++) {
                    int block = (r / 3) * 3 + c / 3 ;
                    if (map1[r][k] || map2[c][k] || map3[block][k]) continue;

                    board[r][c] = (char) (k + '1');
                    map1[r][k] = true;
                    map2[c][k] = true;
                    map3[block][k] = true;
                    if (dfsSearchAll(pos + 1, board, map1, map2, map3)) {
                        return true;
                    }
                    board[r][c] = '.';
                    map1[r][k] = false;
                    map2[c][k] = false;
                    map3[block][k] = false;
                }
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        char[][] board = {
                {'5','3','.','.','7','.','.','.','.'},
                {'6','.','.','1','9','5','.','.','.'},
                {'.','9','8','.','.','.','.','6','.'},
                {'8','.','.','.','6','.','.','.','3'},
                {'4','.','.','8','.','3','.','.','1'},
                {'7','.','.','.','2','.','.','.','6'},
                {'.','6','.','.','.','.','2','8','.'},
                {'.','.','.','4','1','9','.','.','5'},
                {'.','.','.','.','8','.','.','7','9'}};
        SudokuSolver solver = new SudokuSolver();
        solver.solveSudoku(board);
    }
}
