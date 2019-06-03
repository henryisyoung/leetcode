package leetcode.binaryTree;

public class BinaryTreeLongestConsecutiveSequence {
    int max = 1;
    public int longestConsecutive(TreeNode root) {
        if (root == null) {
            return 0;
        }
        dfsSearchAll(root, 1);
        return max;
    }

    private void dfsSearchAll(TreeNode root, int len) {
        if (root == null) {
            return;
        }
        if (len > max) {
            max = len;
        }
        if (root.left != null) {
            if (root.val + 1 == root.left.val) {
                dfsSearchAll(root.left, len + 1);
            } else {
                dfsSearchAll(root.left, 1);
            }
        }
        if (root.right != null) {
            if (root.val + 1 == root.right.val) {
                dfsSearchAll(root.right, len + 1);
            } else {
                dfsSearchAll(root.right, 1);
            }
        }
    }
}
