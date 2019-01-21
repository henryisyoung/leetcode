package leetcode.binarySearch;

public class SearchInRotatedSortedArray {
    public int search(int[] nums, int target) {
        if(nums == null || nums.length == 0) return -1;
        int n = nums.length;
        int s = 0, e = n -1;
        while (s + 1 < e){
            int mid = s + (e - s)/2;
            if(nums[mid] == target){
                return mid;
            }else if (nums[mid] > nums[e]){
                if(nums[mid] > target && target >= nums[s]){
                    e = mid;
                }else {
                    s = mid;
                }
            }else {
                if(nums[mid] < target && target <= nums[e]){
                    s = mid;
                }else {
                    e = mid;
                }
            }

        }
        if(nums[s] == target) return s;
        if(nums[e] == target) return e;
        return -1;
    }
}
