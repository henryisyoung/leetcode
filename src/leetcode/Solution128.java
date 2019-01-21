package leetcode;
import java.util.*;
public class Solution128 {
    public int longestConsecutive(int[] nums) {
        if(nums == null || nums.length == 0) return 0;
        int max = 0, n = nums.length;
        HashSet<Integer> set = new HashSet<Integer>();
        for(int i = 0; i< n; i++){
        	set.add(nums[i]);
        }
        
        for(int i = 0; i< n; i++){
        	int down = nums[i] - 1, up = nums[i] + 1;
        	
        	while(set.contains(down)){
        		set.remove(down);
        		down--;        		
        	}
        	while(set.contains(up)){
        		set.remove(up);
        		up++;        		
        	}
        	max = Math.max(max, up - down - 1);
        }
        return max;
    }
}
