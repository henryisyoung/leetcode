package leetcode.solution;
import java.util.*;
public class Solution111 {
    public int minDepth(TreeNode root) {
        int count = 1;
        if(root == null) return 0;
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(root);
        while(!queue.isEmpty()){
        	int size = queue.size();
        	for(int i = 0; i < size; i++){
        		TreeNode p = queue.poll();
        		if(p.left == null && p.right == null){
        			return count;
        		}else{
        			if(p.left != null) queue.offer(p.left);
        			if(p.right != null) queue.offer(p.right);
        		}
        	}
        	count++;
        }
        return count;
    }
}
