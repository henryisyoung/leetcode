package leetcode.solution;
import java.util.*;
public class Solution199 {
    public List<Integer> rightSideView(TreeNode root) {
    	List<Integer> rlt = new ArrayList<Integer>(); 
    	if(root == null){
    		return rlt;
    	}
    	Queue<TreeNode> q = new LinkedList<TreeNode>();
    	q.offer(root);
    	while(!q.isEmpty()){
    		int size = q.size();
    		for(int i = 0; i < size; i++){
    			TreeNode p = q.poll();
    			if(i == size - 1){
    				rlt.add(p.val);
    			}
    			if(p.left != null){
    				q.offer(p.left);
    			}
    			if(p.right != null){
    				q.offer(p.right);
    			}
    		}
    	}
    	return rlt;
    }
    int maxdepth = 0;
    public List<Integer> rightSideView2(TreeNode root) {
    	List<Integer> rlt = new ArrayList<Integer>(); 
    	if(root != null){
    		helper(root, rlt, 1);
    	}
    	return rlt;
    }

	private void helper(TreeNode root, List<Integer> rlt, int depth) {
		if(depth > maxdepth){
			maxdepth = depth;
			rlt.add(root.val);
		}
		if(root.right != null){
			helper(root.right, rlt, depth + 1);
		}
		if(root.left != null){
			helper(root.left, rlt, depth + 1);
		}
	}
}
