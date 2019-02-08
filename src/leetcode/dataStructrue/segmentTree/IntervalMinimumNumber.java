package leetcode.dataStructrue.segmentTree;

import java.util.ArrayList;
import java.util.List;

public class IntervalMinimumNumber {
    private class SegmentTreeNode {
        public int start, end, min;
        public SegmentTreeNode left, right;
        public SegmentTreeNode(int start, int end, int min) {
            this.start = start;
            this.end = end;
            this.min = min;
            this.left = this.right = null;
        }
    }

        public ArrayList<Integer> intervalMinNumber(int[] A,
        ArrayList<Interval> queries) {
            ArrayList<Integer> rlt = new ArrayList<Integer>();
            if(A == null || A.length == 0 || queries == null){
                return rlt;
            }
            SegmentTreeNode root = build(A, 0, A.length - 1);
            for(Interval in : queries){
                rlt.add(query(root, in.start, in.end));
            }
            return rlt;
        }

        private int query(SegmentTreeNode root, int start, int end) {
            if(start > end){
                return 0;
            }
            if(start == root.start && root.end == end){
                return root.min;
            }

            int leftMin = Integer.MAX_VALUE, rightMin = Integer.MAX_VALUE;
            int left = root.start, right = root.end, mid = left + (right - left)/2;
            if(start <= mid){
                if(end > mid){
                    leftMin = query(root.left, start, mid);
                }else{
                    leftMin = query(root.left, start, end);
                }
            }
            if(end > mid){
                if(start > mid){
                    rightMin = query(root.right, start, end);
                }else{
                    rightMin = query(root.right, mid + 1, end);
                }
            }
            return Math.min(leftMin, rightMin);
        }

        private SegmentTreeNode build(int[] A, int start, int end) {
            SegmentTreeNode root = new SegmentTreeNode(start, end, Integer.MAX_VALUE);
            if(start > end){
                return null;
            }
            else{
                if(start < end){
                    int mid = start + (end - start)/2;
                    root.left = build(A, start, mid);
                    root.right = build(A, mid + 1, end);
                    root.min = Math.min(root.left.min, root.right.min);
                }else{
                    root.min = A[start];
                }
            }
            return root;
        }
}
