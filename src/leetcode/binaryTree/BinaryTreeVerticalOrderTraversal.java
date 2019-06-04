package leetcode.binaryTree;

import java.util.*;

public class BinaryTreeVerticalOrderTraversal {
    class Node {
        int col;
        TreeNode treeNode;
        public Node(TreeNode n, int col) {
            this.col = col;
            this.treeNode = n;
        }
    }
    public List<List<Integer>> verticalOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Map<Integer, List<Integer>> map = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(root, 0));
        int min = 0, max = 0;
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            int col = cur.col;
            if (!map.containsKey(col)) {
                map.put(col, new ArrayList<>());
            }
            map.get(col).add(cur.treeNode.val);
            if (cur.treeNode.left != null) {
                queue.add(new Node(cur.treeNode.left, col - 1));
                min = Math.min(col - 1, min);
            }
            if (cur.treeNode.right != null) {
                queue.add(new Node(cur.treeNode.right, col + 1));
                max = Math.max(col + 1, max);
            }
        }

        for (int i = min; i <= max; i++) {
            result.add(map.get(i));
        }
        return result;
    }
}
