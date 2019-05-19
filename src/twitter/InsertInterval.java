package twitter;

import java.util.*;

public class InsertInterval {
    public static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int pos = 0;
        int[] insert = new int[2];
        for (int[] arr : intervals) {
            if (arr[1] < newInterval[0]) {
                result.add(arr);
                pos++;
            } else if (arr[0] > newInterval[1]) {
                result.add(arr);
            } else {
                insert[0] = Math.min(arr[0], newInterval[0]);
                insert[1] = Math.max(arr[1], newInterval[1]);
                newInterval = insert;
            }
        }
        result.add(pos, newInterval);
        int size = result.size();
        int[][] ans = new int[size][2];
        for (int i = 0; i < size; i++) {
            ans[i] = result.get(i);
        }
        return ans;
    }

    public static void main(String[] args) {
        int[][] intervals = {{1,2},{3,5},{6,7},{8,10},{12,16}};
        int[] newInterval = {4, 8};
        System.out.println(Arrays.deepToString(insert(intervals, newInterval)));
    }
}
