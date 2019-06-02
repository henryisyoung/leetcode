package leetcode.dataStructrue.heap;

import java.util.*;

public class IslandsWithBridge {
    public List<int[]> findMiniIslandsPath(int[][] board) {
        List<int[]> result = new ArrayList<>();
        int rows = board.length, cols = board[0].length;
        PriorityQueue<Node> pq = new PriorityQueue<> (rows * cols, (a, b) -> (a.level - b.level));
        pq.add(new Node(new int[]{0, cols - 1}, 0));
        boolean[][] visited = new boolean[rows][cols];
        Map<int[], int[]> map = new HashMap<>();
        visited[0][cols - 1] = true;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        int[] last = null;
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            int curR = cur.pos[0], curC = cur.pos[1];
            if (curR == rows - 1 && curC == cols - 1) {
                last = cur.pos;
                break;
            }
            for (int[] dir : dirs) {
                int nextR = curR + dir[0], nextC = curC + dir[1];
                if (nextR >= 0 && nextR < rows && nextC >= 0 && nextC < cols && !visited[nextR][nextC]) {
                    if (board[curR][curC] == 1 && board[nextR][nextC] == 2) {
                        Node next = new Node(new int[]{nextR, nextC},cur.level + 1);
                        pq.add(next);
                        map.put(next.pos, cur.pos);
                        visited[nextR][nextC] = true;
                    } else if (board[curR][curC] != 0 && board[nextR][nextC] != 0) {
                        Node next = new Node(new int[]{nextR, nextC}, cur.level);
                        pq.add(next);
                        map.put(next.pos, cur.pos);
                        visited[nextR][nextC] = true;
                    }
                }
            }
        }
        while (last != null) {
            result.add(last);
            last = map.get(last);
        }
        Collections.reverse(result);
        return result;
    }

    class Node{
        int level;
        int[] pos;
        public Node(int[] pos,  int level) {
            this.pos = pos;
            this.level = level;
        }
    }

    public static void main(String[] args) {
        int[][] board = {
                {1,1,1,1,1,1,1,1,1},
                {2,0,1,0,0,0,0,2,0},
                {1,1,1,0,0,0,0,1,1},
                {0,0,2,0,0,0,0,1,1},
                {0,0,1,1,1,0,0,1,1},
                {0,0,0,0,1,2,2,1,1}
        };
        IslandsWithBridge solver = new IslandsWithBridge();
        List<int[]> result = solver.findMiniIslandsPath(board);
        for (int[] pos : result) {
            System.out.println(Arrays.toString(pos));
        }
    }
}
