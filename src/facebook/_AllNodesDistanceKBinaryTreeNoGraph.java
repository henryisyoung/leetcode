package facebook;

import Bloomberg.TreeNode;

import java.util.*;

public class _AllNodesDistanceKBinaryTreeNoGraph {
    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        List<Integer> result = new ArrayList<>();

        dfsSearch(result, root, K, target);

        return result;
    }

    private int dfsSearch(List<Integer> result, TreeNode root, int k, TreeNode target) {
        if (root == null) return -1;
        if (root == target) {
            findBelow(target, k, result, 0);
            return 1;
        }
        int left = dfsSearch(result, root.left, k, target);
        int right = dfsSearch(result, root.right, k, target);
        if (left != -1) {
            if (left == k) {
                result.add(root.val);
            }
            if (left < k) {
                findBelow(root.right, k - left - 1, result, 0);
            }
            return left + 1;
        }
        if (right != -1) {
            if (right == k) {
                result.add(root.val);
            }
            if (right < k) {
                findBelow(root.left, k - right - 1, result, 0);
            }
            return right + 1;
        }
        return -1;
    }

    private void findBelow(TreeNode node, int k, List<Integer> result, int depth) {
        if (node == null) return;
        if (depth == k) {
            result.add(node.val);
        }
        findBelow(node.left, k, result, depth + 1);
        findBelow(node.right, k, result, depth + 1);
    }
}
