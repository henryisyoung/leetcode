package leetcode.solution;
class ResultType {
	int minValue, maxValue;
	boolean isBalanced;
	public ResultType(int minValue, int maxValue, boolean isBalanced){
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.isBalanced = isBalanced;
	}
}
public class Solution98 {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	// Traverse
	int last = Integer.MIN_VALUE;
	boolean firstNode = true;
    public boolean isValidBST(TreeNode root) {
        if(root == null) return true;
        if(! isValidBST(root.left)){
        	return false;
        }
        if(!firstNode && last >= root.val){
        	return false;
        }
        firstNode = false;
        last = root.val;
        if(! isValidBST(root.right)){
        	return false;
        }
        return true;
    }
    // Divide & Conquer
    public boolean isValidBST2(TreeNode root) {
    	ResultType rlt = helper(root);
    	return rlt.isBalanced;
    }
	private ResultType helper(TreeNode root) {
		if(root.left == null){
			return new ResultType(Integer.MAX_VALUE, Integer.MIN_VALUE, true);
		}
		
		ResultType left = helper(root.left);
		ResultType right = helper(root.right);
		
		if(! left.isBalanced || ! right.isBalanced){
			return new ResultType(0, 0, false);
		}
		if(root.left != null && left.maxValue >= root.val || 
			root.right != null && right.minValue <= root.val){
			return new ResultType(0, 0, false);
		}
		return new ResultType(Math.min(root.val, left.minValue), Math.max(root.val, right.maxValue), true);
	}
}
