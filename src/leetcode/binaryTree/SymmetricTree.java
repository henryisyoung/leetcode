package leetcode.binaryTree;

public class SymmetricTree {
    public boolean isSymmetric(TreeNode root) {
        if(root == null){
            return true;
        }
        return isSame(root.left, root.right);
    }

    private boolean isSame(TreeNode p, TreeNode q) {
        if(p == null && q == null){
            return true;
        }
        if(p == null || q == null){
            return false;
        }
        return p.val == q.val && isSame(p.left, q.right) && isSame(p.right, q.left);
    }
}
