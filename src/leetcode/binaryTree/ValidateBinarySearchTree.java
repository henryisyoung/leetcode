package leetcode.binaryTree;

import Bloomberg.TreeNode;

public class ValidateBinarySearchTree {
    public boolean isValidBST(TreeNode root) {
        return validTreeHelper(root).isBst;
    }

    private MyResult validTreeHelper(TreeNode root) {
        if(root == null) return new MyResult(true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MyResult left = validTreeHelper(root.left);
        MyResult right = validTreeHelper(root.right);

        if(left.isBst == false || right.isBst == false){
            return new MyResult(false, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        if ((root.left != null && left.maxValue >= root.val)
                || ( root.right != null && right.minValue <= root.val)){
            return new MyResult(false, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        return new MyResult(true, Math.max(right.maxValue, root.val), Math.min(left.minValue, root.val));
    }

    private class MyResult{
        boolean isBst;
        int maxValue;
        int minValue;

        public MyResult(boolean isBst, int maxValue, int minValue) {
            isBst = isBst;
            maxValue = maxValue;
            minValue = minValue;
        }
    }

    public boolean isValidBST2(TreeNode root) {
        return bstValidHelper(root).isBst;
    }

    private MyResult bstValidHelper(TreeNode root) {
        if (root == null) {
            return new MyResult(true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        MyResult left = bstValidHelper(root.left);
        MyResult right = bstValidHelper(root.right);

        if (left.isBst == false || right.isBst == false) {
            return new MyResult(false, 0, 0);
        }
        if (root.left != null && left.maxValue >= root.val
                || root.right != null && right.maxValue <= root.val) {
            return new MyResult(false, 0, 0);
        }
        return new MyResult(true, Math.max(right.maxValue, root.val), Math.min(left.minValue, root.val));
    }
}
