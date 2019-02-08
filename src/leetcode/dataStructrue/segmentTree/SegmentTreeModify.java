package leetcode.dataStructrue.segmentTree;

public class SegmentTreeModify {
    public void modify(SegmentTreeNode root, int index, int value) {
        if (root.start == index && root.end == index) {
            root.max = value;
            return;
        }
        int mid = root.start + (root.end - root.start)/2;
        if (index <= mid && index >= root.start) {
            modify(root.left, index, value);
        } else if (index > mid && index <= root.end){
            modify(root.right, index, value);
        }
        root.max = Math.max(root.left.max, root.right.max);
    }
}
