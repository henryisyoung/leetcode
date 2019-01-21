package leetcode;
import java.util.*;
public class Solution129 {
    public int sumNumbers(TreeNode root) {
        if(root == null){
        	return 0;
        }
        List<Integer> list = new ArrayList<Integer>();
        helper(root, list, 0);
        int sum = 0;
        for(int i : list){
        	sum += i;
        }
        return sum;
    }

	private void helper(TreeNode root, List<Integer> list, int val) {
		if(root == null){
			return;
		}
		if(root.left == null && root.right == null){
			val = 10*val + root.val;
			list.add(val);
			return;
		}
		val = 10*val + root.val;
		helper(root.left, list, val);
		helper(root.right, list, val);
	}
}
