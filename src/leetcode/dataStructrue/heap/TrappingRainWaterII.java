package leetcode.dataStructrue.heap;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

public class TrappingRainWaterII {
    public int trapRainWater(int[][] heightMap) {
        if (heightMap == null || heightMap.length == 0
                || heightMap[0] == null || heightMap[0].length == 0) {
            return 0;
        }
        int result = 0;
        int rows = heightMap.length, cols = heightMap[0].length;
        PriorityQueue<Node> pq = new PriorityQueue<>(rows * cols, new NodeComparator());
        int[][] table = new int[rows][cols];

        for (int i = 0; i < cols; i++) {
            pq.add(new Node(0, i, heightMap[0][i]));
            pq.add(new Node(rows - 1, i, heightMap[rows - 1][i]));
            table[0][i] = 1;
            table[rows - 1][i] = 1;

        }
        for (int j = 1; j < rows - 1; j++) {
            pq.add(new Node(j, 0, heightMap[j][0]));
            pq.add(new Node(j, cols - 1, heightMap[j][cols - 1]));
            table[j][0] = 1;
            table[j][cols - 1] = 1;
        }
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            for (int i = 0; i < 4; i++) {
                int nRow = cur.r + dirs[i][0];
                int nCol = cur.c + dirs[i][1];

                if (nRow >= 0 && nCol >= 0 && nRow < rows && nCol < cols && table[nRow][nCol] == 0) {
                    table[nRow][nCol] = 1;
                    pq.add(new Node(nRow, nCol, Math.max(cur.h, heightMap[nRow][nCol])));
                    result += Math.max(0, cur.h - heightMap[nRow][nCol]);
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
