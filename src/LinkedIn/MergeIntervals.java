package LinkedIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MergeIntervals {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> {
            if (a[0] != b[0]) {
                return a[0] - b[0];
            }
            return a[1] - b[1];
        });

        int[] prev = intervals[0];
        List<int[]> list = new ArrayList<>();
        for (int i = 1; i < intervals.length; i++) {
            int[] cur = intervals[i];
            if (cur[0] > prev[1]) {
                list.add(prev);
                prev = cur;
            } else {
                prev[1] = Math.max(prev[1], cur[1]);
            }
        }

        list.add(prev);
        int[][] result = new int[list.size()][2];
        for (int i = 0 ; i < list.size(); i++) {
            result[i] = list.get(i);
        }

        return result;
    }
}
