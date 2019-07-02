package leetcode.dataStructrue.segmentTree;

import java.util.*;

public class RangeSumQueryMutable {
    SegmentTreeNode root;
    private class SegmentTreeNode {
        public int start, end;
        int sum;
        public SegmentTreeNode left, right;
        public SegmentTreeNode(int start, int end, int sum) {
            this.start = start;
            this.end = end;
            this.sum = sum;
            this.left = this.right = null;
        }
    }

    private int query(SegmentTreeNode root, int start, int end) {
        if(root.start == start && root.end == end){
            return root.sum;
        }
        int mid = root.start + (root.end - root.start)/2;
        if(end <= mid) {
            return query(root.left, start, end);
        } else if (start > mid) {
            return query(root.right, start, end);
        } else{
            return query(root.left, start, mid) + query(root.right, mid + 1, end);
        }
    }

    private SegmentTreeNode build(int[] a, int start, int end) {
        SegmentTreeNode root = new SegmentTreeNode(start, end, 0);
        if(start > end){
            return null;
        }
        else{
            if(start < end){
                int mid = start + (end - start)/2;
                root.left = build(a, start, mid);
                root.right = build(a, mid + 1, end);
                root.sum = root.left.sum + root.right.sum;
            }else{
                root.sum = a[start];
            }
        }
        return root;
    }

    public RangeSumQueryMutable(int[] nums) {
        this.root = build(nums, 0, nums.length - 1);
    }

    public void update(int i, int val) {
        modify(root, i, val);
    }

    private void modify(SegmentTreeNode root, int index, int val) {
        int start = root.start, end = root.end;
        if (start == index && end == index) {
            root.sum = val;
            return;
        }
        int mid = start + (end - start) / 2;
        if (index <= mid) {
            modify(root.left, index, val);
        } else {
            modify(root.right, index, val);
        }
        root.sum = root.left.sum + root.right.sum;
    }

    public int sumRange(int start, int end) {
        return query(root, start, end);
    }
}
