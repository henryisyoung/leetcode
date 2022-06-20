package datastructure.tree;

import java.util.Stack;

public class RecoverBinarySearchTree {
    public void recoverTree(TreeNode root) {
        if (root == null) return;
        Stack<TreeNode> stack = new Stack<>();
        TreeNode first= null, second =null;

        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        TreeNode prev = null;
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            if (prev != null && prev.val > cur.val) {
               if (first == null) {
                   first = prev;
               }
               second = cur;
            }
            prev = cur;
            TreeNode next = cur.right;
            while (next != null) {
                stack.push(next);
                next = next.left;
            }
        }

        int tmp = first.val;
        first.val = second.val;
        second.val = tmp;
    }

}
