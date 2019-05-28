package google;

import java.util.*;

public class RobotPaths {
    public int uniquePaths(int rows, int cols) {
        int[] dp = new int[rows];
        int[] tmp = new int[rows];
        dp[0] = 1;
        for(int j = 1 ; j  < cols ; j++) {
            for(int i = 0 ; i < rows ; i++) {
                int val1 = i - 1 >= 0 ? dp[i - 1] : 0;
                int val2 = dp[i];
                int val3 = i + 1 < rows ? dp[i + 1] : 0;
                tmp[i] = val1 + val2 + val3;
            }
            System.arraycopy(tmp, 0, dp, 0, tmp.length);
        }
        return dp[0];
    }

    public boolean canReach(int[][] points) {
        List<int[]> list = new ArrayList<>();
        list.add(new int[] {0, 0});
        for(int[] point : points) {
            list.add(point);
        }
        Collections.sort(list, (a, b) -> {
            return a[1] - b[1];
        });
        for(int i = 1 ; i < list.size() ; i++) {
            int[] curr = list.get(i);
            int[] prev = list.get(i-1);
            if(curr[1] == prev[1]) {
                return false;// 此处的判断不够严谨；还有一种情况是，两个点在同一列上，但这两个点其实是同一个点，则我们不应该直接false
            }
            int len = curr[1] - prev[1];
            int upper = prev[0] - len;
            int lower = prev[0] + len;
            if(curr[0] <= lower && curr[0] >= upper) continue;
            else return false;
        }
        return true;
    }

//    followup3: 给定矩形里的三个点，找到遍历这三个点的所有路径数量
//    思路：
//    1：还是按照follow up 1的思路用滚动数组dp，但是如果当前列有需要到达的点时，只对该点进行dp，其他格子全部置零，表示我们只care这一列上经过目标点的路径
//    2：如果一列上有多个需要达到的点，直接返回0；
    public int uniquePaths(int rows, int cols, int[][] points) {
        int[] dp = new int[rows];
        int[] tmp = new int[rows];
        Map<Integer, Integer> map = new HashMap<>();
        for(int[] point : points) {
            if(map.containsKey(point[1])) {
                return 0;
            } else {
                map.put(point[1], point[0]);
            }
        }
        int res = 0;
        dp[0] = 1;

        for(int j = 1 ; j  < cols ; j++) {
            for(int i = 0 ; i < rows ; i++) {
                int val1 = i - 1 >= 0 ? dp[i - 1] : 0;
                int val2 = dp[i];
                int val3 = i + 1 < rows ? dp[i + 1] : 0;
                tmp[i] = val1 + val2 + val3;
            }
            System.arraycopy(tmp, 0, dp, 0, tmp.length);
            if(map.containsKey(j)) {
                int row = map.get(j);
                for(int i = 0 ; i < rows ; i++) {
                    if(i != row) dp[i] = 0;
                    else res = dp[i];
                }
            }
        }
        return res;
    }

}
