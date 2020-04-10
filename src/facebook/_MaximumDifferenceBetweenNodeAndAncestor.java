package facebook;

import Bloomberg.TreeNode;

public class _MaximumDifferenceBetweenNodeAndAncestor {
    public int maxAncestorDiff(TreeNode root) {
        return calculate(root, root.val, root.val);
    }

    private int calculate(TreeNode root, int min, int max) {
        if (root == null) {
            return Math.abs(max - min);
        }
        max = Math.max(max, root.val);
        min = Math.min(min, root.val);

        return Math.max(calculate(root.left, min, max), calculate(root.right, min, max));
    }
}
