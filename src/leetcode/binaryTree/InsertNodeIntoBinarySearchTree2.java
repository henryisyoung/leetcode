package leetcode.binaryTree;

public class InsertNodeIntoBinarySearchTree2 {
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }
        TreeNode last = root, cur = root;
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
