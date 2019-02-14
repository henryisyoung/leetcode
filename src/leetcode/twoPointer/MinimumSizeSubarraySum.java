package leetcode.twoPointer;

public class MinimumSizeSubarraySum {
    public int minSubArrayLen(int s, int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int i = 0, j = 0, n = nums.length;
        int result = Integer.MAX_VALUE, sum = 0;
        while (j < n) {
            while (j < n && sum < s) {
                sum += nums[j++];
            }
            while (sum >= s) {
                result = Math.min(j - i, result);
                sum -= nums[i++];
            }
        }
        return result == Integer.MAX_VALUE ? 0 : result;
    }
}
