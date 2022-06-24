package apple;

public class ValidSudoku {
    public boolean isValidSudoku(char[][] board) {
        for (int i = 0; i < 9; i++) {
            boolean[] visited = new boolean[10];
            for (int j = 0; j < 9; j++) {
                if (!validHelper(visited, board[i][j])) {
                    return false;
                }
            }
        }
        for (int j = 0; j < 9; j++) {
            boolean[] visited = new boolean[10];
            for (int i = 0; i < 9; i++) {
                if (!validHelper(visited, board[i][j])) {
                    return false;
                }
            }
        }

        for (int i = 0; i < 9; i+= 3) {
            for (int j = 0; j < 9; j+= 3) {
                boolean[] visited = new boolean[10];
                for (int k = 0; k < 9; k++) {
                    int r = i + k / 3, c = j + k % 3;
                    if (!validHelper(visited, board[r][c])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean validHelper(boolean[] visited, char c) {
        if (c == '.') return true;
        int num = c - '0';
        if (visited[num]) return false;
        visited[num] = true;
        return true;
    }
}
