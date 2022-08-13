package google.vo;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class FindLeavesBinaryTree {
    public List<List<Integer>> findLeaves(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        findAll(root, result);
        return result;
    }

    private int findAll(TreeNode root, List<List<Integer>> result) {
        if (root == null) {
            return -1;
        }
        int left = findAll(root.left, result);
        int right = findAll(root.right, result);
        int level = Math.max(left, right) + 1;
        if (level >= result.size()) {
            result.add(new ArrayList<>());;
        }
        result.get(level).add(root.val);
        return level;
    }


}
