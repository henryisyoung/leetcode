package leetcode;

public class Solution169 {
	
	public static void main(String[] args) {
		Solution169 t = new Solution169();
		int[] nums = {2,1,3,1,4,1};
		System.out.println(t.majorityElement(nums));
	}
	
    public int majorityElement(int[] nums) {
        if(nums == null || nums.length == 0){
        	return 0;
        }
        int index = 0, count = 1, n = nums.length;
        for(int i = 1; i < n; i++){
        	if(nums[index] == nums[i]){
        		count++;
        	}else{
        		count--;
        	}if(count == 0){
        		index = i;
        		count = 1;
        	}
        }
        return nums[index];
    }
}
