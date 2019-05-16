package twitter;

import Bloomberg.TreeNode;

import java.util.*;

public class BinaryTreeRightSideView {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if (cur.left != null) {
                    queue.add(cur.left);
                }
                if (cur.right !=null) {
                    queue.add(cur.right);
                }
                if (i == size - 1) {
                    result.add(cur.val);
                }
            }
        }
        return result;
    }
    int max = 0;
    public List<Integer> rightSideViewRecrusion(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        rightSideViewRecrusionHelper(root, 1, result);
        return result;
    }

    private void rightSideViewRecrusionHelper(TreeNode root, int depth, List<Integer> result) {
        if (root == null) {
            return;
        }
        if (depth > max) {
            max = depth;
            result.add(root.val);
        }
        rightSideViewRecrusionHelper(root.right, depth + 1, result);
        rightSideViewRecrusionHelper(root.left, depth + 1, result);
    }
}
