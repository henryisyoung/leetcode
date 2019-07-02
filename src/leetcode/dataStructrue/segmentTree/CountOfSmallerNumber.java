package leetcode.dataStructrue.segmentTree;

import java.util.*;

public class CountOfSmallerNumber {
    class SegmentTreeNode {
        int start, end, count;
        SegmentTreeNode left, right;
        public SegmentTreeNode(int start, int end, int count) {
            this.start = start;
            this.count = count;
            this.end = end;
        }
    }

    public List<Integer> countOfSmallerNumber(int[] A, int[] queries) {
        // write your code here
        List<Integer> result = new ArrayList<>();
        SegmentTreeNode root = buildSegmentTree(0, 1000);
        for (int i : A){
            update(root, i, 1);
        }
        for (int i : queries) {
            if (i > 0) {
                result.add(query(root, 0, i - 1));
            }
        }
        return result;
    }

    private int query(SegmentTreeNode root, int start, int end) {
        if (root.start == start && root.end == end) return root.count;
        int mid = root.start + (root.end - root.start) / 2;
        if (end <= mid) {
            return query(root.left, start, end);
        } else if (start > mid) {
            return query(root.right, start, end);
        } else {
            return query(root.left, start, mid) + query(root.right, mid + 1, end);
        }
    }

    private void update(SegmentTreeNode root, int index, int count) {
        if (root.start == index && root.end == index) {
            root.count += count;
            return;
        }
        int mid = root.start + (root.end - root.start) / 2;
        if (index <= mid) {
            update(root.left, index, count);
        } else {
            update(root.right, index, count);
        }
        root.count = root.left.count + root.right.count;
    }

    private SegmentTreeNode buildSegmentTree(int start, int end) {
        if (start > end) return null;
        if (start == end) return new SegmentTreeNode(start, end, 0);
        int mid = start + (end - start) / 2;
        SegmentTreeNode root = new SegmentTreeNode(start, end, 0);
        root.left = buildSegmentTree(start, mid);
        root.right = buildSegmentTree(mid + 1, end);
        root.count = root.left.count + root.right.count;
        return root;
    }

    public static void main(String[] args) {
        int[] array ={1,2,7,8,5}, queries ={1,8,5};
        CountOfSmallerNumber solver = new CountOfSmallerNumber();
        List<Integer> result = solver.countOfSmallerNumber(array, queries);
        System.out.println(result.toString());
    }
}
