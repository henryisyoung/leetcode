package snap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BestMeetingPoint {
    public int minTotalDistance(int[][] grid) {
        if(grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) return 0;
        int rows = grid.length, cols = grid[0].length;
        List<Integer> rPos = new ArrayList<>(), cPos = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(grid[i][j] == 1) {
                    rPos.add(i);
                    cPos.add(j);
                }
            }
        }
        Collections.sort(cPos);
        return calDist(rPos) + calDist(cPos);
    }

    private int calDist(List<Integer> list) {
        int i = 0, j = list.size() - 1, sum = 0;
        while (i < j) {
            sum += list.get(j--) - list.get(i++);
        }
        return sum;
    }
}
