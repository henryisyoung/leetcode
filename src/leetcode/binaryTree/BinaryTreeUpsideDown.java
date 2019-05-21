package leetcode.binaryTree;

public class BinaryTreeUpsideDown {
    TreeNode prev = null;
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        if (root == null) {
            return root;
        }
        upsideDownBinaryTreeHelper(root);
        return prev;
    }

    private TreeNode upsideDownBinaryTreeHelper(TreeNode root) {
        if (root == null) {
            return root;
        }
        if (root.left == null && root.right == null) {
            prev = root;
            return root;
        }
        TreeNode parent = upsideDownBinaryTreeHelper(root.left);
        parent.left = root.right;
        parent.right = root;
        root.left = null;
        root.right = null;
        return parent.right;
    }
}
