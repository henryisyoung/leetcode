package leetcode.dynamicProgramming;

public class PaintHouseII {
    public int minCostII(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0] == null || costs[0].length == 0) {
            return 0;
        }
        int prevFirst = 0, prevSecond = 0, prevIndex = -1;
        for (int i = 0; i < costs.length; i++) {
            int curFirst = Integer.MAX_VALUE, curSecond = Integer.MAX_VALUE, curIndex = -1;
            for (int j = 0; j < costs[0].length; j++) {
                costs[i][j] += j == prevIndex ? prevSecond : prevFirst;
                if (curFirst > costs[i][j]) {
                    curSecond = curFirst;
                    curFirst = costs[i][j];
                    curIndex = j;
                } else if (curSecond > costs[i][j]) {
                    curSecond = costs[i][j];
                }
            }
            prevFirst = curFirst;
            prevSecond = curSecond;
            prevIndex = curIndex;
        }
        return prevFirst;
    }

    public static void main(String[] args) {
        PaintHouseII solver = new PaintHouseII();
        System.out.println(solver.minCostII(new int[][]{{17,2,17},{16,16,5},{14,3,19}}));
    }
}
