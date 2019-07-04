package leetcode.dataStructrue.segmentTree;

import java.util.*;

public class CountOfSmallerNumbersAfterSelf {
    public List<Integer> countSmaller(int[] nums) {
        List<Integer> result = new ArrayList<>();
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        SegmentTreeNode root = buildSegmentTree(-1000, 1000);
        for (int i = nums.length - 1; i >= 0; i--) {
            int val = nums[i];
            result.add(query(root, -1000, val - 1));
            update(root, val, 1);
        }
        Collections.reverse(result);
        return result;
    }

    class SegmentTreeNode {
        int start, end, count;
        SegmentTreeNode left, right;
        public SegmentTreeNode(int start, int end, int count) {
            this.start = start;
            this.count = count;
            this.end = end;
        }
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
        int[] nums = {5,2,6,1};
        int[] nums2 = {-1};
        CountOfSmallerNumbersAfterSelf solver = new CountOfSmallerNumbersAfterSelf();
//        List<Integer> list = solver.countSmaller(nums);
        List<Integer> list = solver.countSmaller(nums2);
        System.out.println(list.toString());
    }
}
