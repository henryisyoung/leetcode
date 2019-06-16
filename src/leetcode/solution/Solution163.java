package leetcode.solution;
import java.util.*;
public class Solution163 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] nums = {0, 1, 3, 50, 75};
		Solution163 t = new Solution163();
		System.out.println(t.findMissingRanges(nums, 0, 99));
	}
    public List<String> findMissingRanges(int[] nums, int lower, int upper) {
    	List<String> rlt = new ArrayList<String>();
    	if(nums == null || nums.length == 0){
    		rlt.add(toRangeString(lower, upper));
    		return rlt;
    	}
    	int prev = lower - 1, cur = 0;
    	for(int i = 0; i <= nums.length; i++){
    		cur = i == nums.length?upper + 1:nums[i];
    		if((cur - prev) > 1){
    			rlt.add(toRangeString(prev + 1, cur - 1));
    		}
    		prev = cur;
    	}
    	return rlt;
    }
	private String toRangeString(int lower, int upper) {
		if(lower == upper){
			return Integer.toString(lower);
		}
		return lower + "->" + upper;
	}
}
