package leetcode.binaryTree;

import java.util.ArrayList;
import java.util.List;

public class SearchRangeInBST {
    public List<Integer> searchRange(TreeNode root, int k1, int k2) {
        return searchHelper(root, k1, k2);
    }

    private List<Integer> searchHelper(TreeNode root, int k1, int k2) {
        if(root == null){
            return new ArrayList<>();
        }
        List<Integer> result = new ArrayList<>();
        List<Integer> left = searchRange(root.left, k1, k2);
        List<Integer> right = searchRange(root.right, k1, k2);
        result.addAll(left);
        if(root.val >= k1 && root.val <= k2){
            result.add(root.val);
        }
        result.addAll(right);
        return result;
    }

}
