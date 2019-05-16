package pinterest;

public class JumpGameII {
    public static int jump(int[] nums) {
        if (nums == null || nums.length == 0){
            return 0;
        }
        int n = nums.length;
        int[] dp = new int[n];
        for (int i = 1; i < n; i++) {
            dp[i] = Integer.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                if (dp[j] != Integer.MAX_VALUE && nums[j] + j >= i) {
                    dp[i] = Math.min(dp[i], dp[j] + 1);
                }
            }
        }
        return dp[n - 1];
    }

    public static int jumpGreedy(int[] nums) {
        if (nums == null || nums.length <= 1){
            return 0;
        }
        int n = nums.length, max = 0, count = 0;
        int start = 0, end = 0;
        while (end < n) {
            count++;
            for (int i = start; i <= end; i++) {
                if (nums[i] + i > max) {
                    max = nums[i] + i;
                }
                if (max >= n - 1) {
                    return count;
                }
            }
            start = end + 1;
            end = max;
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums = {2,1,1,1,4};
        System.out.println(jumpGreedy(nums));
    }
}
