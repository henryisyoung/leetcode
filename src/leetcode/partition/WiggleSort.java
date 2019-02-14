package leetcode.partition;

public class WiggleSort {
    public void wiggleSort(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int n = nums.length;
        for (int i = 1; i < n; i++) {
            if (i % 2 == 1 && nums[i] < nums[i - 1] ||
                    i % 2 == 0 && nums[i] > nums[i - 1]) {
                int temp = nums[i];
                nums[i] = nums[i - 1];
                nums[i - 1] = temp;
            }
        }
    }
}
