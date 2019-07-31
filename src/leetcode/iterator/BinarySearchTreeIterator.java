package leetcode.iterator;

import leetcode.solution.TreeNode;

import java.util.Stack;

public class BinarySearchTreeIterator {
    Stack<TreeNode> stack;
    TreeNode root;
    public BinarySearchTreeIterator(TreeNode root) {
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
        int val = n.val;
        root = n.right;
        return val;
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return !stack.isEmpty() || root != null;
    }
}
