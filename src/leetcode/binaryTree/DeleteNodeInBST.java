package leetcode.binaryTree;

public class DeleteNodeInBST {
    public TreeNode deleteNode(TreeNode root, int key) {
        if(root == null){
            return root;
        }
        if(root.val > key){
            root.left = deleteNode(root.left, key);
        } else if (root.val < key) {
            root.right = deleteNode(root.right, key);
        } else {
            if(root.left == null){
                root = root.right;
            } else if (root.right == null) {
                root = root.left;
            } else {
                root.val = findLeftMost(root.right);
                root.right = deleteNode(root.right, root.val);
            }
        }
        return root;
    }

    private int findLeftMost(TreeNode node) {
        while (node.left != null){
            node = node.left;
        }
        return node.val;
    }
}
