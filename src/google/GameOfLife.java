package google;

import java.util.*;

public class GameOfLife {
    //https://massivealgorithms.blogspot.com/2015/10/leetcodegame-of-life.html
    // https://segmentfault.com/a/1190000003819277
//    0 : 上一轮是0，这一轮过后还是0
//    1 : 上一轮是1，这一轮过后还是1
//    2 : 上一轮是1，这一轮过后变为0
//    3 : 上一轮是0，这一轮过后变为1

//    Any live cell with fewer than two live neighbors dies, as if caused by under-population.
//    Any live cell with two or three live neighbors lives on to the next generation.
//    Any live cell with more than three live neighbors dies, as if by over-population..
//    Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
    public static int[][] gameOfLife(int[][] board) {
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

    public static int[][] gameOfLifeExtraSpace(int[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        // Create a copy of the original board
        int[][] copyBoard = new int[rows][cols];

        // Create a copy of the original board
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                copyBoard[row][col] = board[row][col];
            }
        }
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1, 1}, {-1, -1}};

        // Iterate through board cell by cell.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int lives = 0;
                for (int[] dir : dirs) {
                    int ni = i + dir[0], nj = j + dir[1];
                    if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                        lives += copyBoard[ni][nj];
                    }
                }
                if (copyBoard[i][j] == 1 && lives >= 2 && lives <= 3) {
                    board[i][j] = 0;
                } else {
                    if (lives == 3) {
                        board[i][j] = 1;
                    }
                }
            }
        }
        return board;
    }

    public static int[][] gameOfLifeInfiniteBoard(int[][] board) {
        Set<Coord> live = new HashSet<>();
        int m = board.length;
        int n = board[0].length;
        for (int i = 0; i<m; i++) {
            for (int j = 0; j<n; j++) {
                if (board[i][j] == 1) {
                    live.add(new Coord(i,j));
                }
            }
        };
        live = gameOfLife(live);
        for (int i = 0; i<m; i++) {
            for (int j = 0; j<n; j++) {
                board[i][j] = live.contains(new Coord(i,j))?1:0;
            }
        }
        return board;
    }

    private static Set<Coord> gameOfLife(Set<Coord> live) {
        Map<Coord,Integer> neighbours = new HashMap<>();
        for (Coord cell : live) {
            for (int i = cell.i-1; i<cell.i+2; i++) {
                for (int j = cell.j-1; j<cell.j+2; j++) {
                    if (i==cell.i && j==cell.j) continue;
                    Coord c = new Coord(i,j);
                    if (neighbours.containsKey(c)) {
                        neighbours.put(c, neighbours.get(c) + 1);
                    } else {
                        neighbours.put(c, 1);
                    }
                }
            }
        }
        Set<Coord> newLive = new HashSet<>();
        for (Map.Entry<Coord,Integer> cell : neighbours.entrySet())  {
            if (cell.getValue() == 3 || cell.getValue() == 2 && live.contains(cell.getKey())) {
                newLive.add(cell.getKey());
            }
        }
        return newLive;
    }

    private static class Coord {
        int i;
        int j;
        private Coord(int i, int j) {
            this.i = i;
            this.j = j;
        }
        public boolean equals(Object o) {
            return o instanceof Coord && ((Coord)o).i == i && ((Coord)o).j == j;
        }
        public int hashCode() {
            int hashCode = 1;
            hashCode = 31 * hashCode + i;
            hashCode = 31 * hashCode + j;
            return hashCode;
        }
    }

    public static void main(String[] args) {
        int[][] board = new int[][]{
                {0,1,0},
                {0,0,1},
                {1,1,1},
                {0,0,0}
        };
//        int[][] r2 = gameOfLifeExtraSpace(board), r1 = gameOfLife(board);
        int[][] r3 = gameOfLifeInfiniteBoard(board);
//        System.out.println(Arrays.deepToString(r1));
//        System.out.println(Arrays.deepToString(r2));
        System.out.println(Arrays.deepToString(r3));
    }
}
