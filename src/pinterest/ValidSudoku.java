package pinterest;

public class ValidSudoku {
    public boolean isValidSudoku(char[][] board) {
        boolean[] visited = new boolean[9];
        for (int i = 0; i < 9; i++) {
            visited = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (!isValid(visited, board, i, j)) {
                    return false;
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            visited = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (!isValid(visited, board, j, i)) {
                    return false;
                }
            }
        }
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                visited = new boolean[9];
                for (int k = 0; k < 9; k++) {
                    if (!isValid(visited, board, i + k / 3, j + k % 3)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isValid(boolean[] visited, char[][] board, int i, int j) {
        if (board[i][j] == '.') {
            return true;
        }
        int num = board[i][j] - '0';
        if (num > 9 || num < 0 || visited[num - 1]) {
            return false;
        }
        visited[num - 1] = true;
        return true;
    }
}
