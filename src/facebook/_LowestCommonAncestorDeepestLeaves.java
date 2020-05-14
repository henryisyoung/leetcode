package facebook;

import Bloomberg.TreeNode;

public class _LowestCommonAncestorDeepestLeaves {
    class Node {
        TreeNode treeNode;
        int d;

        Node(int x, TreeNode n) {
            d = x;
            treeNode = n;
        }
    }

    public TreeNode lcaDeepestLeaves(TreeNode root) {
        if (root == null) return root;
        return helper(root).treeNode;
    }

    private Node helper(TreeNode root) {
        if (root == null) return new Node(0, null);
        Node left = helper(root.left);
        Node right = helper(root.right);
        if (left.d == right.d) {
            return new Node(left.d + 1, root);
        } else if (left.d > right.d) {
            return new Node(left.d + 1, left.treeNode);
        } else {
            return new Node(right.d + 1, right.treeNode);
        }
    }
}