package airbnb;

import java.util.*;

public class SlidingPuzzle {
    class BoardState{
        int zr, zc;
        String boardString;
        int[][] board;
        public BoardState(int zr, int zc, int[][] board) {
            this.board = board;
            this.zc = zc;
            this.zr = zr;
            this.boardString = Arrays.deepToString(board);
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
        BoardState start = new BoardState(zr, zc, board);
        int[][] target = {{1,2,3}, {4,5,0}};
        Set<String> set = new HashSet<>();
        set.add(start.boardString);
        int dist = 0;
        Queue<BoardState> queue = new LinkedList<>();
        queue.add(start);
        int[][] dirs = {{1,0},{0,1},{-1,0},{0,-1}};

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                BoardState cur = queue.poll();
                if (cur.boardString.equals(Arrays.deepToString(target))) {
                    return dist;
                }
                for (int[] dir : dirs) {
                    int nr = dir[0] + cur.zr, nc = dir[1] + cur.zc;
                    if (nr >= 0 && nr < 2 && nc >= 0 && nc < 3) {
                        int[][] newBoard = new int[2][3];
                        int index = 0;
                        for (int[] arr : cur.board) {
                            newBoard[index++] = arr.clone();
                        }
                        newBoard[cur.zr][cur.zc] = newBoard[nr][nc];
                        newBoard[nr][nc] = 0;
                        BoardState next = new BoardState(nr, nc, newBoard);
                        if (set.contains(next.boardString)) continue;
                        set.add(next.boardString);
                        queue.add(next);
                    }
                }

            }
            dist++;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[][] target = {{1,2,3},{4,5,0}};
        int[][] target2 = {{1,2,3},{0,4,5}};
        SlidingPuzzle solver = new SlidingPuzzle();
        int count = solver.slidingPuzzle(target2);
        System.out.println(count);
    }
}
