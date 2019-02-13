package leetcode.dataStructrue.heap;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

public class TrappingRainWaterII {
    public int trapRainWater(int[][] heightMap) {
        int result = 0;
        if (heightMap == null || heightMap.length == 0 ||
                heightMap[0] == null || heightMap[0].length == 0) {
            return result;
        }
        int rows = heightMap.length, cols = heightMap[0].length;
        int[][] isVisted = new int[rows][cols];
        PriorityQueue<Node> pq = new PriorityQueue<>(rows * cols, new NodeComparator());

        for (int i = 0; i < rows; i++) {
            pq.add(new Node(i, 0, heightMap[i][0]));
            pq.add(new Node(i, cols - 1, heightMap[i][cols - 1]));
            isVisted[i][0] = 1;
            isVisted[i][cols - 1] = 1;
        }

        for (int j = 1; j < cols - 1; j++) {
            pq.add(new Node(0, j, heightMap[0][j]));
            pq.add(new Node(rows - 1, j, heightMap[rows - 1][j]));
            isVisted[0][j] = 1;
            isVisted[rows - 1][j] = 1;
        }
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            for (int[] dir : dirs) {
                int r = cur.r, c = cur.c, h = cur.h;
                int nRow = r + dir[0], nCol = c + dir[1];
                if (nRow >= 0 && nRow < rows && nCol >= 0 && nCol < cols && isVisted[nRow][nCol] == 0) {
                    isVisted[nRow][nCol] = 1;
                    result += Math.max(h - heightMap[nRow][nCol], 0);
                    pq.add(new Node(nRow, nCol, Math.max(h, heightMap[nRow][nCol])));
                }
            }
        }

        return result;
    }

    private class Node{
        int r, c, h;
        public Node(int r, int c, int h) {
            this.r = r;
            this.c = c;
            this.h = h;
        }
    }

    private class NodeComparator implements Comparator<Node>{
        @Override
        public int compare(Node o1, Node o2) {
            return o1.h - o2.h;
        }
    }
}
