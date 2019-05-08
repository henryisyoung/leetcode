package leetcode.graphAndSearch;

import java.util.Arrays;

public class SortColors {
    public static void sortColors(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int color = 1;
        int start = 0, end = nums.length - 1;
        while (color <= 2) {
            while (start < end) {
                while (start < end && nums[start] < color) {
                    start++;
                }
                while (start < end && nums[end] >= color) {
                    end--;
                }
                if (nums[start] > nums[end]) {
                    int tmp = nums[start];
                    nums[start] = nums[end];
                    nums[end] = tmp;
                    start++;
                    end--;
                }
            }
            color++;
            end = nums.length - 1;
        }
        System.out.println(Arrays.toString(nums));
    }

    public static void main(String[] args) {
        int[] nums = {2,0,2,1,1,0};
        sortColors(nums);
    }
}
