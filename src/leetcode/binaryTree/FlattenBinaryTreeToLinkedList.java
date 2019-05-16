package leetcode.binaryTree;

public class FlattenBinaryTreeToLinkedList {
    TreeNode prev;
    public void flatten(TreeNode root) {
        if (root == null){
            return;
        }
        TreeNode right = root.right;
        if(prev != null){
            prev.left = null;
            prev.right = root;
        }

        prev = root;
        flatten(root.left);
        flatten(right);
    }
}
