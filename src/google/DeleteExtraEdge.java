package google;

import leetcode.solution.TreeNode;

public class DeleteExtraEdge {
    public void deleteEdge(TreeNode root) {
        if(root == null) {
            return;
        }
        dfsDeleteEdge(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode dfsDeleteEdge(TreeNode root, int minValue, int maxValue) {
        if (root == null) {
            return null;
        }
        if (root.val <= minValue || root.val >= maxValue) {
            return null;
        }
        root.left = dfsDeleteEdge(root.left, minValue, root.val);
        root.right = dfsDeleteEdge(root.right, root.val, maxValue);
        return root;
    }
}
