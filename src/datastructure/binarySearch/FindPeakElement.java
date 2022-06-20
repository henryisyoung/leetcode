package datastructure.binarySearch;

public class FindPeakElement {
    public static int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] >= nums[mid - 1] && nums[mid] >= nums[mid + 1]) {
                return mid;
            }
            else if (nums[mid] >= nums[mid - 1] && nums[mid] <= nums[mid + 1]) {
                left = mid;
            } else {
                right = mid;
            }
        }
        if (nums[left] > nums[right]) return left;
        return right;
    }

    public static void main(String[] args) {
        int[] nums = {1,2,1,3,5,6,4};
        System.out.println(findPeakElement(nums));
    }
}
