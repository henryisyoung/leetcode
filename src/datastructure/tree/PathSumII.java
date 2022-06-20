package datastructure.tree;

import java.util.ArrayList;
import java.util.List;

public class PathSumII {
    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        helper(root, targetSum, result, new ArrayList<>());

        return result;
    }

    private void helper(TreeNode root, int targetSum, List<List<Integer>> result, ArrayList<Integer> list) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            if (root.val == targetSum) {
                list.add(root.val);
                result.add(new ArrayList<>(list));
                list.remove(list.size() - 1);
            }
            return;
        }

        list.add(root.val);
        helper(root.left, targetSum - root.val, result, list);
        helper(root.right, targetSum - root.val, result, list);
        list.remove(list.size() - 1);
    }
}
