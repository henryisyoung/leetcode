package leetcode.solution;
import java.util.*;
public class Solution174 {
    public int calculateMinimumHP(int[][] dungeon) {
        if(dungeon == null || dungeon.length == 0 || dungeon[0].length == 0) {
        	return 0;
        }
        int r = dungeon.length, c = dungeon[0].length;
        int[][] dp = new int[r][c];
        dp[r - 1][c - 1] = Math.max(1, 1 - dungeon[r - 1][c - 1]);
        for(int i = r - 2; i >= 0; i--){
        	dp[i][c - 1] = Math.max(1, dp[i + 1][c - 1] - dungeon[i][c - 1]);
        }
        for(int i = c - 2; i >= 0; i--){
        	dp[r - 1][i] = Math.max(1, dp[r - 1][i + 1] - dungeon[r - 1][i]);
        }
        
        for(int i = r - 2; i >= 0; i--){
        	for(int j = c - 2; j >= 0; j--){
        		dp[i][j] = Math.max(1, Math.min(dp[i + 1][j], dp[i][j + 1]) - dungeon[i][j]);
        	}
        }
        
        return dp[0][0];
    }
}
