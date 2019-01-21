package leetcode.binaryTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryTreeInorderTraversal {
    // divide and conquer
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        result.addAll(inorderTraversal(root.left));
        result.add(root.val);
        result.addAll(inorderTraversal(root.right));
        return result;
    }

    // no recursion
    public List<Integer> inorderTraversalNoRecursion(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack = new Stack<>();
        while (root != null){
            stack.push(root);
            root = root.left;
        }

        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            result.add(cur.val);
            if(cur.right != null){
                TreeNode node = cur.right;
                while (node != null){
                    stack.push(node);
                    node = node.left;
                }
            }
        }

        return result;
    }
}
