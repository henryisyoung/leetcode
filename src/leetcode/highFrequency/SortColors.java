package leetcode.highFrequency;

public class SortColors {
    public void sortColors(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int start = 0, end = nums.length - 1, color = 1;
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
    }
}
