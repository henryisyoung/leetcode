package leetcode;

public class Solution136 {
    public int singleNumber(int[] nums) {
        if(nums == null | nums.length == 0) return -1;
        int n = nums[0];
        for(int i = 1; i < nums.length; i++){
        	n = nums[i] ^ n;
        }
        return n;
    }
    
    // hash table

}
