package recovery;

public class SudokuSolver {
    public void solveSudoku(char[][] board) {
        boolean[][] rowRecorder = new boolean[9][9], colRecorder = new boolean[9][9], blockRecorder = new boolean[9][9];

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == '.') continue;
                int blx = r / 3 * 3 + c / 3;
                int number = board[r][c] - '1';
                rowRecorder[r][number] = true;
                colRecorder[c][number] = true;
                blockRecorder[blx][number] = true;
            }
        }

        findAllCells(board, rowRecorder, colRecorder, blockRecorder, 0);
    }

    private boolean findAllCells(char[][] board, boolean[][] rowRecorder, boolean[][] colRecorder, boolean[][] blockRecorder, int pos) {
        if (pos == 81) {
            return true;
        }
        int r = pos / 9, c = pos % 9;
        if (board[r][c] != '.') {
            return findAllCells(board, rowRecorder, colRecorder, blockRecorder, pos + 1);
        }
        for (int number = 0; number < 9; number++) {
            int blx = r / 3 * 3 + c / 3;
            if (rowRecorder[r][number] || colRecorder[c][number] || blockRecorder[blx][number]) continue;
            rowRecorder[r][number] = colRecorder[c][number] = blockRecorder[blx][number] = true;
            board[r][c] = (char) (number + '1');
            if (findAllCells(board, rowRecorder, colRecorder, blockRecorder, pos + 1)) {
                return true;
            }
            rowRecorder[r][number] = colRecorder[c][number] = blockRecorder[blx][number] = false;
            board[r][c] = '.';
        }
        return false;
    }
}
