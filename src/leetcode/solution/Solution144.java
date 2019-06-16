package leetcode.solution;
import java.util.*;
public class Solution144 {
	
	// Divide & Conquer
    public List<Integer> preorderTraversal(TreeNode root) {
    	List<Integer> rlt = new ArrayList<Integer>();
    	if(root == null){
    		return rlt;
    	}
    	List<Integer> left = preorderTraversal(root.left);
    	List<Integer> right = preorderTraversal(root.right);
    	
    	rlt.add(root.val);
    	rlt.addAll(left);
    	rlt.addAll(right);
    	return rlt;
    }
    // DFS
    public List<Integer> preorderTraversal2(TreeNode root) {
    	List<Integer> rlt = new ArrayList<Integer>();
    	helper(root,rlt);
    	return rlt;
    }
	private void helper(TreeNode root, List<Integer> rlt) {
		if(root == null) return;
		rlt.add(root.val);
		helper(root.left,rlt);
		helper(root.right,rlt);
	}
	
	// No recursion
	public List<Integer> preorderTraversal3(TreeNode root) {
		List<Integer> rlt = new ArrayList<Integer>();
		Stack<TreeNode> stack = new Stack<TreeNode>();
		if(root == null) return rlt;
		stack.push(root);
		while(!stack.isEmpty()){
			TreeNode p = stack.pop();
			rlt.add(p.val);
			if(p.right != null) stack.push(p.right);
			if(p.left != null) stack.push(p.left);
		}
		return rlt;
	}
}
