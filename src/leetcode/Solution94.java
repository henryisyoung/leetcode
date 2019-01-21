package leetcode;
import java.util.*;
public class Solution94 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	// DFS
    public List<Integer> inorderTraversal(TreeNode root) {
    	List<Integer> rlt = new ArrayList<Integer>();
    	helper(root,rlt);
    	return rlt;
    }
	private void helper(TreeNode root, List<Integer> rlt) {
		if(root==null) return;
		helper(root.left,rlt);
		rlt.add(root.val);
		helper(root.right,rlt);
	}
	
	// Divide & Conqure
	public List<Integer> inorderTraversal2(TreeNode root) {
    	return helper2(root);
	}

	private List<Integer> helper2(TreeNode root) {
		List<Integer> rlt = new ArrayList<Integer>();
		if(root==null) return rlt;
		List<Integer> left = helper2(root.left);
		List<Integer> right = helper2(root.right);
		
		rlt.addAll(left);
		rlt.add(root.val);
		rlt.addAll(right);
		return rlt;
	}
	
}
