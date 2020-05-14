package facebook;

import Bloomberg.TreeNode;

public class MaxSubBST {
    int max;

    public int largestBSTSubtree(TreeNode root) {
        if (root == null) return 0;
        max = 0;
        helper(root);
        return max;
    }

    private void helper(TreeNode root) {
        if (root == null) return;

        int d = isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (d != -1) max = Math.max(max, d);
        helper(root.left);
        helper(root.right);
    }

    private int isBST(TreeNode node, int low, int high) {
        if (node == null) return 0;
        if (node.val <= low || node.val >= high) return -1;
        int left = isBST(node.left, low, node.val);
        if (left == -1) return -1;
        int right = isBST(node.right, node.val, high);
        if (right == -1) return -1;
        return left + 1 + right;
    }

    class Node {
        int min, max, count;
        boolean isBST;
        public Node(boolean isBST, int count, int min, int max) {
            this.count = count;
            this.isBST = isBST;
            this.max = max;
            this.min = min;
        }
    }

    public int largestBSTSubtree2(TreeNode root) {
        if (root == null) return 0;
        return helper2(root).count;
    }

    private Node helper2(TreeNode root) {
        if (root == null) return new Node(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE);
        Node left = helper2(root.left), right = helper2(root.right);
        if (left.isBST && right.isBST && left.max < root.val && right.min > root.val) {
            return new Node(true, left.count + right.count + 1, Math.min(left.min, root.val),Math.max( right.max, root.val));
        }
        return new Node(false, Math.max(left.count, right.count), root.val, root.val);
    }


}