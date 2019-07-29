package pinterest;


public class WordSearch {
    public boolean exist(char[][] board, String word) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) return false;
        int rows = board.length, cols = board[0].length;
        if (rows * cols < word.length()) return false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean[][] visited = new boolean[rows][cols];
                if (dfsSearchAll(i, j, word, 0, board, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfsSearchAll(int r, int c, String word, int pos, char[][] board, boolean[][] visited) {
        int rows = board.length, cols = board[0].length;
        if (pos == word.length()) {
            return true;
        }
        if (r < 0 || r >= rows || c < 0 || c >= cols || visited[r][c] || board[r][c] != word.charAt(pos)) {
            return false;
        }
        visited[r][c] = true;
        int[][] dirs = {{1,0}, {0,1},{-1,0},{0,-1}};
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            if (dfsSearchAll(nr, nc, word, pos + 1, board, visited)) {
                return true;
            }
        }
        visited[r][c] = false;
        return false;
    }

    public static void main(String[] args) {
        char[][] board =
                {
                        {'A','B','C','E'},
                        {'S','F','C','S'},
                        {'A','D','E','E'}
                };
        String word = "ABCCED";
        WordSearch solution = new WordSearch();
        System.out.println(solution.exist(board, word));
    }
}
