package leetcode;

import java.util.Stack;

public class Solution114 {
	
	private TreeNode prev;
    public void flatten(TreeNode root) {
        if(root == null){
        	return;
        }
        
        TreeNode right = root.right;
        if(prev != null){
        	prev.left = null;
        	prev.right = root;
        }
        
        prev = root;
        flatten(root.left);
        flatten(right);
    }
    
    public void flatten3(TreeNode root) {
        Stack<TreeNode> stack = new Stack<TreeNode>();
        TreeNode p = root;
 
        while(p != null || !stack.empty()){
 
            if(p.right != null){
                stack.push(p.right);
            }
 
            if(p.left != null){
                p.right = p.left;
                p.left = null;
            }else if(!stack.empty()){
                TreeNode temp = stack.pop();
                p.right=temp;
            }
 
            p = p.right;
        }
    }
}
