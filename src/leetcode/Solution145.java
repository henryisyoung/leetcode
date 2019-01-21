package leetcode;
import java.util.*;
public class Solution145 {
	   public List<Integer> postorderTraversal(TreeNode root) {
	    	List<Integer> rlt = new ArrayList<Integer>();
	    	helper(root,rlt);
	    	return rlt;
	    }
		private void helper(TreeNode root, List<Integer> rlt) {
			if(root==null) return;
			helper(root.left,rlt);
			
			helper(root.right,rlt);
			rlt.add(root.val);
		}
		
		public List<Integer> postorderTraversal2(TreeNode root) {
	    	List<Integer> rlt = new ArrayList<Integer>();
	    	if(root == null){
	    		return rlt;
	    	}
	    	List<Integer> left = postorderTraversal2(root.left);
	    	List<Integer> right = postorderTraversal2(root.right);
	    	
	    	
	    	rlt.addAll(left);
	    	rlt.addAll(right);
	    	rlt.add(root.val);
	    	return rlt;
		}
}
