package leetcode.iterator;

import leetcode.TreeNode;

import java.util.Stack;

public class BinarySearchTreeIterator {
    Stack<TreeNode> stack;
    TreeNode root;
    public BinarySearchTreeIterator(TreeNode root) {
        this.root = root;
        this.stack = new Stack<>();
    }

    /** @return the next smallest number */
    public int next() {
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        TreeNode now = stack.pop();
        root = now.right;
        return now.val;
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return root != null || !stack.isEmpty();
    }
}
