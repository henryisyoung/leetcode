package recovery;

public class SearchInsertPos {
    public int searchInsert(int[] nums, int target) {

        int left = 0, right = nums.length - 1;
        if (nums[left] >= target) return left;
        if (nums[right] < target) return right;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (nums[left] == target) return left;
        return right;
    }
}
