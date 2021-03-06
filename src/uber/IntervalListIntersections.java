package uber;

import java.util.*;

public class IntervalListIntersections {

    public int[][] intervalIntersection(int[][] A, int[][] B) {
        List<int[]> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < A.length && j < B.length) {
            int start = Math.max(A[i][0], B[j][0]);
            int end = Math.min(A[i][1], B[j][1]);
            if (start <= end) {
                result.add(new int[]{start, end});
            }
            if (A[i][1] < B[j][1]) {
                i++;
            } else {
                j++;
            }
        }
        return result.toArray(new int[result.size()][]);
    }

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
            if (prev[1] < cur[0]) {
                list.add(prev);
                prev = cur;
            } else {
                prev[1] = Math.max(cur[1], prev[1]);
            }
        }
        list.add(prev);
        return list.toArray(new int[list.size()][]);
    }
    public static int[][] merge2(int[][] A, int[][] B) {
        if (A == null || A.length == 0 || A[0] == null || A[0].length == 0){
            return B;
        }
        if (B == null || B.length == 0 || B[0] == null || B[0].length == 0){
            return A;
        }
        int m = A.length, n = B.length;
        int i = 0, j = 0;

        List<int[]> list = new ArrayList<>();
        while (i < m && j < n) {
            if (A[i][0] < B[j][0]) {
                list.add(A[i++]);
            } else {
                list.add(B[j++]);
            }
        }
        while (i < m) list.add(A[i++]);
        while (j < n) list.add(B[j++]);

        int[] prev = list.get(0);
        List<int[]> result = new ArrayList<>();
        for (int k = 1; k < list.size(); k++) {
            int[] cur  = list.get(k);
            if (prev[1] < cur[0]) {
                result.add(prev);
                prev = cur;
            } else {
                prev[1] = Math.max(cur[1], prev[1]);
            }
        }
        result.add(prev);
        return result.toArray(new int[result.size()][]);
    }

    public static void main(String[] args) {
//        int[][] A = {{0,2},{5,10},{13,23},{24,25}}, B = {{1,5},{8,12},{15,24},{25,26}};
//        int[][] A2 = {{5,10}}, B2 = {{5,10}};
//        IntervalListIntersections solution = new IntervalListIntersections();
//        int[][] result = solution.intervalIntersection(A, B);
//        int[][] result2 = solution.intervalIntersection(A2, B2);
//        System.out.println(Arrays.deepToString(result));
//        System.out.println(Arrays.deepToString(result2));
        int[][] set1 = {{1, 2} ,{4, 7}, {9, 10}}, set2 ={ {2, 3}, {7, 9}};
        int[][] result3 = merge2(set1, set2);
        System.out.println(Arrays.deepToString(result3));
    }
}
