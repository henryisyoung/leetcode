package datastructure.interval;

import java.util.ArrayList;
import java.util.List;

public class InsertInterval {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        if (intervals == null || intervals.length == 0) {
            return new int[][]{newInterval};
        }
        List<int[]> list = new ArrayList<>();
        int pos = 0;
        for (int i = 0; i < intervals.length; i++) {
            int[] cur = intervals[i];
            if(cur[1] < newInterval[0]) {
                pos++;
                list.add(cur);
            } else if (cur[0] > newInterval[1]){
                list.add(cur);
            } else {
                newInterval[0] = Math.min(cur[0], newInterval[0]);
                newInterval[1] = Math.max(cur[1], newInterval[1]);
            }
        }
        list.add(pos, newInterval);
        int size = list.size();
        int[][]result = new int[size][2];
        for (int i = 0; i < size; i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
