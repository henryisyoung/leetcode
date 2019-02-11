package leetcode.dataStructrue.stack;

import leetcode.TreeNode;

import java.util.Stack;

public class MaxTree {
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        Stack<TreeNode> stack = new Stack<>();
        int n = nums.length, index = 0;
        TreeNode[] nodes = new TreeNode[n];
        for (int i : nums) {
            nodes[index++] = new TreeNode(i);
        }
        int max = Integer.MIN_VALUE;
        TreeNode root = null;
        for (TreeNode node : nodes) {
            if (node.val > max) {
                max = node.val;
                root = node;
            }
            while (!stack.isEmpty() && stack.peek().val < node.val) {
                TreeNode cur = stack.pop();
                if (!stack.isEmpty() && stack.peek().val < node.val) {
                    stack.peek().right = cur;
                } else {
                    node.left = cur;
                }
            }
            stack.push(node);
        }

        while (!stack.isEmpty()){
            TreeNode cur = stack.pop();
            if (!stack.isEmpty()) {
                stack.peek().right = cur;
            }
        }

        return root;
    }
}
