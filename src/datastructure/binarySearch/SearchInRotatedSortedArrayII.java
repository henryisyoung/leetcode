package datastructure.binarySearch;

public class SearchInRotatedSortedArrayII {
    public boolean search(int[] nums, int target) {
        if (nums == null || nums.length == 0) return false;

        int left = 0, right = nums.length - 1;

        while(left + 1 < right) {
            int mid = left + (right - left) / 2;

            if(nums[mid] == target) {
                return true;
            } else if (nums[mid] < nums[right]) {
                if(target <= nums[right] && target > nums[mid]) {
                    left = mid;
                } else {
                    right = mid;
                }
            } else if (nums[mid] > nums[right]) {
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid;
                } else {
                    left = mid;
                }
            } else {
                right--;
            }
        }

        return nums[left] == target || nums[right] == target;
    }
}
