package snap;

import Bloomberg.TreeNode;

import java.util.*;

public class TwoSumIV {
    public boolean findTarget(TreeNode root, int k) {
        if (root == null) return false;
        Set<Integer> set = new HashSet<>();
        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            int diff = k - cur.val;
            if (set.contains(diff)) return true;
            set.add(cur.val);
            TreeNode n = cur.right;
            while (n != null) {
                stack.push(n);
                n = n.left;
            }
        }
        return false;
    }

    public boolean findTarget2(TreeNode root, int k) {
        if (root == null) return false;
        List<Integer> list = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            list.add(cur.val);
            TreeNode n = cur.right;
            while (n != null) {
                stack.push(n);
                n = n.left;
            }
        }
        int left = 0, right = list.size() - 1;
        while (left < right) {
            if (list.get(left) + list.get(right) == k) {
                return true;
            }
            else if (list.get(left) + list.get(right) < k) {
                left++;
            } else {
                right--;
            }
        }
        return false;
    }
}