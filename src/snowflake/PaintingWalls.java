package snowflake;

import java.util.Arrays;
import java.util.Map;

public class PaintingWalls {
    // Reframing: if paid painter takes wall i, during its time[i] units the free
    // painter knocks out time[i] more walls. So picking wall i "covers" time[i]+1
    // walls at cost[i]. We need a subset whose total coverage >= n, minimizing cost.
    // This is a 0/1 knapsack.
    public int paintWalls(int[] cost, int[] time) {
        int n = cost.length;
        int[] dp = new int[n + 1];
        int sum = 0;
        for(int c : cost) sum += c;
        Arrays.fill(dp, sum);
        dp[0] = 0;
        for (int i = 0; i < n; i++) {
            for (int j = n; j >= 1; j--) {
                int prevWall = Math.max(0, j - (1 + time[i]));
                dp[j] = Math.min(dp[j], dp[prevWall] + cost[i]);
            }
        }
        return dp[n];
    }
}
