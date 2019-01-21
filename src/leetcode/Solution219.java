package leetcode;
import java.util.*;
public class Solution219 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] nums = {1,4,5,2,3,1,6,9,12,1};
		Solution219 t = new Solution219();		
		System.out.println(t.containsNearbyDuplicate(nums, 2));
		
	}
	
  
	
    public boolean containsNearbyDuplicate(int[] nums, int k) {
    	if(nums==null || nums.length<2) return false;  
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(int i = 0; i < nums.length; i++){
        	if(map.containsKey(nums[i])){
        		if(i - map.get(nums[i]) <= k){
        			return true;
        		}
        	}
    			map.put(nums[i], i);
        	
    		
        }
        return false;
    }
}
