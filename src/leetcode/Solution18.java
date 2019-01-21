package leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution18 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a ={-1,0,-5,-2,-2,-4,0,1,-2};
        
		Solution18 t = new Solution18();
		int aa = 3; 
		System.out.println(aa<<2);
		//System.out.println(t.fourSum(a,-9));
	}
    public List<List<Integer>> fourSum(int[] nums, int target) {
    	List<List<Integer>> lists = new ArrayList<List<Integer>>();
    	Arrays.sort(nums);
    	int len = nums.length;
    	for(int i=0;i<len-3;i++){
    		if(4*nums[i]>target) return lists;
    		for(int j=i+1;j<len-2;j++){
    			if(2*(nums[i]+nums[j])>target) return lists;
    			int m=j+1, n=len-1; 
    			while(m<n){
    				int sum = nums[i]+nums[j]+nums[m]+nums[n];
    				if(sum==target){
    					lists.add(Arrays.asList(nums[i],nums[j],nums[m],nums[n]));
    					do{m++;}while(m<n&&nums[m]==nums[m-1]);
    					do{n--;}while(m<n&&nums[n]==nums[n+1]);
    				}else{
    					 if(sum<target){
        					do{m++;}while(m<n&&nums[m]==nums[m-1]);
        				}else{
        					
        					do{n--;}while(m<n&&nums[n]==nums[n+1]);
        				}
    				}
    			}
    			while(j<len-2&&nums[j]==nums[j+1]) j++;
    		}
    		while(i<len-3&&nums[i]==nums[i+1])i++;
    	}
    	return lists;
    }
}
