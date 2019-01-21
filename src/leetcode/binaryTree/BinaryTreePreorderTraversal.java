package leetcode.binaryTree;


import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryTreePreorderTraversal {
    // divide and conquer
    public List<Integer> preorderTraversal(TreeNode root) {
        return trasverThree(root);
    }

    private List<Integer> trasverThree(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if(root == null){
            return list;
        }
        List<Integer> left = trasverThree(root.left);
        List<Integer> right = trasverThree(root.right);
        list.add(root.val);
        list.addAll(left);
        list.addAll(right);
        return list;
    }

    public List<Integer> preorderTraversalDfs(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        dfsTrasversal(root, list);
        return list;
    }

    private void dfsTrasversal(TreeNode root, List<Integer> list) {
        if(root == null) return;
        list.add(root.val);
        dfsTrasversal(root.left, list);
        dfsTrasversal(root.right, list);
    }

    // no recursion
    public List<Integer> preorderTraversalWhile(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if(root == null) return list;
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (! stack.isEmpty()){
            TreeNode cur = stack.pop();
            list.add(cur.val);
            if(cur.right != null) stack.push(cur.right);
            if(cur.left != null) stack.push(cur.left);
        }
        return list;
    }
}
