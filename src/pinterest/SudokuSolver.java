package pinterest;

import java.util.Arrays;

public class SudokuSolver {
    public void solveSudoku(char[][] board) {
        boolean[][] map1 = new boolean[9][9], map2 = new boolean[9][9], map3 = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != '.') {
                    int num = board[i][j] - '1';
                    map1[i][num] = true;
                    map2[j][num] = true;
                    map3[(i/3) * 3 + j / 3][num] = true;
                }
            }
        }
//        System.out.println(Arrays.deepToString(map1));
//        System.out.println(Arrays.deepToString(map2));
//        System.out.println(Arrays.deepToString(map3));
        dfsFillAll(board, map1, map2, map3, 0);
        System.out.println(Arrays.deepToString(board));
    }

    private boolean dfsFillAll(char[][] board, boolean[][] map1, boolean[][] map2, boolean[][] map3, int pos) {
        if (pos >= 81) {
            return true;
        }
        for (int i = pos; i < 81; i++) {
            int r = i / 9, c = i % 9;
            if (board[r][c] == '.') {
                for (int k = 1; k <= 9; k++) {
                    if (map1[r][k - 1] || map2[c][k - 1] || map3[(r/3) * 3 + c / 3][k - 1]) {
                        continue;
                    }
                    map1[r][k - 1] = true;
                    map2[c][k - 1] = true;
                    map3[(r/3) * 3 + c / 3][k - 1] = true;
                    board[r][c] = (char) (k + '0');
                    if (dfsFillAll(board, map1, map2, map3, i + 1)) {
                        return true;
                    }
                    board[r][c] = '.';
                    map1[r][k - 1] = false;
                    map2[c][k - 1] = false;
                    map3[(r/3) * 3 + c / 3][k - 1] = false;
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
