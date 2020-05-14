package facebook;

import Bloomberg.TreeNode;

public class BinaryTreeLongestConsecutiveSequenceII {
    int max = 0;
    public int longestConsecutive(TreeNode root) {
        if (root == null) return 0;
        finder(root);
        return max;
    }

    private int[] finder(TreeNode root) {
        if (root == null) {
            return new int[2];
        }
        int inr = 1, der = 1;
        if (root.left != null) {
            int[] left = finder(root.left);
            if (root.val - 1 == root.left.val) {
                inr = left[0] + 1;
            } else if (root.val + 1 == root.left.val) {
                der = left[1] + 1;
            }
        }
        if (root.right != null) {
            int[] right = finder(root.right);
            if (root.val - 1 == root.right.val ) {
                inr = Math.max(inr, right[0] + 1);
            } else if (root.val + 1 == root.right.val) {
                der = Math.max(der, right[1] + 1);
            }
        }
        max = Math.max(max, inr + der - 1);
        return new int[]{inr, der};
    }
}
