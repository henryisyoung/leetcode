package leetcode.partition;

import java.util.Collections;
import java.util.PriorityQueue;

public class KthLargestElementInArray2 {
    public int findKthLargest(int[] nums, int k) {
        if (nums == null || k < 0 || k > nums.length) {
            return 0;
        }
        return findKth(nums, nums.length - k, 0, nums.length - 1);
    }

    private int findKth(int[] nums, int k, int left, int right) {
        if (left == right) {
            return nums[left];
        }
        int pos = partition(nums, left, right);
        if (pos == k) {
            return nums[pos];
        } else if (pos < k) {
            return findKth(nums, k, pos + 1, right);
        } else {
            return findKth(nums, k, left, pos - 1);
        }
    }

    private int partition(int[] nums, int left, int right) {
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
        int[] a = {1,4,5};
        KthLargestElementInArray2 solver = new KthLargestElementInArray2();
        System.out.println(solver.findKthLargest(a, 5));
    }
}
