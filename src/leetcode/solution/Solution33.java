package leetcode.solution;

public class Solution33 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution33 t = new Solution33();
		int[] a = {1,3};
		System.out.println(t.search(a, 3));
	}
	public int search(int[] nums, int target) {
		int left=0,right=nums.length-1;
        return searchHelper(nums,left,right,target);
    }
	private int searchHelper(int[] a, int left, int right, int target) {
		int mid = (left+right)/2;
		if(a[mid]==target) return mid;
		if(left>right) return -1;
		if(a[left]<=a[mid]){
			if(target<a[mid] && target>=a[left]){
				return searchHelper(a,left,mid-1,target);
			} else{
				return searchHelper(a,mid+1,right,target);
			}
		}
		else if(a[left]>a[mid]){
			if(a[mid]<a[right]){
				if(target>a[mid] && target<=a[right]){
					return searchHelper(a,mid+1,right,target);
				}else{
					return searchHelper(a,left,mid-1,target);
				}
			}
		}
		return -1;
	}
	
	public int search2(int[] nums, int target) {
		if(nums == null || nums.length ==0) return -1;
		return helper(nums,0,nums.length-1,target);
	}
	private int helper(int[] nums, int start, int end, int target) {
		while(start +1 < end){
			int mid = start + (end - start)/2;
			if(nums[mid] == target){
				return mid;
			}else if(nums[mid] > nums[start]){
				if(nums[mid] >= target && nums[start] <= target){
					end = mid;
				}else{
					start = mid;
				}
			}else{
				if(nums[mid] <= target && nums[end] >= target){
					start = mid;
				}else{
					end = mid;
				}
			}
		}
		if(nums[start] == target) return start;
		if(nums[end] == target) return end;
		return -1;
	}
}
