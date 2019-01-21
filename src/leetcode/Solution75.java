package leetcode;

import java.util.Arrays;

public class Solution75 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution75 t = new Solution75();
		int[] nums = {1,0};
		t.sortColors2(nums);
		Arrays.toString(nums);
	}
    public void sortColors(int[] nums) {
        if(nums==null||nums.length==0) return;
        int n=nums.length,start=0,end=n-1;
        int[] rlt = new int[n];
        for(int i:nums){
        	if(i==0){
        		rlt[start]=0;
        		start++;
        	}else if(i==2){
        		rlt[end]=2;
        		end--;
        	}
        }
        while(start<=end){
        	rlt[start]=1;
        	start++;
        }
        System.arraycopy(rlt, 0, nums, 0, n);
    }
    
    public void sortColors2(int[] nums) {
        if(nums==null||nums.length==0) return;
        int n=nums.length, start=0, end=n-1, count = 1;
        while(count <= 2){
        	while(start < end){
	        	while(start < end && nums[start] < count){
	        		start++;
	        	}
	        	while(start < end && nums[end] >= count){
	        		end--;
	        	}
	        	if(start < end){
	        		int t = nums[start];
	        		nums[start] = nums[end];
	        		nums[end] = t;
	        		start++;
	        		end--;
	        	}
	        }
            end = n - 1;
            count++;
        }
    }
}
