package leetcode.solution;

public class Solution152 {
    public int maxProduct(int[] nums) {
        if(nums == null || nums.length ==0){
        	return 0;
        }
        int[] max = new int[nums.length];
        int[] min = new int[nums.length];
        max[0] = nums[0];
        min[0] = nums[0];
        int rlt = nums[0];
        for(int i = 1; i < nums.length; i++){
            max[i] = nums[i];
            min[i] = nums[i];
            if(nums[i] > 0){
            	max[i] = Math.max(max[i - 1]*nums[i], max[i]);
            	min[i] = Math.min(min[i - 1]*nums[i], min[i]);
            }else{
            	max[i] = Math.max(min[i - 1]*nums[i], max[i]);
            	min[i] = Math.min(max[i - 1]*nums[i], min[i]);
            }
            rlt = Math.max(rlt, max[i]);
        }
        return rlt;
    }
}
