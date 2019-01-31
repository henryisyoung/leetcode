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
    public int maxDepth2(TreeNode root) {
        return maxLevelHelper(root);
    }

    private int maxLevelHelper(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = maxLevelHelper(root.left);
        int right = maxLevelHelper(root.right);
        return Math.max(left, right) + 1;
    }
}
