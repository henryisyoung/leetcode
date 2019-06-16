package leetcode.solution;

public class Question98_Binary_Search_Tree {
    private class ResultType{
        boolean isValid;
        int max;
        int min;
        public ResultType(boolean isValid, int max, int min){
            this.isValid = isValid;
            this.max = max;
            this.min = min;
        }
    }

    public boolean isValidBST(TreeNode root) {
        return helper(root).isValid;
    }

    private ResultType helper(TreeNode root) {
        if(root == null) return new ResultType(true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        ResultType left = helper(root.left);
        ResultType right = helper(root.right);
        if(!left.isValid || !right.isValid) {
            return new ResultType(false, 0 ,0);
        }
        if(root.left != null && left.max >= root.val ||
                root.right != null && right.min <= root.val){
            return new ResultType(false, 0 ,0);
        }
        return new ResultType(true, Math.max(right.max, root.val), Math.min(left.min, root.val));
    }
}
