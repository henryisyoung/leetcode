package leetcode.binaryTree;

public class SameTree {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if(p == null && q == null){
            return true;
        }
        if(p == null || q == null){
            return false;
        }
        boolean left = isSameTree(p.left, q.left);
        boolean right = isSameTree(p.right, q.right);
        if(left == false || right == false){
            return false;
        }
        if(p.val != q.val){
            return false;
        }
        return true;
    }

    public boolean isSameTree2(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }
        if(p == null || q == null) {
            return false;
        }
        boolean left = isSameTree2(p.left, q.left);
        boolean right = isSameTree2(p.right, q.right);

        if (left && right && p.val == q.val) {
            return true;
        }
        return false;
    }
}
