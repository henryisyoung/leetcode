package leetcode.twoPointer;

import java.util.Arrays;

public class TriangleCount {
    public int triangleCount(int[] nums) {
        int count = 0;
        if (nums == null || nums.length < 3) {
            return count;
        }
        Arrays.sort(nums);
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                int k = n - 1;
                while (j < k) {
                    if (nums[i] + nums[j] > nums[k]) {
                        count += k - j;
                        break;
                    } else {
                        k--;
                    }
                }
            }
        }
        return count;
    }
}
