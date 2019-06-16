package leetcode.solution;

public class Solution81 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution81 t = new Solution81();
		int[] nums = {2,2,2,0,2,2};
		System.out.println(t.search(nums, 0));
	}
    public boolean search(int[] nums, int target) {
        if(nums.length==0||nums==null) return false;
        int start = 0, end = nums.length-1;
        while(start + 1 < end){
        	int mid = start + (end - start)/2;
        	if(nums[mid] == target) return true;
        	else if(nums[mid]<nums[end]){
        		if(target>nums[mid] && target<=nums[end]){
        			start = mid;
        		}else{
        			end = mid;
        		}
        	}else if(nums[mid]>nums[end]){
        		if(target<nums[mid] && target>=nums[start]){
        			end = mid;
        		}else{
        			start = mid;
        		}
        	}else{
        		end--;
        	}
        }
        return (nums[start]==target||nums[end]==target);
    }
}
