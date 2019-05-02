package leetcode.binaryTree;

public class DeleteNodeInBST2 {
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) {
            return root;
        }
        if (root.val > key) {
            root.left = deleteNode(root.left, key);
        } else if (root.val < key) {
            root.right = deleteNode(root.right, key);
        } else {
            if (root.left == null) {
                root = root.right;
            } else if (root.right == null) {
                root = root.left;
            } else {
                int val = findLeftMax(root.left);
                root.val = val;
                root.left = deleteNode(root.left, val);
            }
        }

        return root;
    }

    private int findLeftMax(TreeNode node) {
        while (node.right != null) {
            node = node.right;
        }
        return node.val;
    }
}
