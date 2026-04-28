package recovery;

public class FindPeakElement {
    public int findPeakElement(int[] nums) {
        if (nums.length == 1) {
            return 0;
        }
        int left = 0, right = nums.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[mid - 1] && nums[mid] > nums[mid + 1]) {
                return mid;
            } else if (nums[mid] >= nums[mid - 1] && nums[mid] <= nums[mid + 1]) {
                left = mid;
            } else {
                right = mid;
            }
        }
        if (nums[right] > nums[left]) return right;
        return left;
    }
}
