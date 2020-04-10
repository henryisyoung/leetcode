package facebook;

import Bloomberg.TreeNode;

public class _BinaryTreeMaximumPathSum {
    class Result{
        int singlePath, maxPath;
        public Result(int singlePath, int maxPath) {
            this.singlePath = singlePath;
            this.maxPath = maxPath;
        }
    }

    public int maxPathSum(TreeNode root) {
        return helper(root).maxPath;
    }

    private Result helper(TreeNode root) {
        if (root == null) return new Result(0, Integer.MIN_VALUE);
        Result left = helper(root.left);
        Result right = helper(root.right);
        int singlePath = Math.max(0, Math.max(left.singlePath, right.singlePath) + root.val);

        int maxPath = Math.max(Math.max(left.maxPath, right.maxPath), left.singlePath + right.singlePath + root.val);

        return new Result(singlePath, maxPath);
    }
}