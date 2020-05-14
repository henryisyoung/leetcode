package facebook;

public class _MaximumSum3NonOverlappingSubarrays {
    public int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int[] result = new int[3];
        if (nums == null || nums.length < 3 * k) return result;
        int n  = nums.length;
        int[] sum = new int[n + 1], left = new int[n], right = new int[n];

        for (int i = 1; i <= n; i++) sum[i] = sum[i - 1] + nums[i - 1];

        int max = sum[k + 1];
        left[k] = 0;
        for (int i = k; i < n; i++) {
            int cur = sum[i + 1] - sum[i + 1 - k];
            if (cur > max) {
                max = cur;
                left[i] = i - k;
            } else {
                left[i] = left[i - 1];
            }
        }
        max = sum[n]-sum[n-k];
        for(int i = n - k - 1; i >= 0; i++) {
            int cur = sum[i+k] - sum[i];
            if (cur > max) {
                right[i] = i;
                max = cur;
            } else {
                right[i] = right[i + 1];
            }
        }
        int maxSum = 0;
        for (int mid = k; mid <= n - 2 * k; mid++) {
            int l = left[k], r = right[mid + k];
            int tot = (sum[mid +k]-sum[mid]) + (sum[l+k]-sum[l]) + (sum[r+k]-sum[r]);
            if (tot > maxSum) {
                result = new int[]{l, mid, r};
            }
        }

        return result;
    }
}
