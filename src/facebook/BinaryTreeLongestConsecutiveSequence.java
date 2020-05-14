package facebook;

import Bloomberg.TreeNode;

public class BinaryTreeLongestConsecutiveSequence {
    int max = 0;
    public int longestConsecutive(TreeNode root) {
        if (root == null) return 0;
        helper(root, 1);
        return max;
    }

    private void helper(TreeNode root, int len) {
        max = Math.max(len, max);
        if (root == null) return;
        if (root.left != null) {
            if (root.left.val - 1 == root.val) {
                helper(root.left, len + 1);
            } else {
                helper(root.left, 1);
            }
        }
        if (root.right != null) {
            if (root.right.val - 1 == root.val) {
                helper(root.right, len + 1);
            } else {
                helper(root.right, 1);
            }
        }
    }
}
