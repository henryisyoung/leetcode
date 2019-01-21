package leetcode;
import java.util.*;
public class Solution113 {
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
    	
    	List<List<Integer>> rlt = new ArrayList<List<Integer>>();
    	if(root == null) return rlt;
    	List<Integer> list = new ArrayList<Integer>();
    	
    	list.add(root.val);
    	dfs(root, sum, list, rlt);
    	return rlt;
    }

	private void dfs(TreeNode root, int sum, List<Integer> list,
			List<List<Integer>> rlt) {
		if(root == null) return;
		
		if(root.left == null && root.right == null && root.val == sum){
			rlt.add(new ArrayList<Integer>(list));
		}
		
		if(root.left != null){
			list.add(root.left.val);
			dfs(root.left, sum - root.val, list, rlt);
			list.remove(list.size() - 1);
		}
		
		if(root.right != null){
			list.add(root.right.val);
			dfs(root.right, sum - root.val, list, rlt);
			list.remove(list.size() - 1);
		}
	}
}
