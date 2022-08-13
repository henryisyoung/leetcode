package uber.phone.leetcode;

public class SplitArrayLargestSum {
    // https://leetcode.com/problems/split-array-largest-sum/
    public  int splitArray(int[] nums, int m) {
        int left = 0, right = 0;
        for (int i : nums) {
            left = Math.max(left, i);
            right += i;
        }

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if(validSize(mid, nums, m)) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (validSize(left, nums, m)) {
            return left;
        }
        return right;
    }

    private boolean validSize(int size, int[] nums, int m) {
        int sum = 0;
        int count = 1;
        for (int i : nums) {
            if (sum + i > size) {
                count++;
                sum = i;
            } else {
                sum += i;
            }
        }
        return count <= m;
    }

    public int calSum(int []sum,int startIndex,int endIndex) {
        return sum[endIndex]-sum[startIndex];
    }

    public int splitArrayLargestSum(int[] pages, int k) {
        //坐标型：dp[m][n]表示前m本书被前n个人拷贝的最短时间
        // 特判
        if(pages==null||pages.length<=0||k<=0)
        {
            return 0;
        }

        // 后续下标从0开始,到length
        int [][]dp=new int [pages.length+1][k+1];
        int []sum=new int [pages.length+1];

        // 初始化
        sum[0]=0;
        dp[0][0]=dp[1][0]=dp[0][1]=0;
        dp[1][1]=pages[0];

        //n本书，1个人，时间是总和
        //因为dp的下标是从开始，而pages要从0开始计算,但是最后一本书就没算了
        for(int i=1;i<dp.length;i++)
        {
            dp[i][1]=pages[i-1]+dp[i-1][1];
            sum[i]=sum[i-1]+pages[i-1];
        }

        //1本书，n个人，时间相同
        for(int i=1;i<dp[0].length;i++)
        {
            dp[1][i]=dp[1][1];
        }

        // 进入dp
        for(int i=2;i<=pages.length;i++) {
            // 表示人数
            for(int j=2;j<=k;j++) {
                dp[i][j]=Integer.MAX_VALUE;
                //注意之前写好的边界条件
                for(int x=1;x<i;x++)
                {
                    dp[i][j]=Math.min(Math.max(dp[x][j-1],calSum(sum,x,i)),dp[i][j]);
                }
            }
        }
        return dp[pages.length][k];
    }
}
