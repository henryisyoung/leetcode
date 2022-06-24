package datastructure.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergeIntervals {
    class Interval{
        int start, end;
        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
    public int[][] merge(int[][] intervals) {
        List<Interval> list = new ArrayList<>();
        for (int[] interval : intervals) {
            list.add(new Interval(interval[0], interval[1]));
        }
        Collections.sort(list, (o1, o2) -> {
            if (o1.start != o2.start) return o1.start - o2.start;
            return o1.end - o2.end;
        });
        Interval prev= list.get(0);
        List<int[]> result = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            Interval cur = list.get(i);
            if (cur.start > prev.end) {
                result.add(new int[]{prev.start, prev.end});
                prev = cur;
            } else {
                prev.end = Math.max(cur.end, prev.end);
            }
        }
        result.add(new int[]{prev.start, prev.end});
        int[][] array = new int[result.size()][2];
        for (int i = 0; i < result.size(); i++) {
            array[i] = result.get(i);
        }
        return array;
    }
}
