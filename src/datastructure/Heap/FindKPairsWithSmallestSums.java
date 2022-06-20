package datastructure.Heap;

import java.util.*;

public class FindKPairsWithSmallestSums {
    static class Node {
        int i , j, sum;
        public Node(int i, int j, int sum) {
            this.i = i;
            this.j = j;
            this.sum = sum;
        }
    }

    public static List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums1 == null || nums1.length == 0 || nums2 == null || nums2.length == 0) {
            return result;
        }
        int m = nums1.length, n = nums2.length;

        PriorityQueue<Node> pq = new PriorityQueue<Node>(m * n, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.sum - o2.sum;
            }
        });
        boolean[][] visited = new boolean[m][n];
        pq.add(new Node(0, 0, nums1[0] + nums2[0]));
        visited[0][0] = true;
        int[][] dirs = {{1,0},{0,1}};
        while (!pq.isEmpty() && k > 0) {
            Node cur = pq.poll();
            int r = cur.i, c = cur.j;
            result.add(Arrays.asList(nums1[r], nums2[c]));
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

    public static void main(String[] args) {
        int[] nums1 = {1,1,2}, num2 = {1,2,3};
        int k = 2;
        System.out.println(kSmallestPairs(nums1, num2, k));
    }
}
