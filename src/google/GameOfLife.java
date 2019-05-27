package google;

public class GameOfLife {
    // https://segmentfault.com/a/1190000003819277
//    0 : 上一轮是0，这一轮过后还是0
//    1 : 上一轮是1，这一轮过后还是1
//    2 : 上一轮是1，这一轮过后变为0
//    3 : 上一轮是0，这一轮过后变为1

//    Any live cell with fewer than two live neighbors dies, as if caused by under-population.
//    Any live cell with two or three live neighbors lives on to the next generation.
//    Any live cell with more than three live neighbors dies, as if by over-population..
//    Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
    public int[][] gameOfLife(int[][] board) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) {
            return null;
        }
        int m = board.length, n = board[0].length;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1, 1}, {-1, -1}};
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int lives = 0;
                for (int[] dir : dirs) {
                    int ni = i + dir[0], nj = j + dir[1];
                    if (ni >= 0 && ni < m && nj >= 0 && nj < n) {
                        if (board[ni][nj] == 1 || board[ni][nj] == 2) {
                            lives++;
                        }
                    }
                }
                if (board[i][j] == 1 && (lives < 2 || lives > 3)) {
                    board[i][j] = 2;
                } else if (board[i][j] == 1 && (lives == 2 || lives == 3)) {
                    board[i][j] = 1;
                } else if (board[i][j] == 0 && lives == 3) {
                    board[i][j] = 3;
                }
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = board[i][j] % 2;
            }
        }
        return board;
    }

    public static void main(String[] args) {

    }
}
