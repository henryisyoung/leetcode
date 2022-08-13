package google.vo;

import java.util.PriorityQueue;

public class SwimRisingWater {
    static class Node{
        int r, c , h;
        public Node(int r, int c, int h) {
            this.r = r;
            this.c =c;
            this.h = h;
        }
    }

    // 1. heap n * n * logn
    // 2. binary search + dfs n * n * logn
    public static int swimInWater(int[][] grid) {
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (a.h - b.h));

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        int min = 0;
        pq.add(new Node(0, 0, grid[0][0]));
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        visited[0][0] = true;
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            min = Math.max(min, cur.h);

            if (cur.r == rows - 1 && cur.c == cols - 1) {
                return min;
            }
            int r = cur.r, c = cur.c;
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || visited[nr][nc]) {
                    continue;
                }
                visited[nr][nc] = true;
                pq.add(new Node(nr, nc, grid[nr][nc]));
            }
        }
        return min;
    }

    public static void main(String[] args) {
        int[][] grid = {
                {0,1,2,3,4},
                {24,23,22,21,5},
                {12,13,14,15,16},
                {11,17,18,19,20},
                {10,9,8,7,6}
        };

        System.out.println(swimInWater(grid));
    }
}
