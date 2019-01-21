package leetcode;

public class Solution124 {
	private class ResultType124{
		int singlePath, maxPath;
		public ResultType124(int singlePath, int maxPath){
			this.singlePath = singlePath;
			this.maxPath = maxPath;
		}
	}
	
    public int maxPathSum(TreeNode root) {
    	
        return helper(root).maxPath;
    }
    
    private ResultType124 helper(TreeNode root){
    	if(root == null){
    		return new ResultType124(0, Integer.MIN_VALUE);
    	}
    	
    	ResultType124 left = helper(root.left);
    	ResultType124 right = helper(root.right);
    	
    	int singlePath = Math.max(left.singlePath, right.singlePath) + root.val;
    	singlePath = Math.max(0, singlePath);
    	
    	int maxPath = Math.max(left.maxPath, right.maxPath);
    	maxPath = Math.max(left.singlePath + right.singlePath + root.val, maxPath);
    	return new ResultType124(singlePath, maxPath);
    }
}
