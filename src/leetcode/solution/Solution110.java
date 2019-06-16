package leetcode.solution;
class ResultType2 {
	int  maxValue;
	boolean isBalanced;
	public ResultType2(int maxValue, boolean isBalanced){
		this.maxValue = maxValue;
		this.isBalanced = isBalanced;
	}
}
public class Solution110 {
	
	// Divide & Conquer
    public boolean isBalanced(TreeNode root) {
        ResultType2 rlt = helper(root);
        return rlt.isBalanced;
    }

	private ResultType2 helper(TreeNode root) {
		if(root == null){
			return new ResultType2(0, true);
		}
		ResultType2 left = helper(root.left);
		ResultType2 right = helper(root.right);
		if(! left.isBalanced || ! right.isBalanced){
			return new ResultType2(-1, false);
		}
		if(Math.abs(left.maxValue-right.maxValue)>1){
			return new ResultType2(-1, false);
		}
		return new ResultType2(Math.max(left.maxValue, right.maxValue) + 1, true);
	}

}
