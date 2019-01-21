package leetcode;

public class Solution226 {
    public TreeNode invertTree(TreeNode root) {
        helper(root);
        return root;
    }

	private void helper(TreeNode root) {
		if(root == null){
			return;
		}
		TreeNode tmp = root.left;
		root.left = root.right;
		root.right = tmp;
		
		helper(root.left);
		helper(root.right);
	}
}
