package leetcode.solution;

import java.util.Arrays;


public class Solution41 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution41 t = new Solution41();
		int[] nums={0,1,1,1,2};
		System.out.println(Arrays.binarySearch(nums, 1));

		//System.out.println(t.firstMissingPositive(nums));
	}
	public int firstMissingPositive(int[] nums) {
	    if (nums == null || nums.length == 0) {
	        return 1;
	    }
	    for (int i = 0; i < nums.length; i++) {
	        while (nums[i] > 0 && nums[i] <= nums.length && nums[i] != i + 1 &&nums[i]!=nums[nums[i] - 1]) {
	            int temp = nums[nums[i] - 1];
	            nums[nums[i] - 1] = nums[i];
	            nums[i] = temp;
	        }
	    }
	    int i = 0;
	    while (i < nums.length) {
	        if (nums[i] != i + 1) {
	            return i + 1;
	        }
	        i++;
	    }
	    return i + 1;
	}
	
}
