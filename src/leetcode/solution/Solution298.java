package leetcode.solution;

public class Solution298 {
	 public int longestConsecutive(TreeNode root) {
		   if(root == null){
	            return 0;
	        }
		 return helper(root, 0, root.val - 1);
	    }
	    
	    private int helper(TreeNode root, int length, int preVal){
	    	if(root == null){
	    		return length;
	    	}
	    	int curLen = preVal + 1 == root.val?length + 1:1;
	    	int left = helper(root.left, curLen, root.val);
	    	int right = helper(root.right, curLen, root.val);
	    	return Math.max(length, Math.max(left, right));
	    }
}
