package facebook;

import java.util.*;

public class SlidingPuzzle {
    class BoardState{
        int zr, zc;
        String board;
        int[][] curBoard;
        public BoardState(int zr, int zc, int[][] boardArray) {
            this.zc = zc;
            this.zr = zr;
            this.curBoard = boardArray;
            this.board = Arrays.deepToString(boardArray);
        }
    }
    public int slidingPuzzle(int[][] board) {
        int zr = 0 , zc = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    zr = i;
                    zc = j;
                }
            }
        }
        BoardState start = new BoardState(zr, zc, board);
        Queue<BoardState> queue = new LinkedList<>();
        queue.add(start);

        String target = Arrays.deepToString(new int[][]{{1,2,3},{4,5,0}});
        int len = 0;
        Set<String> visited = new HashSet<>();
        visited.add(start.board);
        int[][] dirs = {{1,0},{0,1},{-1,0},{0,-1}};

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                BoardState cur = queue.poll();
                int r = cur.zr, c = cur.zc;
                if (cur.board.equals(target)) return len;
                for (int[] dir : dirs) {
                    int nr = r + dir[0], nc = c + dir[1];
                    if (nr >= 0 && nr < 2 && nc >= 0 && nc < 3) {
                        int[][] nextBoard = new int[2][3];
                        for (int k = 0; k < 2; k++) {
                            for (int t = 0; t < 3; t++) {
                                nextBoard[k][t] = cur.curBoard[k][t];
                            }
                        }
                        nextBoard[r][c] = nextBoard[nr][nc];
                        nextBoard[nr][nc] = 0;
                        if (visited.contains(Arrays.deepToString(nextBoard))) continue;
                        BoardState nextState = new BoardState(nr, nc, nextBoard);
                        visited.add(nextState.board);
                        queue.add(nextState);
                    }
                }
            }
            len++;
        }

        return -1;
    }

    public static void main(String[] args) {
        int[][] target = {{1,2,3},{5,4,0}};
        int[][] target2 = {{1,2,3},{0,4,5}};
        SlidingPuzzle solver = new SlidingPuzzle();
        int count = solver.slidingPuzzle(target);
        System.out.println(count);
    }
}
