package leetcode.solution;
import java.util.*;
public class Solution107 {
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
    	List<List<Integer>> rlt = new ArrayList<List<Integer>>();
    	if(root == null) return rlt;
    	Queue<TreeNode> queue = new LinkedList<TreeNode>();
    	queue.offer(root);
    	while(!queue.isEmpty()){
    		int size = queue.size();
    		List<Integer> list = new ArrayList<Integer>();
    		for(int i = 0; i < size; i++){
    			TreeNode p = queue.poll();
    			list.add(p.val);
    			if(p.left != null){
    				queue.offer(p.left);
    			}if(p.right != null){
    				queue.offer(p.right);
    			}
    		}
    		rlt.add(0,list);
    	}
    	return rlt;
    }
}
