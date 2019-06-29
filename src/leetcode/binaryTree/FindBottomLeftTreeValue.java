package leetcode.binaryTree;

import java.util.LinkedList;
import java.util.Queue;

public class FindBottomLeftTreeValue {
    public int findBottomLeftValueBFS(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int result = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if (i == 0) result = cur.val;
                if (cur.left != null) {
                    queue.add(cur.left);
                }
                if (cur.right != null) {
                    queue.add(cur.right);
                }
            }
        }
        return result;
    }
    int max = 0;
    int result;
    public int findBottomLeftValueDFS(TreeNode root) {
        result = root.val;
        dfsFindLeftMost(root, 1);
        return result;
    }

    private void dfsFindLeftMost(TreeNode root, int depth) {
        if (root == null) {
            return;
        }
        if (depth > max) {
            max = depth;
            result = root.val;
        }
        dfsFindLeftMost(root.left, depth + 1);
        dfsFindLeftMost(root.right, depth + 1);
    }
}
