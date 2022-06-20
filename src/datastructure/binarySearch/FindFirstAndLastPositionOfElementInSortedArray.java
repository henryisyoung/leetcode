package datastructure.binarySearch;

public class FindFirstAndLastPositionOfElementInSortedArray {
    public int[] searchRange(int[] nums, int target) {
        int[] result = {-1, -1};
        if (nums == null || nums.length == 0) return result;

        int left = findLeft(nums, target);
        if (left == -1) return result;
        int right = findRight(nums, target);

        result[0] = left;
        result[1] = right;

        return result;
    }

    private int findLeft(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] >= target) {
                right = mid;
            } else {
                left = mid;
            }
        }

        if (nums[left] == target) return left;
        if (nums[right] == target) return right;
        return -1;
    }

    private int findRight(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] <= target) {
                left = mid;
            } else {
                right = mid;
            }
        }

        if (nums[right] == target) return right;
        if (nums[left] == target) return left;

        return -1;
    }
}
