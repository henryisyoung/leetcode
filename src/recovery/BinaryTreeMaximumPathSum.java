package recovery;

import Bloomberg.TreeNode;

public class BinaryTreeMaximumPathSum {
    public int maxPathSum(TreeNode root) {
        if (root == null) return 0;
        return maxPathHelper(root).maxSum;
    }

    static class Result {
        int maxSum, singleSum;

        public Result(int maxSum, int singleSum) {
            this.singleSum = singleSum;
            this.maxSum = maxSum;
        }
    }

    private Result maxPathHelper(TreeNode root) {
        if (root == null) {
            return new Result(Integer.MIN_VALUE, 0);
        }

        Result left = maxPathHelper(root.left);
        Result right = maxPathHelper(root.right);
        int single = Math.max(left.singleSum, right.singleSum) + root.val;
        single = Math.max(0, single);
        int max = Math.max(left.maxSum, right.maxSum);
        max = Math.max(left.singleSum + right.singleSum + root.val, max);
        return new Result(max, single);
    }
}
