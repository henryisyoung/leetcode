package leetcode.solution;

public class Solution35 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution35 t = new Solution35();
		int[] a ={1};
		System.out.println(t.searchInsert(a, 1));
	}
    public int searchInsert(int[] nums, int target) {
        if(nums==null||nums.length==0) return 0;
        int lo=0,hi=nums.length-1;
        while(lo<hi){
        	int mid =lo+ (hi-lo)/2;
        	if(nums[mid]==target) return mid;
        	else if(nums[mid]<target) lo=mid+1;
        	else  hi=mid;
        }
    	if(nums[lo]<target) return lo+1;
    	else {
    		return lo;
    	}	
    }
    public int searchInsert2(int[] nums, int target) {
        if (nums == null || nums.length == 0){
            return 0;
        }
        int start = 0;
        int end = nums.length - 1;
        while (start < end){
            int mid = start + (end - start) /2;
            if (nums[mid] == target){
                return mid;
            }
            else if (nums[mid] > target){
                end = mid;
            }
            else {
                start = mid + 1;
            }
        }
        if (target > nums[start]){
            return start + 1;
        }
        else {
            return start;
        }
    }

}
