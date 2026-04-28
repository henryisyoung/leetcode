package recovery;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryTreeInorderTraversal {
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.add(root);
            root = root.left;
        }

        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            result.add(cur.val);
            TreeNode next = cur.right;
            while (next != null) {
                stack.add(next);
                next = next.left;
            }
        }
        return result;
    }
}
