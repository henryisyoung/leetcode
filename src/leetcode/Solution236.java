package leetcode;

public class Solution236 {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if(!covers(root,p) || !covers(root,q)){
        	return null;
        }
        return helper( root,  p,  q);
    }

	private TreeNode helper(TreeNode root, TreeNode p, TreeNode q) {
		if(root == null || root == p || root == q){
			return root;
		}
		boolean pisLeft = covers(root.left,p);
		boolean qisLeft = covers(root.left,q);
		if( pisLeft != qisLeft) return root;
		if(qisLeft) return helper(root.left,p,q);
		return helper(root.right,p,q);
		
	}

	private boolean covers(TreeNode root, TreeNode p) {
		// TODO Auto-generated method stub
		if(root == null){
			return false;
		}
		if(root == p){
			return true;
		}
		return covers(root.left,p)||covers(root.right,p);
	}
	
	public TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
		if(root == null || root == p || root == q){
			return root;
		}
		TreeNode left = lowestCommonAncestor2(root.left,p,q);
		TreeNode right = lowestCommonAncestor2(root.right,p,q);
		if(left != null && right != null) return root;
		if(left != null) return left;
		if(right != null) return right;
		return null;
	}
}
