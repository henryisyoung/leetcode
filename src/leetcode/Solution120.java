package leetcode;
import java.util.*;
public class Solution120 {
    public int minimumTotal(List<List<Integer>> triangle) {
        int m = triangle.size(), n = triangle.get(m - 1).size();
        int[][] dp = new int[m][n];
        for(int i = 0; i < n; i++) {
        	dp[m-1][i] = triangle.get(m - 1).get(i);
        }
        for(int i = m - 2; i >= 0; i--){
        	for(int j = 0; j <= i; j++){
        		dp[i][j] = Math.min(dp[i + 1][j], dp[i + 1][j + 1]) + triangle.get(i).get(j);
        	}
        }
        return dp[0][0];
    }
}
