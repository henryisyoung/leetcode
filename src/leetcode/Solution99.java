package leetcode;
import java.util.*;
public class Solution99 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	private TreeNode prev = null;
	private TreeNode first = null;
	private TreeNode second = null;
	
    public void recoverTree(TreeNode root) {
    	recoverTreeH(root);
    	int tmp = first.val;
    	first.val = second.val;
    	second.val = tmp;
    }

	private void recoverTreeH(TreeNode root) {
		if(root == null) return;
		
		recoverTreeH(root.left);
		
		if(prev != null && root.val < prev.val){
			if(first == null){
				first = prev;
				second = root;
			}else{
				second = root;
			}
		}
		prev = root;
		recoverTreeH(root.right);
	}
	
	public void recoverTree2(TreeNode root) {
		if(root == null) return;
		
		TreeNode prev = null;
		TreeNode first = null;
		TreeNode second = null;
		Stack<TreeNode> stack = new Stack<TreeNode>();
		
		while(!stack.isEmpty() || root != null){
			if(root != null){
				stack.push(root);
				root = root.left;
			}else{
				TreeNode p = stack.pop();
				if(prev != null && p.val < prev.val){
					if(first == null){
						first = prev;
						second = p;
					}else{
						second = p;
					}
				}
				prev = p;
				root = p.right;
			}
		}
		
    	int tmp = first.val;
    	first.val = second.val;
    	second.val = tmp;
	}
}
