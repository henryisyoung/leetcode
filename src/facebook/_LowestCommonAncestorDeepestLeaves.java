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
        return helper(root).treeNode;
    }

    public Node helper(TreeNode root) {
        if (root == null) return new Node(-1, null);
        Node left = helper(root.left);
        Node right = helper(root.right);
        if (left.d > right.d) {
            return new Node(left.d + 1, left.treeNode);
        } else if (left.d < right.d) {
            return new Node(right.d + 1, right.treeNode);
        }
        return new Node(right.d + 1, root);
    }

    public TreeNode lcaDeepestLeaves2(TreeNode root) {
        if (root == null) return null;
        int left = height(root.left);
        int right = height(root.right);
        if (left > right) {
            return lcaDeepestLeaves2(root.left);
        } else if (left < right) {
            return lcaDeepestLeaves2(root.right);
        }
        return root;
    }

    private int height(TreeNode node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }
}