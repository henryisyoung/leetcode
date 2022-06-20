package datastructure.tree;

import java.util.Stack;

public class BSTIterator {
    Stack<TreeNode> stack;
    public BSTIterator(TreeNode root) {
        stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
    }

    public int next() {
        TreeNode cur = stack.pop();
        int  val = cur.val;
        TreeNode next = cur.right;
        while (next != null) {
            stack.push(next);
            next = next.left;
        }

        return val;
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }
}
