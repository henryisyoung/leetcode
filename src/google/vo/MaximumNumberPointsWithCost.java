package google.vo;

public class MaximumNumberPointsWithCost {
    public static long maxPoints(int[][] points) {
        int rows = points.length, cols = points[0].length;
        long[] prev = new long[cols];
        for (int i = 0; i < cols; i++) {
            prev[i] = points[0][i];
        }

        for (int i = 1; i < rows; i++) {
            long[] left = new long[cols];
            long[] right = new long[cols];
            left[0] = prev[0];
            right[cols - 1] = prev[cols - 1];

            for (int j = 1; j < cols; j++) {
                left[j] = Math.max(left[j - 1] - 1, prev[j]);
            }

            for (int j = cols - 2; j >= 0; j--) {
                right[j] = Math.max(right[j + 1] - 1, prev[j]);
            }
            long[] cur = new long[cols];
            for (int j = 0; j < cols; j++) {
                cur[j] = points[i][j] + Math.max(left[j], right[j]);
            }
            prev = cur;
        }
        long max = 0;
        for (long val : prev) {
            max = Math.max(max, val);
        }

        return max;
    }

    public long maxPoints2(int[][] points) {
        //初始化
        int m = points.length, n = points[0].length;
        long[] dp = new long[n];//dp[i]表示当前行第i个格子被选中时的最大得分
        for (int i = 0; i < n; i++)
            dp[i] = points[0][i];//第0行格子的最大得分就是对应格子的分数

        //动态规划
        for (int i = 1; i < m; i++) {
            long[] lastLine = dp.clone();//复制得到上一行各个格子的最大得分

            //预处理计算各个位置选取上一行的左、右侧格子所能得到的最高分，为简化代码，当前位置正上方的格子统计在左侧中
            long[] left = new long[n], right = new long[n];
            left[0] = lastLine[0];
            for (int j = 1; j < n; j++)
                left[j] = Math.max(left[j - 1] - 1, lastLine[j]);
            right[n - 1] = 0;
            for (int j = n - 2; j >= 0; j--)
                right[j] = Math.max(right[j + 1] - 1, lastLine[j + 1] - 1);

            //计算各个格子的最大得分
            for (int j = 0; j < n; j++)
                dp[j] = Math.max(left[j], right[j]) + points[i][j];
        }

        //统计答案，即求最后一行中各个格子的最大得分
        long ans = 0L;
        for (int i = 0; i < n; i++)
            ans = Math.max(ans, dp[i]);
        return ans;
    }

    public static void main(String[] args) {
        int[][] points = {
                {1,2,3},
                {1,5,1},
                {3,1,1},
        };
        int[][] points2 = {
                {1,5},
                {2,3},
                {4,2},
        };

        int[][] points3 = {
                {5},
                {3},
                {2},
        };
        System.out.println(maxPoints(points3));
    }
}
