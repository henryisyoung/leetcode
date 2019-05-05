package leetcode.dataStructrue.heap;

import java.util.*;

public class FindKPairsWithSmallestSums {
    private class Node{
        int row, col, sum;
        public Node (int r, int c, int sum) {
            this.row = r;
            this.col = c;
            this.sum = sum;
        }
    }

    public List<int[]> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<int[]> result = new ArrayList<>();
        if (k == 0) {
            return result;
        }
        int rows = nums1.length, cols = nums2.length;
        if (rows * cols == 0) {
            return result;
        }
        PriorityQueue<Node> pq = new PriorityQueue<>(rows * cols, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.sum - o2.sum;
            }
        });
        pq.add(new Node(0, 0, nums1[0] + nums2[0]));
        int[][] dirs = {{1, 0}, {0, 1}};
        boolean[][] visited = new boolean[rows][cols];
        visited[0][0] = true;

        for (int i = 0; i < k && !pq.isEmpty(); i++) {
            Node n = pq.poll();
            int curR = n.row, curC = n.col;
            result.add(new int[]{nums1[curR], nums2[curC]});
            for (int[] dir : dirs) {
                int nR = curR + dir[0], nC = curC + dir[1];
                if (nR < rows && nC < cols && !visited[nR][nC]) {
                    pq.add(new Node(nR, nC, nums1[nR] + nums2[nC]));
                    visited[nR][nC] = true;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        FindKPairsWithSmallestSums sovler = new FindKPairsWithSmallestSums();
        int[] nums1 = new int[]{1,7,11}, nums2 = new int[]{2,4,6};
        int k = 3;
        List<int[]> result = sovler.kSmallestPairs(nums1, nums2, k);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }
}
