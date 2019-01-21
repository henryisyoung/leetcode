package leetcode;

public class Solution104 {
	
	// Divide & Conquer
    public int maxDepth(TreeNode root) {
        if(root == null){
        	return 0;
        }
        int left = maxDepth(root.left);
        int right = maxDepth(root.right);
        
        return Math.max(left, right)+1;
    }
    
    // Traverse
    int depth;
    public int maxDepth2(TreeNode root) {
    	depth = 0;
    	helper(root,1);
    	return depth;
    }
	private void helper(TreeNode root, int curDepth) {
		if(root == null){
			return;
		}
		if(curDepth > depth){
			depth = curDepth;
		}
		helper(root.left,curDepth+1);
		helper(root.right,curDepth+1);
	}
}
