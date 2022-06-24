package apple;

public class MaximumSubarray {
    public int maxSubArray(int[] nums) {
        int sum = 0;
        if (nums == null || nums.length == 0) {
            return sum;
        }
        int prev = nums[0];

        for (int i = 1; i < nums.length; i++) {
            if (prev < 0) {
                sum = Math.max(nums[i], sum);
                prev = nums[i];
            } else {
                sum = Math.max(sum, prev + nums[i]);
                prev += nums[i];
            }
        }

        return sum;
    }

}
