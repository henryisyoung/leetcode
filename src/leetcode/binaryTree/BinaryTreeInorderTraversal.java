package leetcode.binaryTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryTreeInorderTraversal {
    // divide and conquer
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        result.addAll(inorderTraversal(root.left));
        result.add(root.val);
        result.addAll(inorderTraversal(root.right));
        return result;
    }

    // no recursion
    public List<Integer> inorderTraversalNoRecursion(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }

        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            result.add(cur.val);
            if (cur.right != null) {
                TreeNode node = cur.right;
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }
        }
        return result;
    }

    public List<Integer> inorderTraversalRec(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(result, root);
        return result;
    }

    private void inorderHelper(List<Integer> result, TreeNode root) {
        if (root == null) {
            return;
        }
        inorderHelper(result, root.left);
        result.add(root.val);
        inorderHelper(result, root.right);
    }

    public List<Integer> inorderTraversalDC(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if (root == null) {
            return list;
        }
        List<Integer> left = inorderTraversalDC(root.left);
        list.addAll(left);
        left.add(root.val);
        List<Integer> right = inorderTraversalDC(root.right);

        list.addAll(right);
        return list;
    }
    public List<Integer> inorderTraversalWhile(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if (root == null) {
            return list;
        }
        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            list.add(cur.val);
            TreeNode next = cur.right;
            while (next != null) {
                stack.push(next);
                next = next.left;
            }
        }
        return list;
    }
}