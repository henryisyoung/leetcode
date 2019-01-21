package leetcode.binarySearch;

public class SearchInsertPosition {
    public int searchInsert(int[] nums, int target) {
        if(nums == null || nums.length == 0){
            return 0;
        }
        int start = 0, end = nums.length - 1;
        if(nums[start] >= target) return start;
        while (start + 1 < end){
            int mid = start + (end - start)/2;
            if(nums[mid] == target){
                return mid;
            }else if(nums[mid] < target){
                start = mid;
            }else {
                end = mid;
            }
        }
        if(nums[start] == target){
            return start;
        }
        if(nums[end] == target){
            return end;
        }
        if(nums[end] < target){
            return end + 1;
        }
        return start + 1;
    }
}
