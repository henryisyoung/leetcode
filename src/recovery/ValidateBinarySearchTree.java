package recovery;

import Bloomberg.TreeNode;

public class ValidateBinarySearchTree {
    public boolean isValidBST(TreeNode root) {
        return helper(root, null, null);
    }

    private boolean helper(TreeNode root, Integer min, Integer max) {
        if(root == null) return true;
        if((min != null && min >= root.val) || (max != null && max <= root.val)) return false;
        return helper(root.left, min, root.val) && helper(root.right, root.val, max);
    }
}
