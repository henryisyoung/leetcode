package snowflake;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BoundaryBinaryTree {
    public List<Integer> boundaryOfBinaryTree(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        if (!isLeaf(root)) result.add(root.val);
        TreeNode cur = root.left;
        while (cur != null) {
            if (!isLeaf(cur)) {
                result.add(cur.val);
            }
            if (cur.left != null) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        findAllLeaves(root, result);
        Stack<Integer> stack = new Stack<>();
        cur = root.right;
        while (cur != null) {
            if (!isLeaf(cur)) {
                stack.add(cur.val);
            }
            if (cur.right != null) {
                cur = cur.right;
            } else {
                cur = cur.left;
            }
        }
        while (!stack.isEmpty()) result.add(stack.pop());
        return result;
    }

    private void findAllLeaves(TreeNode root, List<Integer> result) {
        if (isLeaf(root)) {
            result.add(root.val);
            return;
        }
        if (root.left != null) findAllLeaves(root.left, result);
        if (root.right != null) findAllLeaves(root.right, result);
    }

    private boolean isLeaf(TreeNode cur) {
        return cur.left == null && cur.right == null;
    }
}
