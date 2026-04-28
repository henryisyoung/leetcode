package LinkedIn;

import java.util.ArrayList;
import java.util.List;

public class InsertInterval {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> list = new ArrayList<>();
        int pos = 0;
        for (int[] cur : intervals) {
            if (cur[1] < newInterval[0]) {
                list.add(cur);
                pos++;
            } else if (newInterval[1] < cur[0]) {
                list.add(cur);
            } else {
                newInterval[0] = Math.min(cur[0], newInterval[0]);
                newInterval[1] = Math.max(cur[1], newInterval[1]);
            }
        }
        list.add(pos, newInterval);
        int[][] result = new int[list.size()][2];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }

        return result;
    }
}
