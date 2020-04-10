package facebook;

public class KthLargestElementArray {
    public static int findKthLargest(int[] nums, int k) {
        int n = nums.length;
        return finder(nums, 0, n - 1, n - k);
    }

    private static int finder(int[] nums, int left, int right, int k) {
        if (left >= right) return nums[left];
        int pos = partititon(left, right, nums);
        if (pos == k) return nums[pos];
        if (pos > k) return finder(nums, left, pos - 1, k);
        return finder(nums, pos + 1, right, k);
    }

    private static int partititon(int left, int right, int[] nums) {
        int l = left, r = right, pivot = nums[left];
        while (l < r) {
            while (l < r && nums[r] >= pivot) {
                r--;
            }
            nums[l] = nums[r];
            while (l < r && nums[l] < pivot) {
                l++;
            }
            nums[r] = nums[l];
        }
        nums[l] = pivot;
        return l;
    }

    public static void main(String[] args) {
        int[] nums = {2};
        System.out.println(findKthLargest(nums, 1));
    }
}
