package leetcode.solution;
import java.util.*;
public class MaxTree {
	public TreeNode maxTree(int[] A) {
		if(A == null || A.length == 0){
			return null;
		}
		Stack<TreeNode> stack = new Stack<>();
		stack.push(new TreeNode(A[0]));
		for(int i = 1; i < A.length; i++){
			if(A[i] < stack.peek().val){
				stack.push(new TreeNode(A[i]));
			}else{
				TreeNode n1 = stack.pop();
				while(!stack.isEmpty() && A[i] > stack.peek().val){
					TreeNode n2 = stack.pop();
					n2.right = n1;
					n1 = n2;
				}
				TreeNode node = new TreeNode(A[i]);
				node.left = n1;
				stack.push(node);
			}
		}
		TreeNode root = stack.pop();
		while(!stack.isEmpty()){
			stack.peek().right = root;
			root = stack.pop();
		}
		return root;
	}
}
