package pinterest;

import leetcode.TreeNode;

public class BinaryTreeDiameter {
    int max;
    public int diameterOfBinaryTree(TreeNode root) {
        max = 0;
        depth(root);
        return max;
    }

    private int depth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = depth(root.left), right = depth(root.right);
        max = Math.max(max, left + right + 1);
        return Math.max(left, right) + 1;
    }
}
