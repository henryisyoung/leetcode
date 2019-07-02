package quora;

import Bloomberg.TreeNode;

import java.util.*;

public class PathSumII {
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        dfsSearchAll(root, sum, result, new ArrayList<Integer>());
        return result;
    }

    private void dfsSearchAll(TreeNode root, int sum, List<List<Integer>> result, ArrayList<Integer> list) {
        if (root == null) return;
        if (root.left == null && root.right == null && root.val == sum) {
            list.add(sum);
            result.add(new ArrayList<>(list));
            list.remove(list.size() - 1);
            return;
        }
        list.add(root.val);
        dfsSearchAll(root.left, sum - root.val, result, list);
        dfsSearchAll(root.right, sum - root.val, result, list);
        list.remove(list.size() - 1);
    }
}
