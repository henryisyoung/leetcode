package airbnb;

import java.util.*;

public class SlidingPuzzle {
    public int slidingPuzzle(int[][] board) {
        int R = board.length, C = board[0].length;
        int sr = 0, sc = 0;
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                if (board[i][j] == 0) {
                    sr = i;
                    sc = j;
                    break;
                }
            }
        }

        int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        Queue<Node> queue = new LinkedList<>();
        Node start = new Node(board, sr, sc, 0);
        queue.add(start);

        Set<String> seen = new HashSet();
        seen.add(start.boardstring);

        String target = Arrays.deepToString(new int[][]{{1,2,3}, {4,5,0}});

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.boardstring.equals(target)) {
                return node.depth;
            }
            for (int[] di: directions) {
                int nei_r = di[0] + node.zero_r;
                int nei_c = di[1] + node.zero_c;

                if (nei_r < 0 || nei_r >= R || nei_c < 0 || nei_c >= C) {
                    continue;
                }
                int[][] newboard = new int[R][C];
                int t = 0;
                for (int[] row: node.board) {
                    newboard[t++] = row.clone();
                }
                newboard[node.zero_r][node.zero_c] = newboard[nei_r][nei_c];
                newboard[nei_r][nei_c] = 0;

                Node nei = new Node(newboard, nei_r, nei_c, node.depth + 1);
                if (seen.contains(nei.boardstring)) {
                    continue;
                }
                queue.add(nei);
                seen.add(nei.boardstring);
            }
        }
        return -1;
    }

    class Node {
        int[][] board;
        String boardstring;
        int zero_r;
        int zero_c;
        int depth;

        Node(int[][] B, int r, int c, int d) {
            board = B;
            boardstring = Arrays.deepToString(board);
            zero_r = r;
            zero_c = c;
            depth = d;
        }
    }

    public static void main(String[] args) {
        int[][] target = {{1,2,3},{4,5,0}};
        int[][] target2 = {{1,2,3},{0,4,5}};
        SlidingPuzzle solver = new SlidingPuzzle();
        int count = solver.slidingPuzzle(target2);
        System.out.println(count);
    }
}
