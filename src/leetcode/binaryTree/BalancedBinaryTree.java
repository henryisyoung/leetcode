package leetcode.binaryTree;

import Bloomberg.TreeNode;

public class BalancedBinaryTree {
    public boolean isBalanced(TreeNode root) {
        return balancedTree(root) != -1;
    }

    private int balancedTree(TreeNode root) {
        if(root == null){
            return 0;
        }
        int left = balancedTree(root.left);
        int right = balancedTree(root.right);
        if(left == -1 || right == -1 || Math.abs(left - right) > 1){
            return -1;
        }
        return 1 + Math.max(left, right);
    }
    public boolean isBalanced2(TreeNode root){
        return heightTree(root) != -1;
    }

    private int heightTree(TreeNode root) {
        if(root == null) {
            return 0;
        }
        int left = heightTree(root.left);
        int right = heightTree(root.right);
        if (left == -1 || right == -1 || Math.abs(left - right) > 1) {
            return -1;
        }
        return 1 + Math.max(left, right);
    }

}
