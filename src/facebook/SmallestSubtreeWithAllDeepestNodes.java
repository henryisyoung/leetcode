package facebook;


import java.util.*;

public class SmallestSubtreeWithAllDeepestNodes {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    int level = 0;
    Map<TreeNode, Integer> depth = new HashMap<>();

    public TreeNode subtreeWithAllDeepest(TreeNode root) {
        if (root == null) return root;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        dfsFindAll(root, 0);
        return findAllNode(root);
    }

    private void dfsFindAll(TreeNode root, int cur) {
        if (root == null) return;
        depth.put(root, cur);
        level = Math.max(level, cur);
        dfsFindAll(root.left, cur + 1);
        dfsFindAll(root.right, cur + 1);
    }

    private TreeNode findAllNode(TreeNode root) {
        if (root == null || depth.get(root) == level) {
            return root;
        }
        TreeNode left = findAllNode(root.left);
        TreeNode right = findAllNode(root.right);
        if (left != null && right != null) return root;
        if (left != null) return left;
        if (right != null) return right;
        return null;
    }

    public TreeNode subtreeWithAllDeepest2(TreeNode root) {
        return findAllNodeHelper(root).node;
    }

    private Result findAllNodeHelper(TreeNode root) {
        if (root == null) return new Result(0, root);
        Result left = findAllNodeHelper(root.left), right = findAllNodeHelper(root.right);
        if (left.depth > right.depth) return new Result(left.depth + 1, left.node);
        if (left.depth < right.depth) return new Result(right.depth + 1, right.node);
        return new Result(right.depth, root);
    }

    class Result {
        int depth;
        TreeNode node;

        public Result(int depth, TreeNode node) {
            this.depth = depth;
            this.node = node;
        }
    }

}