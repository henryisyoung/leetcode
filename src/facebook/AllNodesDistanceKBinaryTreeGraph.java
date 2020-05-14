package facebook;

import Bloomberg.TreeNode;

import java.util.*;
import java.util.List;

public class AllNodesDistanceKBinaryTreeGraph {
    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        Map<TreeNode, List<TreeNode>> graph = new HashMap<>();

        dfsBuildGrapg(root, graph);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(target);
        Set<TreeNode> visited = new HashSet<>();
        visited.add(target);

        while (!queue.isEmpty()) {
            int size = queue.size();
            if (K == 0) {
                return buildList(queue);
            }
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if (graph.containsKey(cur)) {
                    for (TreeNode next : graph.get(cur)) {
                        if (visited.contains(next)) continue;
                        queue.add(next);
                        visited.add(next);
                    }
                }
            }
            K--;
        }
        return new ArrayList<>();
    }

    private List<Integer> buildList(Queue<TreeNode> queue) {
        List<Integer> list = new ArrayList<>();
        while (!queue.isEmpty()) {
            TreeNode cur = queue.poll();
            if (cur == null) continue;
            list.add(cur.val);
        }
        return list;
    }

    private void dfsBuildGrapg(TreeNode root, Map<TreeNode, List<TreeNode>> graph) {
        if (root == null) return;
        TreeNode left = root.left, right = root.right;
        graph.putIfAbsent(root, new ArrayList<>());
        graph.putIfAbsent(left, new ArrayList<>());
        graph.putIfAbsent(right, new ArrayList<>());
        if (left != null) {
            graph.get(root).add(left);
            graph.get(left).add(root);
        }
        if (right != null) {
            graph.get(root).add(right);
            graph.get(right).add(root);
        }
        dfsBuildGrapg(root.left, graph);
        dfsBuildGrapg(root.right, graph);
    }
}
