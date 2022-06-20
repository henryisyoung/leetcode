package datastructure.backtracking;

public class WordSearch {
    public boolean exist(char[][] board, String word) {
        int rows = board.length, cols = board[0].length;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if(findAll(i,j, word, board, 0, new boolean[rows][cols])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findAll(int r, int c, String word, char[][] board, int pos, boolean[][] visited) {
        int rows = board.length, cols = board[0].length;
        if(pos == word.length()) return true;
        if(r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != word.charAt(pos) || visited[r][c] ){
            return false;
        }
        visited[r][c] = true;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        for(int[] dir : dirs) {
            if(findAll(r + dir[0], c + dir[1], word, board, pos + 1,visited)){
                return true;
            }
        }
        visited[r][c] = false;
        return false;
    }
}
