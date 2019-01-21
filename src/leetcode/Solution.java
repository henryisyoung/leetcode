package leetcode;

import java.util.Arrays;
import java.util.HashMap;

public class Solution {

	public static void main(String[] args) {
		Solution test = new Solution();
		int[] a = {0,4,3,0};
		int[] result = test.twoSum(a,0);
		System.out.print(Arrays.toString(result));
	}
	
	public int[] twoSum(int[] nums, int target) {  //  Problem 1 -- 7ms
        int[] result = new int[2];
        HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
        for(int i = 0; i < nums.length; i++){
            int off = target - nums[i];
            if(map.containsKey(off)){
                result[0] = i;
                result[1] = map.get(off);
                break;
            }
            else{
                map.put(nums[i],i);
            }
        }
        return result;
    }
}
