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
            if (A[i][0] < B[j][0]) {
                i++;
            } else {
                j++;
            }
        }
        int n = result.size();
        int[][] nums = new int[n][2];
        int index = 0;
        for (int[] arr : result) {
            nums[index++] = arr;
        }
        return nums;
    }
    public List<int[]> intervalUnionsection(int[][] A, int[][] B) {
        List<int[]> result = new ArrayList<>();
        // Merge Intervals
        return result;
    }

    public static void main(String[] args) {
        int[][] A = {{0,2},{5,10},{13,23},{24,25}}, B = {{1,5},{8,12},{15,24},{25,26}};
        int[][] A2 = {{5,10}}, B2 = {{5,10}};
        IntervalListIntersections solution = new IntervalListIntersections();
        int[][] result = solution.intervalIntersection(A, B);
        int[][] result2 = solution.intervalIntersection(A2, B2);
        System.out.println(Arrays.deepToString(result));
        System.out.println(Arrays.deepToString(result2));
    }
}
