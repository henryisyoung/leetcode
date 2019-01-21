package leetcode;

public class Solution268 {
    public int missingNumber(int[] nums) {
        int res = 0;
		for (int i = 0; i < nums.length; ++i) {
		    res ^= (i + 1) ^ nums[i];
		}
		return res;
	}
}
