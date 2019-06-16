package leetcode.solution;

import java.util.Arrays;

public class Solution377 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution377 t = new Solution377();
		int[] a ={1,2,3};
		
		//System.out.println(i--);
		System.out.println(t.combinationSum42(a, 4));
	}
	int count = 0;
    public int combinationSum4(int[] nums, int target) {
    	this.count = 0;
        if(nums == null || nums.length == 0){
        	return count;
        }
        Arrays.sort(nums);
        helper(nums, target, 0);
        return count;
    }
    
	private void helper(int[] nums, int target, int sum) {
		if(sum == target){
			//System.out.println(count);
			count++;
			return;
		}
		for(int i = 0; i < nums.length; i++){
			if(sum + nums[i] > target){
				break;
			}
			//System.out.println(sum);
			helper(nums, target, sum + nums[i]);
		}
	}
	
	public int combinationSum42(int[] nums, int target) {
        if(nums == null || nums.length == 0){
        	return 0;
        }
        Arrays.sort(nums);
		int[] dp = new int[target + 1];
		for(int i = 1; i <= target; i++){
			for(int num : nums){
				if(num > i){
					break;
				}
				else if(num == i){
					dp[i] += 1;
				}else{
					dp[i] += dp[i - num];
				}
			}
		}
		return dp[target];
	}
}
