package leetcode.dataStructrue.segmentTree;

public class SegmentTreeBuild {
    public SegmentTreeNode build(int start, int end) {
        if (start > end) {
            return null;
        }
        if (start == end) {
            return new SegmentTreeNode(start, end);
        }
        SegmentTreeNode root = new SegmentTreeNode(start, end);
        int mid = start + (end - start)/2;
        root.left = build(start, mid);
        root.right = build(mid + 1, end);

        return root;
    }
}
