package leetcode.solution;

import java.util.Arrays;

public class Solution259 {

	public static void main(String[] args) {
		Solution259 t = new Solution259();
		int[] nums = {-2, 0, 1, 3};
		System.out.println(t.threeSumSmaller(nums, 4));

	}
    public int threeSumSmaller(int[] nums, int target) {
        if(nums == null || nums.length == 0){
        	return 0;
        }
        Arrays.sort(nums);
        int count = 0, n = nums.length;
        for(int i = 0; i < n - 2; i++){
        	int j = i + 1, k = n - 1;
        	while(j < k){
        		int sum = nums[i] + nums[j] + nums[k];
        		if(sum >= target){
        			k--;

        		}else{
        			count += k - j;
        			j++;

        		}
        	}

        }
        return count;
    }
}
