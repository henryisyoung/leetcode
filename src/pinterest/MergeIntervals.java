package pinterest;

import leetcode.linkedList.ListNode;

import java.util.*;

public class MergeIntervals {
    public int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length == 0 || intervals[0] == null || intervals[0].length == 0){
            return intervals;
        }
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        int[] prev = intervals[0];
        int n = intervals.length;
        List<int[]> list = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            int[] cur = intervals[i];
            if (cur[0] <= prev[1]) {
                prev[1] = Math.max(cur[1], prev[1]);
            } else {
                list.add(prev);
                prev = cur;
            }
        }
        list.add(prev);
        int size = list.size();
        int[][] result = new int[size][2];
        for (int i = 0; i < size; i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
