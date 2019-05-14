package leetcode.binaryTree;

import java.util.*;

public class RecoverBinarySearchTree {
    // extra o(n) space
    public void recoverTree(TreeNode root) {
        if (root == null) {
            return;
        }
        List<TreeNode> nodes = new ArrayList<>();
        List<Integer> vals = new ArrayList<>();
        inorderTrasverse(root, nodes, vals);
        Collections.sort(vals);
        for (int i = 0; i < vals.size(); i++) {
            nodes.get(i).val = vals.get(i);
        }
    }

    private void inorderTrasverse(TreeNode root, List<TreeNode> nodes, List<Integer> vals) {
        if (root == null) {
            return;
        }
        inorderTrasverse(root.left, nodes, vals);
        nodes.add(root);
        vals.add(root.val);
        inorderTrasverse(root.right, nodes, vals);
    }

    // recusion extra o(n) space
    TreeNode prev = null, first = null, second = null;
    public void recoverTreeRecruision(TreeNode root) {
        if (root == null) {
            return;
        }
        inorderTrasverse2(root);
        int temp = first.val;
        first.val = second.val;
        second.val = temp;
    }

    private void inorderTrasverse2(TreeNode root) {
        if (root == null) {
            return;
        }
        inorderTrasverse2(root.left);
        if (prev == null) {
            prev = root;
        } else {
            if (prev.val > root.val) {
                if (first == null) {
                    first = prev;
                }
                second = root;
            }
            prev = root;
        }
        inorderTrasverse2(root.right);
    }

    // no extra space
    public void recoverTreeIteration(TreeNode root) {
        if (root == null) {
            return;
        }
        TreeNode prev = null, first = null, second = null;
        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            if (prev != null && prev.val > cur.val) {
                if (first == null) {
                    first = prev;
                }
                second = cur;
            }
            prev = cur;
            if (cur.right != null) {
                TreeNode node = cur.right;
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }
        }
        int temp = first.val;
        first.val = second.val;
        second.val = temp;
    }

}
