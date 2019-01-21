package leetcode;

import java.util.*;

public class Question144_Binary_Tree {
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> list = new ArrayList<Integer>();
        preorder(root, list);
        return list;
    }

    private void preorder(TreeNode root, List<Integer> list) {
        if (root == null) return;
        list.add(root.val);
        preorder(root.left, list);
        preorder(root.right, list);
    }

    public List<Integer> preorderTraversalDC(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        if (root.left == null && root.right == null){
            result.add(root.val);
            return result;
        }
        result.add(root.val);
        result.addAll(preorderTraversalDC(root.left));
        result.addAll(preorderTraversalDC(root.right));
        return result;
    }

    public List<Integer> preorderTraversalStack(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if(root == null) return result;
        Stack<TreeNode> stack = new Stack<>();
        stack.add(root);
        while(!stack.isEmpty()){
            TreeNode tmp = stack.pop();
            result.add(tmp.val);
            if(tmp.right != null) stack.add(tmp.right);
            if(tmp.left != null) stack.add(tmp.left);
        }
        return result;
    }
}
