package recovery;

public class MaximumSubarray {
    public int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int prev = nums[0], max = prev;

        for (int i = 1; i < nums.length; i++) {
            if (prev < 0) {
                max = Math.max(max, nums[i]);
                prev = nums[i];
            } else {
                max = Math.max(max, prev + nums[i]);
                prev += nums[i];
            }
        }
        return max;
    }
}
