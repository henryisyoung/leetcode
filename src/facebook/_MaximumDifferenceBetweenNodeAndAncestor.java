package facebook;

import Bloomberg.TreeNode;

public class _MaximumDifferenceBetweenNodeAndAncestor {
    int max = 0;

    public int maxAncestorDiff(TreeNode root) {
        helper(root, root.val, root.val);
        return max;
    }

    private void helper(TreeNode root, int low, int high) {
        if (root == null) return;
        max = Math.max(Math.max(Math.abs(root.val - low), Math.abs(high - root.val)), max);

        low = Math.min(low, root.val);
        high = Math.max(high, root.val);
        helper(root.left, low, high);
        helper(root.right, low, high);
    }
}
