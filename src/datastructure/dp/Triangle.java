package datastructure.dp;

import java.util.Arrays;
import java.util.List;

public class Triangle {
    public static int minimumTotal(List<List<Integer>> triangle) {
        int rows = triangle.size();
        int[][] dp = new int[rows][rows];
        dp[0][0] = triangle.get(0).get(0);
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j <= i; j++) {
                if (j == 0) {
                    dp[i][j] = dp[i - 1][j] + triangle.get(i).get(j);
                } else if (j == i) {
                    dp[i][j] = dp[i - 1][j - 1] + triangle.get(i).get(j);
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], dp[i - 1][j]) + triangle.get(i).get(j);
                }
            }
        }
        int min = Integer.MAX_VALUE;
        for (int i : dp[rows - 1]) min = Math.min(min, i);
        return min;
    }

    public static void main(String[] args) {
        List<List<Integer>> triangle = Arrays.asList(
                Arrays.asList(2),
                Arrays.asList(3, 4),
                Arrays.asList(6,5,7),
                Arrays.asList(4,1,8,3)
        );
        System.out.println(minimumTotal(triangle));
    }
}
