package recovery;

import Bloomberg.TreeNode;

public class InsertIntoBinarySearchTree {
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }
        TreeNode prev = null;
        TreeNode cur = root;
        while (cur != null) {
            prev = cur;
            if (val < cur.val) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        if (val < prev.val) prev.left = new TreeNode(val);
        if (val > prev.val) prev.right = new TreeNode(val);
        return root;
    }
}
