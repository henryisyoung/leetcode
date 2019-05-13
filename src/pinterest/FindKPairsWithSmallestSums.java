package pinterest;

import java.util.*;

public class FindKPairsWithSmallestSums {
    public List<int[]> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<int[]> result = new ArrayList<>();
        if (nums1 == null || nums1.length == 0 || nums2 == null || nums2.length == 0) {
            return result;
        }
        int m = nums1.length, n = nums2.length;

        PriorityQueue<Node> pq = new PriorityQueue<Node>(m * n, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.val - o2.val;
            }
        });
        boolean[][] visited = new boolean[m][n];
        pq.add(new Node(0, 0, nums1[0] + nums2[0]));
        visited[0][0] = true;

        int[][] dirs = {{1,0},{0,1}};
        while (!pq.isEmpty() && k > 0) {
            Node cur = pq.poll();
            int r = cur.r, c = cur.c;
            result.add(new int[]{nums1[r], nums2[c]});
            k--;
            if (k == 0) {
                return result;
            }
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr < m && nc < n && !visited[nr][nc]) {
                    int val = nums1[nr] + nums2[nc];
                    pq.add(new Node(nr, nc, val));
                    visited[nr][nc] = true;
                }
            }
        }

        return result;
    }
    class Node{
        int r, c, val;
        public Node(int r, int c, int val) {
            this.c = c;
            this.r = r;
            this.val = val;
        }
    }
}
