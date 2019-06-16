package leetcode.solution;
import java.util.*;
public class Solution230 {
	   private int cnt=0;
	    private int res=0;
	    public int kthSmallest(TreeNode root, int k) {
	        helper(root,k);
	        return res;
	    }
	    private void helper(TreeNode cur,int k){
	        if(cur==null){
	            return;
	            }
	            
	            helper(cur.left,k);
	            cnt++;
	            if(cnt==k) {res=cur.val;return;}
	            helper(cur.right,k);
	        
	    }
	    
	    private boolean founded=false;

	    private void helper2(TreeNode cur,int k){
	        if(!founded&&cur!=null){
	            helper(cur.left,k);
	            cnt++;
	            if(cnt==k) {res=cur.val;founded=true;return;}
	            helper(cur.right,k);
	        }
	    }
	    public int kthSmallest2(TreeNode root, int k) {
	    	Stack<TreeNode> stack = new Stack<TreeNode>();
	    	TreeNode p = root;
	    	int result = 0;
	    	while(!stack.isEmpty() && p != null){
	    		if(p != null){
	    			stack.push(p);
	    			p = p.left;
	    		}else{
	    			TreeNode n = stack.pop();
	    			k--;
	    			if(k==0){
	    				result = n.val;
	    			}
	    			p = n.right;
	    		}
	    	}
	    	return result;
	    }
	    
	    public int kthSmallest3(TreeNode root, int k) {
	        if (root == null) {
	            return 0;
	        }
	         
	        int leftNodes = getNumberNodes(root.left);
	        if(k == leftNodes + 1) {
	            return root.val;
	        } else if (k > leftNodes + 1) {
	            return kthSmallest(root.right, k - leftNodes - 1);
	        } else {
	            return kthSmallest(root.left, k);
	        }
	    }
	     
	    private int getNumberNodes(TreeNode root) {
	        if (root == null) {
	            return 0;
	        }
	         
	        return getNumberNodes(root.left) + getNumberNodes(root.right) + 1;
	    }
	    
}
