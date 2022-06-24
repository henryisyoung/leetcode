package apple;

import Bloomberg.TreeNode;

import java.util.Stack;

public class RecoverBinarySearchTree {
    public void recoverTree(TreeNode root) {
        if (root == null) return;
        Stack<TreeNode> stack = new Stack<>();

        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        TreeNode first = null, second = null;
        TreeNode prev = null;
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            if (prev != null) {
                if (cur.val < prev.val) {
                    if (first == null) {
                        first = prev;
                    }
                    second = cur;
                }
            }
            prev = cur;
            TreeNode next = cur.right;
            while (next != null) {
                stack.push(next);
                next = next.left;
            }
        }
        int val = first.val;
        first.val = second.val;
        second.val = val;
        return;
    }
}
