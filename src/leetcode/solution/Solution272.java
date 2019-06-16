package leetcode.solution;
import java.util.*;
public class Solution272 {
	  public class TreeNode {
		      int val;
		      TreeNode left;
		      TreeNode right;
		      TreeNode(int x) { val = x; }
		  }
	  
	  public List<Integer> closestKValues(TreeNode root, double target, int k) {
		  Queue<Integer> queue = new LinkedList<Integer>();
		  Stack<TreeNode> stack = new Stack<TreeNode>();
		  while(!stack.isEmpty() || root != null){
			  if(root != null){
				  stack.push(root);
				  root = root.left;
			  }else{
				  TreeNode p = stack.pop();
				  if(queue.size() < k){
					  queue.offer(p.val);
				  }else{
					  int val = queue.peek();
					  if(Math.abs(val - target) > Math.abs(p.val - target)){
						  queue.poll();
						  queue.offer(p.val);
					  }
				  }
				  root = p.right;
			  }
		  }
		  return (List<Integer>) queue;
	  }
	  
	  public List<Integer> closestKValues2(TreeNode root, double target, int k) {  
	        List<Integer> result = new ArrayList<Integer>();  
	        LinkedList<Integer> stackPre = new LinkedList<Integer>();  
	        LinkedList<Integer> stackSucc = new LinkedList<Integer>();  
	        inorder(root, target, false, stackPre);  
	        inorder(root, target, true, stackSucc);  
	        while (k-- > 0) {  
	            if (stackPre.isEmpty()) {  
	                result.add(stackSucc.pop());  
	            } else if (stackSucc.isEmpty()) {  
	                result.add(stackPre.pop());  
	            } else if (Math.abs(stackPre.peek() - target) < Math.abs(stackSucc.peek() - target)) {  
	                result.add(stackPre.pop());  
	            } else {  
	                result.add(stackSucc.pop());  
	            }  
	        }  
	        return result;  
	    }  
	    public void inorder(TreeNode root, double target, boolean reverse, LinkedList<Integer> stack) {  
	        if (root == null) return;  
	        inorder(reverse ? root.right : root.left, target, reverse, stack);  
	        if ((reverse && root.val <= target) || (!reverse && root.val > target))  
	            return;  
	        stack.push(root.val);  
	        inorder(reverse ? root.left : root.right, target, reverse, stack);  
	    }  

}
