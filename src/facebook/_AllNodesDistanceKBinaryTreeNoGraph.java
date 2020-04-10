package facebook;

import Bloomberg.TreeNode;

import java.util.*;

public class _AllNodesDistanceKBinaryTreeNoGraph {
    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        List<Integer> result = new ArrayList<>();
        helper(root, target, K, result);
        return result;
    }

    private int helper(TreeNode node, TreeNode target, int k, List<Integer> result) {
        if (node == null) return -1;
        if (target == node) {
            findBelow(result, target, 0, k);
            return 1;
        } else {
            int left = helper(node.left, target, k, result);
            int right = helper(node.right, target, k, result);
            if (left != -1) {
                if (left == k) {
                    result.add(node.val);
                }
                findBelow(result, node.right, left + 1, k);
                return left + 1;
            } else if (right != -1) {
                if (right == k) {
                    result.add(node.val);
                }
                findBelow(result, node.left, right + 1, k);
                return right + 1;
            } else {
                return -1;
            }
        }
    }

    private void findBelow(List<Integer> result, TreeNode node, int level, int k) {
        if (node == null) return;
        if (level == k) {
            result.add(node.val);
            return;
        }
        findBelow(result, node.left, level + 1, k);
        findBelow(result, node.right, level + 1, k);
    }
}
