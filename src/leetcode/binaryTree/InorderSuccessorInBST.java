package leetcode.binaryTree;

import java.util.Stack;

public class InorderSuccessorInBST {
    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.left;
        }
        boolean prev= false;
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            if (prev) {
                return cur;
            }
            if (cur == p) {
                prev = true;
            }
            TreeNode n = cur.right;
            while (n != null) {
                stack.push(n);
                n = n.left;
            }
        }
        return null;
    }

    public TreeNode inorderSuccessor2(TreeNode root, TreeNode p) {
        TreeNode suc = null;
        while(root != null){
            if(root.val > p.val){
                suc = root;
                root = root.left;
            }else{
                root = root.right;
            }
        }
        return suc;
    }
}
