package facebook;

public class BurstBallons {
    public static int maxCoins(int[] nums) {
        if (nums == null || nums.length == 0){
            return 0;
        }

        nums = init(nums);
        int[][] dp = new int[nums.length][nums.length];

        for (int i = nums.length - 2; i >= 0; i--) {
            for (int j = i + 2; j < nums.length; j++) {
                for (int k = i + 1; k < j; k++) {
                    dp[i][j] = Math.max(dp[i][j], dp[i][k] + dp[k][j] + nums[i] * nums[k] * nums[j]);    //状态转移利用小区间最优解更新大区间
                }
            }
        }

        return dp[0][nums.length - 1];
    }

    public static int[] init(int[] nums){					//初始化数组，头部和尾部插入1
        int[] temp = new int[nums.length + 2];
        temp[0] = 1;
        for(int i = 1; i < temp.length - 1; i++){
            temp[i] = nums[i - 1];
        }
        temp[temp.length - 1] = 1;
        return temp;
    }

    public static void main(String[] args) {
        int[] nums = {4, 1, 5, 10};
        System.out.println(maxCoins(nums));
    }
}