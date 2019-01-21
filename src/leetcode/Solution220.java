package leetcode;

public class Solution220 {

	public static void main(String[] args) {
		Solution220 t = new Solution220();
		int[] nums = {0, 2147483647};
		System.out.println(t.containsNearbyAlmostDuplicate(nums, 1, 2));

	}
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        if(nums == null || nums.length == 0){
        	return false;
        }
        for(int i = 0; i < nums.length; i++){
        	if(find(i, nums, k , t)){
        		return true;
        	}
        }
        return false;
    }
	private boolean find(int index, int[] nums, int k, int t) {
		int start = index - k >= 0?index - k:0;
		int end = index + k < nums.length?index + k:nums.length - 1;
		//System.out.println( (long)nums[index] - t);
		//System.out.println(end);
		for(int i = start; i <= end; i++){
			System.out.println( nums[i] >= (long)(nums[index] - t));
			System.out.println((long)(nums[index] + t));
			if(i != index &&(long) nums[i] <= (long)(nums[index] + t) &&(long) nums[i] >= (long)(nums[index] - t)){
				//System.out.println(nums[i]);
				//System.out.println(nums[index]);
				return true;
			}
		}
		return false;
	}
}
