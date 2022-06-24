package datastructure.interval;

import java.util.ArrayList;
import java.util.List;

public class IntervalListIntersections {
    public int[][] intervalIntersection(int[][] A, int[][] B) {
        int n = A.length, m = B.length;
        List<int[]> result = new ArrayList<>();
        int i = 0, j = 0;
        while(i < n && j < m) {
            int start = Math.max(A[i][0], B[j][0]), end = Math.min(A[i][1], B[j][1]);
            if(end >= start) {
                result.add(new int[]{start, end});
            }
            if(A[i][1] < B[j][1]) {
                i++;
            } else {
                j++;
            }
        }
        int[][] array = new int[result.size()][2];

        return result.toArray(array);
    }

}
