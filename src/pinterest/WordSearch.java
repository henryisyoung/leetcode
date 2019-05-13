package pinterest;

public class WordSearch {
    public boolean exist(char[][] board, String word) {
        if (word == null || word.length() == 0) {
            return true;
        }
        int rows = board.length, cols = board[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == word.charAt(0)) {
                    boolean[][] visited = new boolean[rows][cols];
                    if (searchAll(i, j, word.toCharArray(), 0, board, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean searchAll(int sr, int sc, char[] target, int start, char[][] board, boolean[][] visited) {
        int rows=board.length, cols=board[0].length;
        int[][] dir = {{+1,0},{-1,0},{0,+1},{0,-1}};
        if (board[sr][sc] == target[start]) {
            visited[sr][sc] = true;
            if (start == target.length - 1) {
                return true;
            }
            for(int i=0; i<dir.length;i++){
                int nr = sr + dir[i][0], nc = sc + dir[i][1];
                if( nr>=0 && nr<rows && nc>=0 && nc<cols &&!visited[nr][nc]&&
                        searchAll(nr,nc,target,start+1, board, visited)){
                    return true;
                }
            }
            visited[sr][sc] = false;
        }
        return false;
    }
}
