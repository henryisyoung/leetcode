package datastructure.binarySearch;

public class FindMinimumInRotatedSortedArray {
    public static int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid;
            } else {
                right = mid;
            }
        }

        if (nums[left] < nums[right]) return nums[left];

        return nums[right];
    }

    public static void main(String[] args) {
        int[] nums = {11,13,15,17};
        System.out.println(findMin(nums));
    }
}
