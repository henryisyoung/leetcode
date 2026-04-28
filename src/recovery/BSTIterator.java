package recovery;

import Bloomberg.TreeNode;

import java.util.Stack;

public class BSTIterator {
    Stack<TreeNode> stack;
    TreeNode root;
    public BSTIterator(TreeNode root) {
        this.root = root;
        this.stack = new Stack<>();

        while (root != null) {
            stack.add(root);
            root = root.left;
        }
    }

    public int next() {
        TreeNode cur = stack.pop();
        TreeNode next = cur.right;
        while (next != null) {
            stack.add(next);
            next = next.left;
        }
        return cur.val;
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }
}
