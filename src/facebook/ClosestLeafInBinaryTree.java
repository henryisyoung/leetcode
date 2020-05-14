package facebook;

import Bloomberg.TreeNode;

import java.util.*;

public class ClosestLeafInBinaryTree {
    public int findClosestLeaf(TreeNode root, int k) {
        Map<TreeNode, List<TreeNode>> graph = new HashMap<>();
        Map<Integer, TreeNode> map = new HashMap<>();
        buildGraph(graph, map, root);
        TreeNode start = map.get(k);

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(start);
        Set<TreeNode> visited = new HashSet<>();
        visited.add(start);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if (cur.left == null && cur.right == null) return cur.val;
                for (TreeNode next : graph.get(cur)) {
                    if (visited.contains(next)) continue;
                    queue.add(next);
                    visited.add(next);
                }
            }
        }

        return -1;
    }

    private void buildGraph(Map<TreeNode, List<TreeNode>> graph, Map<Integer, TreeNode> map, TreeNode root) {
        if (root == null) return;
        map.put(root.val, root);
        if (root.left != null) {
            graph.putIfAbsent(root, new ArrayList<>());
            graph.get(root).add(root.left);
            graph.putIfAbsent(root.left, new ArrayList<>());
            graph.get(root.left).add(root);
            buildGraph(graph, map, root.left);
        }
        if (root.right != null) {
            graph.putIfAbsent(root, new ArrayList<>());
            graph.get(root).add(root.right);
            graph.putIfAbsent(root.right, new ArrayList<>());
            graph.get(root.right).add(root);
            buildGraph(graph, map, root.right);
        }
    }
}
