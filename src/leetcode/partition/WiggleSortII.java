package leetcode.partition;

import java.util.Arrays;

public class WiggleSortII {
    public void wiggleSort(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int n = nums.length, i = (n - 1) / 2, j = n - 1;
        int[] table = new int[n];
        Arrays.sort(nums);

        for (int k = 0; k < n; k++) {
            table[k] = k % 2 == 0 ? nums[i--] : nums[j--];
        }
        for (int k = 0; k < n; k++) {
            nums[k] = table[k];
        }
    }
}
