package leetcode.binaryTree;

public class InsertNodeIntoBinarySearchTree {
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }
        TreeNode cur = root;
        TreeNode last = cur;
        while (cur != null) {
            last = cur;
            if (val > cur.val) {
                cur = cur.right;
            } else {
                cur = cur.left;
            }
        }
        if (val > last.val) {
            last.right = new TreeNode(val);
        } else {
            last.left = new TreeNode(val);
        }
        return root;
    }

    public TreeNode insertIntoBST2(TreeNode root, int val) {
        if (root == null) {
            return root;
        }
        TreeNode cur = root, last = null;

        while (cur != null) {
            last = cur;
            if (cur.val > val) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        if (last.val > val) {
            last.left = new TreeNode(val);
        } else {
            last.right = new TreeNode(val);
        }

        return root;
    }
}
