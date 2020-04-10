package intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertInterval {
    public static int[][] insert(int[][] intervals, int[] newInterval) {
        if (intervals == null || intervals.length == 0) return new int[][]{newInterval};
        int pos = 0;
        List<int[]> list = new ArrayList<>();
        for (int[] ints : intervals) {
            if (ints[1] < newInterval[0]) {
                list.add(ints);
                pos++;
            } else if (ints[0] > newInterval[1]) {
                list.add(ints);
            } else {
                newInterval[0] = Math.min(ints[0], newInterval[0]);
                newInterval[1] = Math.max(ints[1], newInterval[1]);
            }
        }
        list.add(pos, newInterval);
        int[][] result = new int[list.size()][2];
        return list.toArray(result);
    }

    public static void main(String[] args) {
        int[][] intervals = {{1,3},{6,9}};
        int[] newInterval = {2,5};
        System.out.println(Arrays.deepToString(insert(intervals, newInterval)));
    }
}
