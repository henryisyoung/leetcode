package leetcode.solution;

public class Solution285 {
	public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
		TreeNode suc = null;
		while(root != null){
			if(root.val > p.val){
				suc = root;
				root = root.left;
			}else{
				root = root.right;
			}
		}
		return suc;
	}
}
