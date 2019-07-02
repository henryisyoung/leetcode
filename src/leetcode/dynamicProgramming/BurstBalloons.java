package leetcode.dynamicProgramming;

public class BurstBalloons {
    public int maxCoins(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int n = nums.length;
        nums = init(nums);
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 2; j < n; j++) {
                for (int k = i + 1; k < j; k++) {
                    //dp[i][j] 表示把第i到第j个气球打爆的最大价值dp[i][j] 表示把第i到第j个气球打爆的最大价值
                    dp[i][j] = Math.max(dp[i][j], dp[i][k] + dp[k][j] + nums[i] * nums[k] * nums[j]);
                }
            }
        }
        return dp[0][n - 1];
    }

    public int[] init(int[] nums){
        //初始化数组，头部和尾部插入1
        int[] temp = new int[nums.length + 2];
        temp[0] = 1;
        for(int i = 1; i < temp.length - 1; i++){
            temp[i] = nums[i - 1];
        }
        temp[temp.length - 1] = 1;
        return temp;
    }
}
