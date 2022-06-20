package datastructure.binarySearch;

public class SearchInRotatedSortedArray {
    public static void main(String[] args) {

    }

    public int search(int[] nums, int target) {

        if (nums == null || nums.length == 0) return -1;

        int left = 0, size = nums.length, right = size - 1;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > nums[right]) {
                if (nums[mid] > target && target >= nums[left]) {
                    right = mid;
                } else {
                    left = mid;
                }
            } else {
                if (target <= nums[right] && target > nums[mid]) {
                    left = mid;
                } else {
                    right = mid;
                }
            }

        }

        if (nums[left] == target) return left;
        if (nums[right] == target) return right;
        return -1;

    }
}
