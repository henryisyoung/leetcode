package databricks;

public class HouseRobber {
    public static int rob(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        int[] dp = new int[n];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n; i++) {
            dp[i] = Math.max(dp[i - 1], nums[i] + dp[i - 2]);
        }
        return dp[n - 1];
    }

    public static int robII(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        return Math.max(robHelper(nums, 0, n - 2),  robHelper(nums, 1, n - 1));
    }

    private static int robHelper(int[] nums, int start, int end) {
        int n = end - start + 1;
        int[] dp = new int[n];
        dp[0] = nums[start];
        dp[1] = Math.max(nums[start], nums[start + 1]);
        for (int i = 2; i < n; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[start + i]);
        }
        return dp[n - 1];
    }

    public static void main(String[] args) {
//        int[] nums = {2,7,9,3,1};
//        System.out.println(rob(nums));

        int[] nums2 = {1,2,3};
        System.out.println(robII(nums2));
    }
}
