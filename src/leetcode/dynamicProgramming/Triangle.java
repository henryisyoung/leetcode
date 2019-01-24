package leetcode.dynamicProgramming;

import java.util.ArrayList;
import java.util.List;

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
}
