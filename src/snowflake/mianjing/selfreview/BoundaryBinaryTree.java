package snowflake.mianjing.selfreview;

import Bloomberg.TreeNode;

import java.util.*;

public class BoundaryBinaryTree {
    public List<Integer> boundaryOfBinaryTree(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        if (isLeaf(root)) {
            return Arrays.asList(root.val);
        }

        result.add(root.val);

        addLeftBoundary(root.left, result);
        addleaves(root, result);
        List<Integer> list = new ArrayList<>();
        addRightBoundary(root.right, list);
        Collections.reverse(list);
        result.addAll(list);
        return result;
    }

    private void addRightBoundary(TreeNode root, List<Integer> result) {
        while (root != null && !isLeaf(root)) {
            result.add(root.val);
            if (root.right != null) {
                root = root.right;
            } else {
                root = root.left;
            }
        }
    }

    private void addleaves(TreeNode root, List<Integer> result) {
        if(root == null) return;

        if (isLeaf(root)) {
            result.add(root.val);
            return;
        }
        addleaves(root.left, result);
        addleaves(root.right, result);
    }

    private void addLeftBoundary(TreeNode root, List<Integer> result) {
        while (root != null && !isLeaf(root)) {
            result.add(root.val);
            if (root.left != null) {
                root = root.left;
            } else {
                root = root.right;
            }
        }
    }

    private boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }
}
