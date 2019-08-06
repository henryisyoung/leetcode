package snap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class KSpacedArraySorting {
    class Node{
        int index, val;
        public Node(int index, int val) {
            this.index = index;
            this.val = val;
        }
    }
    public int[] getSortedArray(int[] arr, int k) {
        if (arr == null || arr.length < k) return arr;
        int n = arr.length;
        PriorityQueue<Node> pq = new PriorityQueue<>(k, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.val - o2.val;
            }
        });
        for (int i = 0; i < k; i++) {
            pq.add(new Node(i, arr[i]));
        }
        int[] result = new int[n];
        int i = 0;
        while (!pq.isEmpty()) {
            Node node = pq.poll();
            int index = node.index;
            result[i++] = node.val;
            if (index + k >= n) continue;
            pq.add(new Node(index + k, arr[index + k]));
        }

        return result;
    }

    public static void main(String[] args) {
        int[] arr = {4,0,5,3,10};
        int k = 2;
        KSpacedArraySorting solver = new KSpacedArraySorting();
        int[] result = solver.getSortedArray(arr, k);
        System.out.println(Arrays.toString(result));
    }
}
