package leetcode;

import java.util.Arrays;

public class Solution283 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub.
		int[] nums = {20, 1, 10, 3, 12};
		Solution283 t = new Solution283();
		t.moveZeroes(nums);
		System.out.println(Arrays.toString(nums));

	}
    public void moveZeroes(int[] nums) {
        if(nums == null || nums.length < 2){
        	return;
        }
        int i = 0, n = nums.length, index = 0;
        while(i < n){
        	if(nums[i] != 0){
        		nums[index++] = nums[i];
        	}
        	i++;
        }
        while(index < n){
        	nums[index++] = 0;
        }
    }
}
