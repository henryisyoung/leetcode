package leetcode.binarySearch;

public class FindMinimumInRotatedSortedArray {
    public int findMin(int[] nums) {
        if(nums == null || nums.length == 0){
            return 0;
        }
        int start = 0, end = nums.length - 1;
        while (start + 1 < end){
            int mid = start + (end - start)/2;
            if(nums[mid] > nums[end]){
                start = mid;
            }else {
                end = mid;
            }
        }
        return Math.min(nums[start], nums[end]);
    }
    public int findMin2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int start = 0, end = nums.length - 1;
        while (start + 1 < end) {
            int mid = start + (end - start) / 2;
            if(nums[mid] > nums[end]) {
                start = mid;
            } else {
                end = mid;
            }
        }
        return Math.min(nums[start], nums[end]);
    }
}
