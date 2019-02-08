package leetcode.dataStructrue.segmentTree;

public class SegmentTreeQuery {
    public int query(SegmentTreeNode root, int start, int end) {
        if (root.start == start && root.end == end) {
            return root.max;
        }
        int leftMax = Integer.MIN_VALUE, rightMax = Integer.MIN_VALUE;
        int mid = root.start + (root.end - root.start)/2;
        if (start <= mid) {
            if (mid < end) {
                leftMax = query(root.left, start, mid);
            } else {
                leftMax = query(root.left, start, end);
            }
        }
        if (mid < end) {
            if (start <= mid) {
                rightMax = query(root.right, mid + 1, end);
            } else {
                rightMax = query(root.right, start, end);
            }
        }
        return Math.max(leftMax, rightMax);
    }
}
