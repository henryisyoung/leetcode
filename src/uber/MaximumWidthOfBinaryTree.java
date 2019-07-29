package uber;

import leetcode.solution.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

public class MaximumWidthOfBinaryTree {
    public int widthOfBinaryTree(TreeNode root) {
        if (root == null) return 0;
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(root, 0, 0));
        int left = 0, cur = 0, result = 0;
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.root != null) {
                TreeNode curNode = node.root;
                int depth = node.depth, pos = node.pos;
                queue.add(new Node(curNode.left, depth + 1, 2 * pos + 1));
                queue.add(new Node(curNode.right, depth + 1, 2 * pos + 2));
                if (cur != depth) {
                    cur = depth;
                    left = pos;
                }
                result = Math.max(result, pos - left + 1);
            }
        }
        return result;
    }
    class Node{
        TreeNode root;
        int pos, depth;
        public Node(TreeNode root, int depth, int pos) {
            this.depth = depth;
            this.pos = pos;
            this.root = root;
        }
    }
}
