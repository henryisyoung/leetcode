package apple;

public class SudokuSolver {
    public void solveSudoku(char[][] board) {
        boolean[][] map1 = new boolean[9][9], map2 = new boolean[9][9], map3 = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char cur = board[i][j];
                if (cur == '.') continue;
                int pos = cur - '1';
                map1[i][pos] = true;
                map2[j][pos] = true;
                map3[(i / 3) * 3 + j / 3][pos] = true;
            }
        }
        dfsSearchAll(0, map1, map2, map3, board);
    }

    private boolean dfsSearchAll(int pos, boolean[][] map1, boolean[][] map2, boolean[][] map3, char[][] board) {
        if (pos == 81) return true;
        int r = pos / 9, c = pos % 9;
        if (board[r][c] != '.') {
            if (dfsSearchAll(pos + 1, map1, map2, map3, board)) return true;
            return false;
        }
        int b = (r / 3) * 3 + c / 3;
        for (char k = '1'; k <= '9'; k++) {
            int index = k - '1';
            if (map1[r][index] || map2[c][index] || map3[b][index]) continue;
            map1[r][index] = map2[c][index] = map3[b][index] = true;
            board[r][c] = k;

            if (dfsSearchAll(pos + 1, map1, map2, map3, board)) return true;

            map1[r][index] = map2[c][index] = map3[b][index] = false;
            board[r][c] = '.';
        }
        return false;
    }
}
