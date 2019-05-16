package leetcode.binaryTree;

import java.util.ArrayList;
import java.util.List;

public class SumRootToLeafNumbers {
    public int sumNumbers(TreeNode root) {
        if (root == null) {
            return 0;
        }
        List<Integer> nums = new ArrayList<>();
        dfsSearchAll(nums, root, 0);
        int sum = 0;
        for (int i : nums) {
            sum += i;
        }
        return sum;
    }

    private void dfsSearchAll(List<Integer> nums, TreeNode root, int val) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            nums.add(val * 10 + root.val);
            return;
        }
        val = val * 10 + root.val;
        dfsSearchAll(nums, root.left,val);
        dfsSearchAll(nums, root.right,val);
    }
}
