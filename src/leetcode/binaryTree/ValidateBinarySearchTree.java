package leetcode.binaryTree;

import Bloomberg.TreeNode;

import java.util.Stack;

public class ValidateBinarySearchTree {
    public boolean isValidBST(TreeNode root) {
        return validTreeHelper(root).isBst;
    }

    private MyResult validTreeHelper(TreeNode root) {
        if (root == null) return new MyResult(true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MyResult left = validTreeHelper(root.left);
        MyResult right = validTreeHelper(root.right);

        if (left.isBst == false || right.isBst == false) {
            return new MyResult(false, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        if ((root.left != null && left.maxValue >= root.val)
                || (root.right != null && right.minValue <= root.val)) {
            return new MyResult(false, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        return new MyResult(true, Math.max(right.maxValue, root.val), Math.min(left.minValue, root.val));
    }

    private class MyResult {
        boolean isBst;
        int maxValue;
        int minValue;

        public MyResult(boolean isBst, int maxValue, int minValue) {
            isBst = isBst;
            maxValue = maxValue;
            minValue = minValue;
        }
    }

    public boolean isValidBST2(TreeNode root){
        return bstHelper(root, null, null);
    }

    private boolean bstHelper(TreeNode root, Integer lower, Integer higher) {
        if (root == null) return true;
        if (lower != null && lower >= root.val) return false;
        if (higher != null && higher <= root.val) return false;
        boolean left = bstHelper(root.left, lower, root.val);
        boolean right = bstHelper(root.right, root.val, higher);
        if (!left || !right) return false;
        return true;
    }

    public boolean isValidInorder(TreeNode root){
        Stack<TreeNode> stack = new Stack();
        double inorder = - Double.MAX_VALUE;

        while (!stack.isEmpty() || root != null) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            // If next element in inorder traversal
            // is smaller than the previous one
            // that's not BST.
            if (root.val <= inorder) return false;
            inorder = root.val;
            root = root.right;
        }
        return true;
    }
}
