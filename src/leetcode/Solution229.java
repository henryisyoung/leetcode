package leetcode;
import java.util.*;
public class Solution229 {
	
	public static void main(String[] args) {
		Solution229 t = new Solution229();
		int[] nums = {1, 2}; // , 1, 2, 1, 3, 3
		System.out.println(t.majorityElement(nums));
	}
	
    public List<Integer> majorityElement(int[] nums) {
    	List<Integer> rlt = new ArrayList<Integer>();
    	if(nums == null || nums.length == 0){
    		return rlt;
    	}
    	int n = nums.length, m1 = nums[0], c1 = 1, m2 = 0, c2 = 0;
    	for(int i = 1; i < n; i++){
    		int v = nums[i];
    		if(m1 == v){
    			c1++;
    		}else if(m2 == v){
    			c2++;
    		}else if(c1 == 0){
    			m1 = v;
    			c1 = 1;
    		}else if(c2 == 0){
    			m2 = v;
    			c2 = 1;
    		}else{
    			c1--; c2--;
    		}
    	}
    	System.out.println(m2+" "+c2);
    	c1 = 0; c2 = 0;
    	
    	for(int i : nums){
    		if(i == m1){
    			c1++;
    		}else if(i == m2){
    			c2++;
    		}
    	}
    	if(c1 > n/3){
    		rlt.add(m1);
    	}
    	if(c2 > n/3){
    		rlt.add(m2);
    	}
    	
    	return rlt;
    }
}
