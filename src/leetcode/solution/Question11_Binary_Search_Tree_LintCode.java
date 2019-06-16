package leetcode.solution;
import java.util.*;

public class Question11_Binary_Search_Tree_LintCode {
    public List<Integer> searchRange(TreeNode root, int k1, int k2) {
        List<Integer> result = new ArrayList<>();
        helper1(root, k1, k2, result);
        return result;
    }

    private void helper1(TreeNode root, int k1, int k2, List<Integer> result) {
        if(root == null) return;
        helper1(root.left, k1, k2, result);
        if(root.val >= k1 && root.val <= k2) result.add(root.val);
        helper1(root.right, k1, k2, result);
    }

    private List<Integer> searchRange2(TreeNode root, int k1, int k2) {
        if(root == null) return new ArrayList<>();
        List<Integer> result= new ArrayList<>();
        List<Integer> left = searchRange2( root.left,  k1,  k2);
        List<Integer> right = searchRange2( root.right,  k1,  k2);
        result.addAll(left);
        if(root.val >= k1 && root.val <= k2) result.add(root.val);
        result.addAll(right);
        return result;
    }


}
