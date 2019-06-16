package leetcode.solution;

public class Solution265 {
    public int mincostsII(int[][] costs) {
        if (costs == null || costs.length == 0) return 0;
        int preMin = 0, preSec = 0, preIndex = -1;
        int n = costs.length, k = costs[0].length;
        for (int i = 0; i < n; i++) {
            int curMin = Integer.MAX_VALUE, curSec = Integer.MAX_VALUE, curIndex = 0;
            for (int j = 0; j < k; j++) {
                costs[i][j] += preIndex == j ? preSec : preMin;
                if (costs[i][j] < curMin) {
                    curSec = curMin;
                    curMin = costs[i][j];
                    curIndex = j;
                }
                else if (costs[i][j] < curSec) {
                    curSec = costs[i][j];
                }
            }
            preMin = curMin;
            preSec = curSec;
            preIndex = curIndex;
        }
        return preMin;
    }
}
