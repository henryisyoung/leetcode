package pinterest;

public class ValidSudoku {
    public boolean isValidSudoku(char[][] board) {
        for (int i = 0; i < 9; i++) {
            boolean[] visited = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (!isValid(visited, board, i, j)) {
                    return false;
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            boolean[] visited = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (!isValid(visited, board, j, i)) {
                    return false;
                }
            }
        }
        for (int i = 0; i < 9 ;i += 3) {
            for (int j = 0; j < 9; j += 3) {
                boolean[] visited = new boolean[9];
                for (int k = 0; k < 9; k++) {
                    int r = i + k / 3, c = j + k % 3;
                    if (!isValid(visited, board, r, c)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isValid(boolean[] visited, char[][] board, int i, int j) {
        if (board[i][j] == '.') return true;
        int num = board[i][j] - '1';
        if (visited[num]) return false;
        visited[num] = true;
        return true;
    }
}
