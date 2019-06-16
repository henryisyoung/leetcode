package leetcode.dataStructrue.stack;

import leetcode.solution.TreeNode;

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

    public TreeNode constructMaximumBinaryTree2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        int n = nums.length;
        Stack<TreeNode> stack = new Stack<>();
        for (int i = 0; i <= n; i++) {
            int v = i == n ? Integer.MAX_VALUE : nums[i];
            TreeNode root = new TreeNode(v);
            while (!stack.isEmpty() && v > stack.peek().val) {
                TreeNode node = stack.pop();
                if (stack.isEmpty()) {
                    root.left = node;
                } else {
                    if (v > stack.peek().val) {
                        stack.peek().right = node;
                    } else {
                        root.left = node;
                    }
                }
            }
            stack.push(root);
        }
        return stack.pop().left;
    }
}
