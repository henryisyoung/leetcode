package datastructure.quickselect;

public class KthLargestElementInArray {
    public int findKthLargest(int[] nums, int k) {
        int n = nums.length;
        return helper(nums, n - k, 0, n - 1);
    }

    private int helper(int[] nums, int kth, int l, int r) {
        if (l == r) return nums[l];
        int pos = partition(nums, l, r);
        if (kth == pos) {
            return nums[pos];
        }
        if (kth < pos) {
            return helper(nums, kth, l, pos - 1);
        } else {
            return helper(nums, kth, pos + 1, r);
        }
    }

    private int partition(int[] nums, int l, int r) {
        int left = l, right = r;
        int pivot = nums[l];
        while (left < right) {
            while (left < right && nums[right] >= pivot) {
                right--;
            }
            nums[left] = nums[right];
            while (left < right && nums[left] <= pivot) {
                left++;
            }
            nums[right] = nums[left];
        }
        nums[left] = pivot;
        return left;
    }
}
