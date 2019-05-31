package google;

public class KthLargestElemenInArray {
    public int findKthLargest(int[] nums, int k) {
        if (nums == null || k > nums.length) {
            return 0;
        }
        return finder(nums, nums.length - k, 0, nums.length - 1);
    }

    private int finder(int[] nums, int k, int left, int right) {
        if (left == right) {
            return nums[left];
        }
        int pos = patition(nums, left, right);
        if (pos == k) {
            return nums[pos];
        } else if (pos > k) {
            return finder(nums, k, left, pos - 1);
        } else {
            return finder(nums, k , pos + 1, right);
        }
    }

    private int patition(int[] nums, int left, int right) {
        int pivot = nums[left];
        int l = left, r = right;
        while (l < r) {
            while (l < r && nums[r] >= pivot) {
                r--;
            }
            nums[l] = nums[r];
            while (l < r && nums[l] <= pivot) {
                l++;
            }
            nums[r] = nums[l];
        }
        nums[l] = pivot;
        return l;
    }

    public static void main(String[] args) {
        KthLargestElemenInArray sovler = new KthLargestElemenInArray();
        System.out.println(sovler.findKthLargest(new int[]{1,2,0,6}, 4));
    }
}
