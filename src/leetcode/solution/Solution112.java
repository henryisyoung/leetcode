package leetcode.solution;
import java.util.*;
public class Solution112 {
	
	// recursion
    public boolean hasPathSum(TreeNode root, int sum) {
        if(root == null) return false;
        if(root.left == null && root.right == null){
        	return sum == root.val;
        }
        return hasPathSum(root.left, sum - root.val) || hasPathSum(root.right, sum - root.val);
    }
    
    // queue
    public boolean hasPathSum2(TreeNode root, int sum) {
    	Queue<TreeNode> qNode = new LinkedList<TreeNode>();
    	Queue<Integer> valNode = new LinkedList<Integer>();
    	qNode.offer(root);
    	valNode.offer(root.val);
    	
    	while(!qNode.isEmpty()){
    		TreeNode p = qNode.poll();
    		int val = valNode.poll();
    		if(p.left == null && p.right == null && val == sum){
    			return true;
    		}
    		if(p.left != null){
    			qNode.offer(p.left);
    			valNode.offer(val + p.left.val);
    		}
    		if(p.right != null){
    			qNode.offer(p.right);
    			valNode.offer(val + p.right.val);
    		}
    	}
    	return false;
    }
    

}
