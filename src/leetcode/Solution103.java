package leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Solution103 {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    	List<List<Integer>> rlt = new ArrayList<List<Integer>>();
    	if(root == null) return rlt;
    	Queue<TreeNode> queue = new LinkedList<TreeNode>();
    	queue.offer(root);
    	int count = 0;
    	while(!queue.isEmpty()){
    		int size = queue.size();
    		List<Integer> list = new ArrayList<Integer>();
    		for(int i = 0; i < size; i++){
    			TreeNode p = queue.poll();
    			if(count %2 ==0){
    				list.add(p.val);
    			}
    			else{
    				list.add(0, p.val);
    			}
    			if(p.left != null){
    				queue.offer(p.left);
    			}if(p.right != null){
    				queue.offer(p.right);
    			}
    		}
    		count++;
    		rlt.add(list);
    	}
    	return rlt;
    }
	
}
