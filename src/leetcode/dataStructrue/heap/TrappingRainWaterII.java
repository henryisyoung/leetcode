package leetcode.dataStructrue.heap;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TrappingRainWaterII {
    class Node {
        int r, c, h;
        public Node(int r, int c, int h) {
            this.r = r;
            this.c = c;
            this.h = h;
        }
    }
    public int trapRainWater(int[][] heightMap) {
       if (heightMap == null || heightMap.length == 0 || heightMap[0] == null || heightMap[0].length == 0) {
           return 0;
       }
       int rows = heightMap.length, cols = heightMap[0].length;
       PriorityQueue<Node> pq = new PriorityQueue<>(rows * cols, new Comparator<Node>() {
           @Override
           public int compare(Node o1, Node o2) {
               return o1.h - o2.h;
           }
       });
       int result = 0;
       boolean[][] visited = new boolean[rows][cols];
       for (int i = 0; i < rows; i++) {
           visited[i][0] = true;
           visited[i][cols - 1] = true;
           Node n1 = new Node(i, 0, heightMap[i][0]);
           Node n2 = new Node(i, cols - 1, heightMap[i][cols - 1]);
           pq.add(n1);
           pq.add(n2);
       }

       for (int i = 0; i < cols; i++) {
           visited[0][i] = true;
           visited[rows - 1][i] = true;
           Node n1 = new Node(0, i, heightMap[0][i]);
           Node n2 = new Node(rows - 1, i, heightMap[rows - 1][i]);
           pq.add(n1);
           pq.add(n2);
       }

       int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
       while (!pq.isEmpty()) {
           Node n = pq.poll();
           int r = n.r, c = n.c, h = n.h;
           for (int[] dir : dirs) {
               int nr = r + dir[0], nc = c + dir[1];
               if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                   visited[nr][nc] = true;
                   result += Math.max(h - heightMap[nr][nc], 0);
                   pq.add(new Node(nr, nc, Math.max(h, heightMap[nr][nc])));
               }
           }
       }
       return result;
    }
}
