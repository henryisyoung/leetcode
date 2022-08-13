package uber.phone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeSeriesData {
    public static List<int[]> aggregateData(List<int[]> first, List<int[]> second) {
        int i = 0, j = 0, m = first.size(), n = second.size();
        List<int[]> result = new ArrayList<>();
        while (i < m && j < n) {
            int[] a = first.get(i), b = second.get(j);
            int aT = a[0], aVal = a[1];
            int bT = b[0], bVal = b[1];
            if (aT == bT) {
                result.add(new int[]{aT,  aVal + bVal});
                i++;
                j++;
            } else if (aT < bT) {
                result.add(new int[]{aT,  aVal + bVal});
                i++;
            } else {
                result.add(new int[]{bT,  aVal + bVal});
                j++;
            }
        }
        while (i < m) {
            result.add(first.get(i++));
        }
        while (j < n) {
            result.add(second.get(j++));
        }
        return result;
    }

    public static void main(String[] args) {
        List<int[]> first = Arrays.asList(
                new int[]{1,3},
                new int[]{3,1},
                new int[]{6,4},
                new int[]{10,1}
                );
        List<int[]> second = Arrays.asList(
                new int[]{2,3},
                new int[]{6,3},
                new int[]{11,2}
        );

        List<int[]> result = aggregateData(first, second);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }
}
