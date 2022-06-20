package datastructure.array;

public class MinimumSizeSubarraySum {
    public int minSubArrayLen(int target, int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int j = 0;

        int sum = 0, len = nums.length, result = Integer.MAX_VALUE;
        for (int i = 0; i < len; i++) {
            while (sum < target && j < len) {
                sum += nums[j++];
            }

            if (sum >= target) {
                result = Math.min(j - i, result);
            }
            sum -= nums[i];
        }
        return result == Integer.MAX_VALUE ? 0 : result;
    }
}
