package leetcode.solution;

import java.util.Stack;

public class Solution255 {
	public boolean verifyPreorder(int[] preorder) {
		Stack<Integer> stack = new Stack<Integer>();
		int min = Integer.MIN_VALUE;
		for(int num : preorder){
			if(num < min){
				return false;
			}
			while(!stack.isEmpty() && num > stack.peek()){
				min = stack.pop();
			}
			stack.push(num);
		}
		return true;
	}
	
	public boolean verifyPreorder2(int[] preorder) {
		int i = -1, min = Integer.MIN_VALUE;
		for(int num : preorder){
			if(num < min){
				return false;
			}
			while(i >= 0 && num > preorder[i]){
				min = preorder[i--];
			}
			preorder[++i] = num;
		}
		return true;
	}
	
	public boolean verifyPostorder2(int[] nums) {
        int i = nums.length;
        int max = Integer.MAX_VALUE;
        for (int j = nums.length - 1; j >= 0; j--)
        {
            if (nums[j] > max) return false;
            while (i <= nums.length - 1 && nums[j] > nums[i])
                max = nums[i++];
            nums[--i] = nums[j];
        }
        return true;
	}
}
