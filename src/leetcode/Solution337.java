package leetcode;

public class Solution337 {
	public int rob(TreeNode root) {  
		return robDFS(root)[1];
	}

	private int[] robDFS(TreeNode root) {
		int[] dp = new int[2];
		if(root == null){
			dp[0] = 0;
			dp[1] = 0;
			return dp;
		}
		int[] left = robDFS(root.left);
		int[] right = robDFS(root.right);
		dp[0] = left[1] + right[1];
		dp[1] = Math.max(dp[0], root.val + left[0] +right[0]);
		return dp;
	}
}
