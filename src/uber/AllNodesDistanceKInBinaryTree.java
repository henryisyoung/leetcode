package uber;

import Bloomberg.TreeNode;

import java.util.*;

public class AllNodesDistanceKInBinaryTree {
    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        List<Integer> result = new ArrayList<>();
        int dist = findDist(root, target);
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        if (K > dist) {
            dfsSearch(root, K - dist, list1);
        }
        System.out.println(list1.toString());
        if (K == dist * 2) {
            list1.remove((Integer) target.val);
        }
        dfsSearch(target, K, list2);
        result.addAll(list1);
        result.addAll(list2);
        return result;
    }

    private int findDist(TreeNode root, TreeNode target) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int dist = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur  = queue.poll();
                if (cur.val == target.val) {
                    return dist;
                }
                if (cur.left != null) queue.add(cur.left);
                if (cur.right != null) queue.add(cur.right);
            }
            dist++;
        }
        return dist;
    }

    private void dfsSearch(TreeNode root, int dist, List<Integer> list) {
        if (root == null) return;
        if (dist == 0) {
            list.add(root.val);
            return;
        }
        dfsSearch(root.left, dist - 1, list);
        dfsSearch(root.right, dist - 1, list);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        AllNodesDistanceKInBinaryTree solution = new AllNodesDistanceKInBinaryTree();
        List<Integer> result = solution.distanceK(root, root.left, 2);
    }
}
