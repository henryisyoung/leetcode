package leetcode.solution;

public class Solution116 {
	    class TreeLinkNode {
		      int val;
		      TreeLinkNode left, right, next;
		      TreeLinkNode(int x) { val = x; }
		  }
	    
	    public void connect(TreeLinkNode root) {
	        if(root == null) return;
	        if(root.left == null && root.right == null) return;
	        
	        root.left.next = root.right;
	        if(root.next != null){
	        	root.right.next = root.next.left;
	        }
	        connect(root.left);
	        connect(root.right);
	    }
}
