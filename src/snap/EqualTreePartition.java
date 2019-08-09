package snap;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class EqualTreePartition {
    public boolean checkEqualTree(TreeNode root) {
        if (root == null) return false;
        Stack<Integer> list = new Stack<>();
        calSum(root, list);
        int sum = list.pop();
        if (sum % 2 == 1) return false;
        for (int i : list) {
            if (i * 2 == sum) return true;
        }
        return false;
    }

    private int calSum(TreeNode root, Stack<Integer> list) {
        if (root == null) return 0;
        int val = calSum(root.left, list) + root.val + calSum(root.right, list);
        list.push(val);
        return val;
    }
}
