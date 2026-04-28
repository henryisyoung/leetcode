package recovery;

import java.util.Map;
import java.util.PriorityQueue;

public class TrappingRainWaterII {

    static class Node {
        int r, c, h;
        public Node(int r, int c, int h) {
            this.r = r;
            this.c = c;
            this.h = h;
        }
    }

    public int trapRainWater(int[][] heightMap) {
        if (heightMap == null || heightMap[0] == null) {
            return 0;
        }
        int count  = 0;
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (a.h - b.h));

        int rows = heightMap.length, cols = heightMap[0].length;
        boolean[][] visited = new boolean[rows][cols];

        for (int i = 0 ; i < rows; i++) {
            pq.add(new Node(i, 0, heightMap[i][0]));
            pq.add(new Node(i, cols - 1, heightMap[i][cols - 1]));
            visited[i][0] = visited[i][cols - 1] = true;
        }

        for (int i = 0 ; i < cols; i++) {
            pq.add(new Node(0, i, heightMap[0][i]));
            pq.add(new Node(rows - 1, i, heightMap[rows - 1][i]));
            visited[0][i] = visited[rows - 1][i] = true;
        }

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            for (int[] dir : dirs) {
                int nr = cur.r + dir[0], nc = cur.c + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                    count += Math.max(0, cur.h - heightMap[nr][nc]);
                    visited[nr][nc] = true;
                    pq.add(new Node(nr, nc, Math.max(cur.h, heightMap[nr][nc])));
                }
            }
        }

        return count;
    }
}
