package leetcode.binaryTree;

import Bloomberg.TreeNode;

public class MaximumDepthOfBinaryTree {
    public int maxDepth(TreeNode root) {
        return depthHelper(root);
    }

    private int depthHelper(TreeNode root) {
        if(root == null) {
            return 0;
        }
        int left = depthHelper(root.left);
        int right = depthHelper(root.right);
        return 1 + Math.max(left, right);
    }
}
