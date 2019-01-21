package leetcode;

import java.util.Arrays;
import java.util.HashMap;

public class Solution16 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = {0,2,1,-3};
		Solution16 t = new Solution16();
		//t.trySth(a);
		System.out.println('9'-48);
		
	}
	
    public int threeSumClosest(int[] nums, int target) {
        int min = 0x7fffffff;
        int result = 0;
        Arrays.sort(nums);
        for(int i=0; i<nums.length-2;i++){
        	int j=i+1,k=nums.length-1;
        	while(j<k){
	        	int sum = nums[i] + nums[j] + nums[k];
	        	if(sum==target){
	        		return sum;
	        	}else{
		        	if(min> Math.abs(sum-target)){
		        		min = Math.abs(sum-target);
		        		result = sum;
		        	}
		        	if(0<sum-target){
		        		do{k--;}while(j<k && nums[k]==nums[k+1]);
		        	}else{
		        		do{j++;}while(j<k&&nums[j]==nums[j-1]);
		        		}
	        		}
	        	//do{j++;}while(j<k&&nums[j]==nums[j-1]);
	        	//do{k--;}while(j<k && nums[k]==nums[k+1]);
        		}
        	//while(i<nums.length-2 && nums[i]==nums[i+1])	i++;	
        	}
        return result;
    }
    
    public void trySth(int[] a){
    	int j=0;
    	int k=a.length;
    	Arrays.sort(a);
    	do{j++;
    	System.out.println(j);
    	}while(j<k&&a[j]==a[j-1]);
    }

}
