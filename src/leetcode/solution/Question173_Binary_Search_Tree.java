package leetcode.solution;

import java.util.Stack;

public class Question173_Binary_Search_Tree {
    TreeNode root;
    Stack<TreeNode> stack;
    public Question173_Binary_Search_Tree(TreeNode root) {
        this.root = root;
        stack = new Stack<>();
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return root != null || !stack.isEmpty();
    }

    /** @return the next smallest number */
    public int next() {
        while(root != null){
            stack.push(root);
            root = root.left;
        }
        TreeNode p = stack.pop();
        root = p.right;
        return p.val;
    }
}
