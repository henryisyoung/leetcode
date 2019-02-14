package leetcode.partition;

import java.util.Collections;
import java.util.PriorityQueue;

public class KthLargestElementInArray {
    public int findKthLargest(int[] nums, int k) {
        if (nums == null || nums.length < k) {
            return 0;
        }
        int n = nums.length;
        int kthSmallest = n + 1 - k;
        return kthHelper(nums, kthSmallest, 0, n - 1);
    }

    private int kthHelper(int[] nums, int kthSmallest, int left, int right) {
        if (left == right) {
            return nums[left];
        }
        int pos = partition(nums, left, right);
        if (pos + 1 == kthSmallest) {
            return nums[pos];
        } else if (pos + 1 < kthSmallest) {
            return kthHelper(nums, kthSmallest, pos + 1, right);
        } else {
            return kthHelper(nums, kthSmallest, left, pos - 1);
        }
    }

    private int partition(int[] nums, int left, int right) {
        int l = left, r = right;
        int pivot = nums[left];
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

    public int findKthLargestPQ(int[] nums, int k) {
        if (nums == null || nums.length < k) {
            return 0;
        }
        int n = nums.length;
        PriorityQueue<Integer> pq = new PriorityQueue<>(n, Collections.<Integer>reverseOrder());
        for (int i : nums) {
            pq.add(i);
        }
        int result = 0;
        while (k > 0) {
            k--;
            result = pq.poll();
        }
        return result;
    }

}
