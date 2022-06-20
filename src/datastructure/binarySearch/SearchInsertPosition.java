package datastructure.binarySearch;

public class SearchInsertPosition {
    public int searchInsert(int[] nums, int target) {
        if (nums == null || nums.length == 0) return 0;

        int left = 0, right = nums.length - 1;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) return mid;
            else if (nums[mid] > target) right = mid;
            else left = mid;
        }

        if (nums[right] < target) return right + 1;
        if (nums[right] == target || (target > nums[left] && target > nums[left])) return right;
        if (nums[left] >= target) return left;
        return 0;
    }
}
