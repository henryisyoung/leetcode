package leetcode.dataStructrue.segmentTree;

import java.util.ArrayList;
import java.util.List;

public class IntervalMinimumNumber {
    class SegmentTreeNode {
        int start, end, min;
        SegmentTreeNode left, right;
        public SegmentTreeNode(int start, int end, int min) {
            this.start = start;
            this.min = min;
            this.end = end;
        }
    }

    public List<Integer> intervalMinNumber(int[] A, List<Interval> queries) {
        // write your code here
        List<Integer> result = new ArrayList<>();
        if (A == null || A.length == 0) return result;
        SegmentTreeNode root = build(A, 0, A.length - 1);
        for (Interval q : queries) {
            result.add(queryTree(root, q.start, q.end));
        }
        return result;
    }

    private int queryTree(SegmentTreeNode root, int s, int e) {
        if (s > e) return 0;
        int start = root.start, end = root.end;
        if (start == s && end == e) return root.min;
        int mid = start + (end - start) / 2;
        if (e <= mid) {
            return queryTree(root.left, s, e);
        } else if (s > mid) {
            return queryTree(root.right, s, e);
        } else {
            return Math.min(queryTree(root.left, s, mid), queryTree(root.right, mid + 1, e));
        }
    }

    private SegmentTreeNode build(int[] A, int start, int end) {
        if (start > end) return null;
        if (start == end) return new SegmentTreeNode(start, end, A[start]);
        int mid = start + (end - start) / 2;
        SegmentTreeNode left = build(A, start, mid);
        SegmentTreeNode right = build(A, mid + 1, end);
        SegmentTreeNode root = new SegmentTreeNode(start, end, Math.min(left.min, right.min));
        root.left = left;
        root.right = right;
        return root;
    }

    public static void main(String[] args) {
        int[] A = {1,2,7,8,5};
        //[(1,2),(0,4),(2,4)]
        List<Interval> queries = new ArrayList<>();
        queries.add(new Interval(1,2));
        queries.add(new Interval(0,4));
        queries.add(new Interval(2,4));
        IntervalMinimumNumber solver = new IntervalMinimumNumber();
        List<Integer> result = solver.intervalMinNumber(A, queries);
        System.out.println(result.toString());
    }
}
