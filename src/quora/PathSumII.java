package quora;

import Bloomberg.TreeNode;

import java.util.*;

public class PathSumII {
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        dfsSearchAll(result, sum, root, new ArrayList<>());
        return result;
    }

    private void dfsSearchAll(List<List<Integer>> result, int sum, TreeNode root, ArrayList<Integer> list) {
        if(root == null) return;
        if (root.left == null && root.right == null && root.val == sum) {
            List<Integer> copy = new ArrayList<>(list);
            copy.add(root.val);
            result.add(copy);
            return;
        }
        list.add(root.val);
        dfsSearchAll(result, sum - root.val,root.left,  list);
        dfsSearchAll(result, sum - root.val,root.right,  list);
        list.remove(list.size() - 1);
    }
}
