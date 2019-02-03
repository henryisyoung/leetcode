package leetcode.dynamicProgramming;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Triangle {
    public int minimumTotal(List<List<Integer>> triangle) {
        if(triangle == null || triangle.size() == 0 ||
                triangle.get(0) == null || triangle.get(0).size() == 0){
            return 0;
        }
        int rows = triangle.size(), cols = triangle.get(rows - 1).size();

        int[][] dp = new int[rows][cols];
        for(int c = 0; c < cols; c++) {
            dp[rows - 1][c] = triangle.get(rows - 1).get(c);
        }
        for(int r = rows - 2; r >= 0; r--){
            for (int c = 0; c <= r; c++) {
                dp[r][c] = Math.min(dp[r + 1][c], dp[r + 1][c + 1]) + triangle.get(r).get(c);
            }
        }

        return dp[0][0];
    }
    public int minimumTotal2(List<List<Integer>> triangle) {
        if (triangle == null || triangle.size() == 0) {
            return 0;
        }
        int rows = triangle.size(), cols = triangle.get(rows - 1).size();

        int[][] dp = new int[rows][cols];
        for (int i = cols - 1; i >= 0; i--) {
            dp[rows - 1][i] = triangle.get(rows - 1).get(i);
        }

        for (int i = rows - 2; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                dp[i][j] = Math.min(dp[i + 1][j], dp[i + 1][j + 1]) + triangle.get(i).get(j);
            }
        }
        return dp[0][0];
    }

}
