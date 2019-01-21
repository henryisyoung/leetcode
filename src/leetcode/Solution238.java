package leetcode;

import java.util.Arrays;

public class Solution238 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution238 t = new Solution238();
		int[] nums = {1,2,3,4};
		System.out.println(Arrays.toString(t.productExceptSelf2(nums)));
	}
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] rlt = new int[n], right = new int[n], left = new int[n];
        for(int i = 0; i < n; i++){
        	if(i == 0){
        		left[i] = 1;
        	}else{
        		left[i] = left[i - 1] * nums[i - 1];
        	}
        }
        for(int i = n - 1; i >= 0; i--){
        	if(i == n - 1){
        		right[i] = 1;
        	}else{
        		right[i] = right[i + 1] * nums[i + 1];
        	}
        }
        for(int i = 0; i < n; i++){
        	rlt[i] = left[i]*right[i];
        }
        return rlt;
    }
    
    
    // constant space
    public int[] productExceptSelf2(int[] nums) {
        int n = nums.length;
        int[] rlt = new int[n];
        for(int i = 0; i < n; i++){
        	if(i == 0){
        		rlt[i] = 1;
        	}else{
        		rlt[i] = rlt[i - 1] * nums[i - 1];
        	}
        }
        int tmp = 0;
        for(int i = n - 1; i >= 0; i--){
        	if(i == n - 1){
        		tmp = nums[i];
        		nums[i] = 1;
        	}else{
        		int local = nums[i];
        		nums[i] = tmp * nums[i + 1];
        		tmp = local;
        	}
        }
        for(int i = 0; i < n; i++){
        	rlt[i] = nums[i]*rlt[i];
        }
        return rlt;
    }
}
