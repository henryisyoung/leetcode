package leetcode.binaryTree;

import java.util.ArrayList;
import java.util.List;

public class BinaryTreePaths {
    public List<String> binaryTreePaths(TreeNode root) {
        List<String> rlt = new ArrayList<String>();
        if(root == null){
            return rlt;
        }
        helper(root, rlt, String.valueOf(root.val));
        return rlt;
    }

    private void helper(TreeNode root, List<String> rlt, String path) {
        if(root.left == null && root.right == null){
            rlt.add(path);
            return;
        }
        if(root.left != null){
            helper(root.left, rlt, path + "->" + root.left.val);
        }
        if(root.right != null){
            helper(root.right, rlt, path + "->" + root.right.val);
        }
    }
}
