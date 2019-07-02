package quora;

import Bloomberg.TreeNode;

public class PathSum {
    public boolean hasPathSum(TreeNode root, int sum) {
        if(root == null) {
            return sum == 0;
        }
        if (root.left == null && root.right == null ){
            return root.val == sum;
        }
        return hasPathSum(root.left, sum - root.val) || hasPathSum(root.right, sum - root.val);
    }
}
