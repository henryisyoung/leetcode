package leetcode.binaryTree;

import java.util.Stack;

public class KthSmallestElementInBST {
    public int kthSmallest(TreeNode root, int k) {
        if (root == null) {
            return 0;
        }
        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        int count = 0;
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            count++;
            if (count == k) {
                return cur.val;
            }
            TreeNode n = cur.right;
            while (n != null) {
                stack.push(n);
                n = n.left;
            }
        }
        return 0;
    }
}
