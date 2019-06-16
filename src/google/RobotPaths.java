package google;

import java.util.*;

public class RobotPaths {
    public int uniquePaths(int rows, int cols) {
        int[] dp = new int[rows];
        int[] temp = new int[cols];
        dp[0] = 1;
        for (int i = 1; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int v1 = j > 0 ? dp[j - 1] : 0;
                int v2 = dp[j];
                int v3 = j < rows - 1 ? dp[j + 1] : 0;
                temp[j] = v1 + v2 + v3;
            }
            System.arraycopy(temp, 0, dp, 0, rows);
        }
        return dp[0];
    }

    public boolean canReach(int[][] points) {
        List<int[]> list = new ArrayList<>();
        for (int[] p : points) {
            list.add(p);
        }
        Collections.sort(list, (a, b) -> (a[1] - b [1]));
        for (int i = 1; i < list.size(); i++) {
            int[] cur = list.get(i), prev = list.get(i - 1);
            if (cur[1] == prev[1]) {
                return false;
            }
            int dist = cur[1] - prev[1];
            int upperBound = prev[0] - dist;
            int lowB0und = prev[0] + dist;
            if (cur[0] >= upperBound && cur[1] <= lowB0und) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

//    followup3: 给定矩形里的三个点，找到遍历这三个点的所有路径数量
//    思路：
//    1：还是按照follow up 1的思路用滚动数组dp，但是如果当前列有需要到达的点时，只对该点进行dp，其他格子全部置零，表示我们只care这一列上经过目标点的路径
//    2：如果一列上有多个需要达到的点，直接返回0；
    public int uniquePaths(int rows, int cols, int[][] points) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int[] p : points) {
            if (map.containsKey(p[1])) {
                return 0;
            }
            map.put(p[1], p[0]);
        }
        int[] dp = new int[rows];
        int[] tmp = new int[rows];
        int result = 0;
        dp[0] = 1;
        for (int i = 1; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int val1 = i - 1 >= 0 ? dp[i - 1] : 0;
                int val2 = dp[i];
                int val3 = i + 1 < rows ? dp[i + 1] : 0;
                tmp[i] = val1 + val2 + val3;
            }
            System.arraycopy(tmp, 0, dp, 0, tmp.length);
            if(map.containsKey(i)) {
                int row = map.get(i);
                for(int j = 0 ; j < rows ; j++) {
                    if(j != row){
                        dp[j] = 0;
                    } else {
                        result = dp[j];
                    }
                }
            }
        }
        return result;
    }
}
