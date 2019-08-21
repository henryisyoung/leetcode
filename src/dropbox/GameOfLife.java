package dropbox;

import java.io.*;
import java.util.*;

public class GameOfLife {
    public void gameOfLife(int[][] board) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) return;
        int rows = board.length, cols = board[0].length;
        int[][] copyBoard = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copyBoard[i][j] = board[i][j];
            }
        }
        int[][] dirs = {{1,0},{1,1},{0,1},{0,-1},{-1,1},{-1,0},{1,-1}, {-1,-1}};
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int count = 0;
                for (int[] dir : dirs) {
                    int nr = i + dir[0], nc = j + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && copyBoard[nr][nc] == 1) {
                        count++;
                    }
                }
                if (board[i][j] == 1 && (count < 2 || count > 3)) {
                    board[i][j] = 0;
                } else if (board[i][j] == 0 && count == 3){
                    board[i][j] = 1;
                }
            }
        }
    }

    public void gameOfLifeNoExtraSpace(int[][] board) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) return;
        int rows = board.length, cols = board[0].length;
        int[][] dirs = {{1,0},{1,1},{0,1},{0,-1},{-1,1},{-1,0},{1,-1}, {-1,-1}};
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int count = 0;
                for (int[] dir : dirs) {
                    int nr = i + dir[0], nc = j + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && (board[nr][nc] == 1 || board[nr][nc] == 2)) {
                        count++;
                    }
                }
                if (board[i][j] == 1 && (count < 2 || count > 3)) {
                    board[i][j] = 2;
                } else if (board[i][j] == 0 && count == 3){
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
        }
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

    public void gameOfLifeMillionBoard(String path) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            int count  = 0;
            String line1 = reader.readLine();

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private String convertToString(int[] board) {
        return "";
    }

    private int[] buildArray(String line) {
        return null;
    }


}
