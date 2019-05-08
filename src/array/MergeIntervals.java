package array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MergeIntervals {
    public static int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[][]{};
        }
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        List<int[]> result = new ArrayList<>();
        int[] prev = intervals[0];
        for (int i = 1; i < intervals.length; i++) {
            int[] cur = intervals[i];
            if (cur[0] <= prev[1]) {
                int[] merger = {prev[0], Math.max(cur[1], prev[1])};
                prev = merger;
            } else {
                result.add(prev);
                prev = cur;
            }
        }
        result.add(prev);
        int[][] ans = new int[result.size()][2];
        int i = 0;
        for (int[] list : result) {
//            System.out.println(Arrays.toString(list));
            ans[i++] = list;
        }
        return ans;
    }

    public static void main(String[] args) {
        int[][] ints = {{1,3},{8,10},{15,18},{2,6}};
        System.out.println(merge(ints));
    }
}
