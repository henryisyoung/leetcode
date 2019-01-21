package leetcode;

import apple.laf.JRSUIUtils;

public class Question104_Binary_Tree {
    public int maxDepth(TreeNode root) {
        if(root == null) return 0;
        int left = maxDepth(root.left);
        int right = maxDepth(root.right);
        return Math.max(left, right) + 1;
    }

    int depth = 0;
    public int maxDepthTrasvers(TreeNode root){
        depthHelper(root, 1);
        return depth;
    }

    private void depthHelper(TreeNode root, int curDepth) {
        if(root == null) return;
        if (curDepth > depth) depth = curDepth;
        depthHelper(root.left, curDepth + 1);
        depthHelper(root.right, curDepth + 1);
    }
}
