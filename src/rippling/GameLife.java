package rippling;

public class GameLife {
    // 0 prev dead, 1 prev live, 2 prev live to cur dead, 3 prev dead to cur live
    public void gameOfLife(int[][] board) {
        int rows = board.length, cols = board[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int ln = findLiveNeighbor(board, i, j);
                if (board[i][j] == 1 && (ln < 2 || ln > 3)) {
                    board[i][j] = 2;
                }
                if (board[i][j] == 0 && (ln == 3)) {
                    board[i][j] = 3;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = board[i][j] % 2;
            }
        }
    }

    private int findLiveNeighbor(int[][] board, int r, int c) {
        int count = 0;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}, {1,1},{-1,-1},{-1,1},{1,-1}};
        int rows = board.length, cols = board[0].length;

        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                continue;
            }
            if (board[nr][nc] == 1 || board[nr][nc] == 2) {
                count ++;
            }
        }
        return count;
    }
}
