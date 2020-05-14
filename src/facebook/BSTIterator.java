package facebook;

import Bloomberg.TreeNode;

import java.util.Stack;

public class BSTIterator {
    Stack<TreeNode> stack;
    TreeNode root;
    public BSTIterator(TreeNode root) {
        this.stack = new Stack<>();
        this.root = root;
    }

    /** @return the next smallest number */
    public int next() {
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        TreeNode n = stack.pop();
        root = n.right;
        return n.val;
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return !stack.isEmpty() || root != null;
    }
}
