package leetcode.binaryTree;

import Bloomberg.TreeNode;

public class BinaryTreeMaximumPathSum {
    public int maxPathSum(TreeNode root) {
        MyResult result = sumHelper(root);
        return result.maxPath;
    }

    private MyResult sumHelper(TreeNode root) {
        if(root == null){
            return new MyResult(0,Integer.MIN_VALUE );
        }

        MyResult left = sumHelper(root.left);
        MyResult right = sumHelper(root.right);

        int singlePath = Math.max(left.singlePath, right.singlePath) + root.val;
        singlePath = Math.max(0, singlePath);

        int maxPath = Math.max(left.maxPath, right.maxPath);
        maxPath = Math.max(left.singlePath + right.singlePath + root.val, maxPath);

        return new MyResult(singlePath, maxPath);
    }

    private class MyResult{
        int singlePath;
        int maxPath;

        public MyResult(int singlePath, int maxPath) {
            this.singlePath = singlePath;
            this.maxPath = maxPath;
        }
    }
}
