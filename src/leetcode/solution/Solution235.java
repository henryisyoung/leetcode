package leetcode.solution;

public class Solution235 {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if(root == null || root == p || root ==q){
        	return root;
        }
        if((root.val - q.val) * (root.val - p.val) < 0){
        		return root;
        }else if(root.val < p.val){
        		return lowestCommonAncestor(root.right,p,q);
        }else{
        		return lowestCommonAncestor(root.left,p,q);
        }
    }
    
    public TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
    	if(root == null) return null;
    	while(root != null){
    		if((root.val - p.val)*(root.val - q.val) <= 0){
    			return root;
    		}else if(root.val > p.val){
    			root = root.left;
    		}else{
    			root = root.right;
    		}
    	}
    	return null;
    }
}
