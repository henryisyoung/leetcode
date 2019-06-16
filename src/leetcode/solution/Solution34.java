package leetcode.solution;

public class Solution34 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution34 t = new Solution34();
		int[] a ={1};
		System.out.println(t.searchRange(a,1));
	}
    public int[] searchRange(int[] nums, int target) {
        int index = binarySearch(nums,target);
        int[] result = new int[2];
        if(index<0){
        	result[0]=-1;
        	result[1]=-1;
        	return result;
        }
        int left=index,right=index;
        while( left>=0 &&nums[left]==target)  left--;
        while(right<nums.length&&nums[right]==target) right++;
        result[0]=++left;
        result[1]=--right;
        return result;
    }

	private int binarySearch(int[] nums, int target) {
		// TODO Auto-generated method stub
		int lo=0,hi=nums.length-1;
		while(lo<=hi){
			int mid=(lo+hi)/2;
			if(nums[mid]<target) lo=mid+1;
			else if(nums[mid]>target) hi=mid-1;
			else return mid;
		}
		return -1;
	}
	
	public int[] searchRange2(int[] nums, int target) {
		int[] result = {-1,-1};
		if(nums==null) return result;
		int hi=nums.length-1,lo=0;
		while(lo<hi){
			int mid=(lo+hi)/2;
			if(nums[mid]<target) lo=mid+1;
			else hi=mid;
		}
		if(nums[lo]==target) result[0]=lo;
		else return result;
		hi=nums.length-1;
		while(lo<hi){
			int mid=(lo+hi+1)/2;
			if(nums[mid]==target) lo=mid;
			else hi=mid-1;
		}
		result[1] = hi;
		return result;
	}
	public int[] searchRange3(int[] nums, int target) {
		int[] rlt = {-1,-1};
		
		if(nums == null || nums.length == 0) return rlt;
		int start = 0, end = nums.length - 1;
		int r1 = find1st(nums,start,end,target);
		int r2 = findlast(nums,start,end,target);
		
		rlt[0] = r1; rlt[1]=r2;
		return rlt;
	}
	private int findlast(int[] nums, int start, int end, int target) {
		while(start + 1 < end){
			int mid = start + (end - start)/2;
			if(nums[mid] > target){
				end = mid;
			}else{
				start = mid;
			}
		}
		if(nums[end] == target) return end;
		if(nums[start] == target) return start;
		return -1;
	}
	private int find1st(int[] nums, int start, int end, int target) {
		while(start + 1 < end){
			int mid = start + (end - start)/2;
			if(nums[mid] < target){
				start = mid;
			}else{
				end = mid;
			}
		}
		if(nums[start] == target) return start;
		if(nums[end] == target) return end;
		return -1;
	}
}
