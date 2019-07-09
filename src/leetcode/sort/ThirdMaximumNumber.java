package leetcode.sort;

public class ThirdMaximumNumber {
    public int thirdMax(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int k = nums.length - 3;
        return findHelper(0, nums.length - 1, k, nums);
    }

    private int findHelper(int l, int r, int k, int[] nums) {
        if (l == r) {
            return nums[l];
        }
        int pos = partion(nums, l, r);
        if (pos == k) {
            return nums[pos];
        } else if (pos > k) {
            return findHelper(l, pos - 1, k, nums);
        } else {
            return findHelper(pos + 1, r, k, nums);
        }
    }

    private int partion(int[] nums, int left, int right) {
        int l = left, r = right;
        int pivot = nums[l];
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
        int[] nums = {2, 2, 3, 1};
        ThirdMaximumNumber solver = new ThirdMaximumNumber();
        System.out.println(solver.thirdMax(nums));
    }
}
