package leetcode.highFrequency;

public class ContinuousSubarraySum {
    public boolean checkSubarraySum(int[] nums, int k) {
        if(nums == null || nums.length == 0) {
            return false;
        }
        int n = nums.length;
        int[] sum = new int[n];
        for (int i = 1; i < n; i++) {
            sum[i] = sum[i - 1] + nums[i];
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int diff = sum[j] - sum[i] + nums[i];
                if (diff == k || (k != 0 && diff % k == 0)) {
                    return true;
                }
            }
        }
        return false;
    }
}
