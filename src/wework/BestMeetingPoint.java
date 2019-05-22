package wework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BestMeetingPoint {
    public int minTotalDistance(int[][] grid) {
        int sum = 0;
        if (grid == null || grid.length == 0) {
            return sum;
        }
        int rows = grid.length, cols = grid[0].length;
        List<Integer> rPos = new ArrayList<>();
        List<Integer> cPos = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    rPos.add(i);
                    cPos.add(j);
                }
            }
        }
        for (int i : rPos) {
            sum += Math.abs(i - rPos.get(rPos.size()/2));
        }
        Collections.sort(cPos);
        for (int i : cPos) {
            sum += Math.abs(i - cPos.get(cPos.size()/2));
        }
        return sum;
    }
}
