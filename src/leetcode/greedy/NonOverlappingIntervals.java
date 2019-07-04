package leetcode.greedy;

import java.util.Arrays;
import java.util.Comparator;

public class NonOverlappingIntervals {
    public static int eraseOverlapIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }
        int result = 0;
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[0] != o2[0]) {
                    return o1[0] - o2[0];
                } else {
                    return o1[1] - o2[1];
                }
            }
        });
        System.out.println(Arrays.deepToString(intervals));
        int last  = 0;
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[last][1]) {
                ++result;
                if (intervals[i][1] < intervals[last][1]) last = i;
            } else {
                last = i;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] ints = { {1,2}, {2,3}, {3,4}, {1,3} };
        System.out.println(eraseOverlapIntervals(ints));
    }
}
