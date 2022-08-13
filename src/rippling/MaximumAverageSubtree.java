package rippling;

import Bloomberg.TreeNode;

public class MaximumAverageSubtree {
    double max = 0;
    public double maximumAverageSubtree(TreeNode root) {
        dfsFindAllNodes(root);
        return max;
    }

    private double[] dfsFindAllNodes(TreeNode root) {
        if (root == null) {
            return new double[]{0, 0};
        }
        double count = 1;
        double[] left = dfsFindAllNodes(root.left);
        double[] right = dfsFindAllNodes(root.right);

        count += left[1] + right[1];
        double sum = left[0] + right[0] + root.val;
        max = Math.max(max, sum / count);
        return new double[]{sum, count};
    }
}
