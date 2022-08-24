package airbnb.Phone;

import java.util.*;

public class SlidingPuzzle {

    class PuzzleBoard{
        private final int zr, zc;
        private final int[][] board;
        PuzzleBoard(int zr, int zc, int[][] board) {
            this.zr = zr;
            this.zc = zc;
            this.board = board;
        }

        public PuzzleBoard move(int[] dir) {
            int nr = zr + dir[0], nc = zc + dir[1];
            if (nr < 0 || nr >= 2 || nc < 0 || nc >= 3) return null;
            int[][] newBoard = new int[2][3];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3;j++) {
                    newBoard[i][j] = board[i][j];
                }
            }

            newBoard[zr][zc] = newBoard[nr][nc];
            newBoard[nr][nc] = 0;

            return new PuzzleBoard(nr, nc, newBoard);
        }

        public String toString(){
            return Arrays.deepToString(board);
        }

        public boolean win() {
            int[][] end = {{1,2,3},{4,5,0}};
            return toString().equals(Arrays.deepToString(end));
        }
    }

    public int slidingPuzzle(int[][] board) {
        int zr = 0, zc = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    zr = i;
                    zc = j;
                    break;
                }
            }
        }
        PuzzleBoard start = new PuzzleBoard(zr,zc, board);
        int level = 0;
        Queue<PuzzleBoard> queue = new LinkedList<>();
        queue.add(start);
        Set<String> visited = new HashSet<>();
        visited.add(start.toString());
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                PuzzleBoard cur = queue.poll();
                if (cur.win()) return level;
                for (int[] dir : dirs) {
                    PuzzleBoard next = cur.move(dir);
                    if (next == null) continue;
                    if (visited.contains(next.toString())) continue;
                    visited.add(next.toString());
                    queue.add(next);
                }
            }
            level++;
        }
        return -1;
    }
}
