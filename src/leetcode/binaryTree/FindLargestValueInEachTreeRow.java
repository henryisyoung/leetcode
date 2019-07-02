package leetcode.binaryTree;

import java.util.*;

public class FindLargestValueInEachTreeRow {
    public List<Integer> largestValuesBFS(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if(root == null) return result;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()) {
            int size = queue.size();
            int max = queue.peek().val;
            for(int i = 0; i < size; i++) {
                TreeNode c = queue.poll();
                max = Math.max(max, c.val);
                if(c.left != null) queue.add(c.left);
                if(c.right != null) queue.add(c.right);
            }
            result.add(max);
        }
        return result;
    }
    public List<Integer> largestValues(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if(root == null) return result;
        result.add(root.val);
        dfsSearchAll(result, root, 0);
        return result;
    }

    private void dfsSearchAll(List<Integer> result, TreeNode root, int lvl) {
        if (root == null) return;
        while (result.size() <= lvl) {
            result.add(Integer.MIN_VALUE);
        }
        if (root.val > result.get(lvl)) {
            result.set(lvl, root.val);
        }
        dfsSearchAll(result, root.left, lvl + 1);
        dfsSearchAll(result, root.right, lvl + 1);
    }
}
