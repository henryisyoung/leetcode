package google.vo;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class StepByStepDirectionsFromBinaryTreeAnother {

    public String getDirections(TreeNode root, int startValue, int destValue) {
        if (root == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        TreeNode lca = findLCA(root, startValue, destValue);
        List<String> startPath = new ArrayList<>();
        findPath(lca, startPath, startValue);
        List<String> destPath = new ArrayList<>();
        findPath(lca, destPath, destValue);

        for (String i : startPath) {
            sb.append("U");
        }
        for (int i = destPath.size() - 1; i >= 0; i--) {
            sb.append(destPath.get(i));
        }
        return sb.toString();
    }

    private TreeNode findPath(TreeNode root, List<String> path, int val) {
        if (root == null || root.val == val) {
            return root;
        }
        TreeNode left = findPath(root.left, path, val);
        TreeNode right = findPath(root.right, path, val);
        if (left != null) {
            path.add("L");
        } else if (right != null){
            path.add("R");
        }
        return left == null ? right : left;
    }

    private TreeNode findLCA(TreeNode root, int p, int q) {
        if (root == null || root.val == p || root.val == q) {
            return root;
        }
        TreeNode left = findLCA(root.left, p, q);
        TreeNode right = findLCA(root.right, p, q);
        if (left != null && right != null) return root;
        if (left != null) return left;
        if (right != null) return right;

        return null;
    }
}
