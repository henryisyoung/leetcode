package leetcode.binaryTree;


import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryTreePreorderTraversal2 {
    // divide and conquer
    public List<Integer> preorderTraversal(TreeNode root) {
        return dcPreorder(root);
    }

    private List<Integer> dcPreorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return null;
        }
        List<Integer> left = dcPreorder(root.left);
        List<Integer> right = dcPreorder(root.right);
        result.add(root.val);
        if (left.size() != 0) {
            result.addAll(left);
        }
        if (right.size() != 0) {
            result.addAll(right);
        }
        return result;
    }

    public List<Integer> preorderTraversal2(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        dfsPreorder(root, result);
        return result;
    }

    private void dfsPreorder(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        result.add(root.val);
        dfsPreorder(root.left, result);
        dfsPreorder(root.right, result);
    }

    public List<Integer> preorderTraversal3(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
        return result;
    }
}