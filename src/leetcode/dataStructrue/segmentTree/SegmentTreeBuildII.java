package leetcode.dataStructrue.segmentTree;

public class SegmentTreeBuildII {
    public SegmentTreeNode build(int[] A) {
        return buildMaxTree(0, A.length - 1, A);
    }

    private SegmentTreeNode buildMaxTree(int start, int end, int[] A) {
        if (start > end) {
            return null;
        }
        if (start == end) {
            return new SegmentTreeNode(start, end, A[start]);
        }
        int mid = start + (end - start)/2;
        SegmentTreeNode root = new SegmentTreeNode(start, end, A[start]);
        SegmentTreeNode left = buildMaxTree(start, mid, A);
        SegmentTreeNode right = buildMaxTree(mid + 1, end, A);
        root.left = left;
        root.right = right;
        if (left != null && left.max > root.max) {
            root.max = left.max;
        }
        if (right != null && right.max > root.max) {
            root.max = right.max;
        }
        return root;
    }
}
