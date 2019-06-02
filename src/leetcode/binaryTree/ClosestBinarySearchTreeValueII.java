package leetcode.binaryTree;

import java.util.*;

public class ClosestBinarySearchTreeValueII {
    public List<Integer> closestKValues(TreeNode root, double target, int k) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>() ;
        Queue<Integer> queue = new LinkedList<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            if (queue.size() < k) {
                queue.add(cur.val);
            } else {
                int val = queue.peek();
                if (Math.abs(val - target) > Math.abs(cur.val - target)) {
                    queue.poll();
                    queue.add(cur.val);
                }
            }
            TreeNode n = cur.right;
            while (n != null) {
                stack.push(n);
                n = n.left;
            }
        }
        result.addAll(queue);
        return result;
    }
}
