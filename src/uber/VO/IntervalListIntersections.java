package uber.VO;

import java.util.ArrayList;
import java.util.List;

public class IntervalListIntersections {
    public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        int i = 0, j = 0;
        int m = firstList.length, n = secondList.length;
        if(m * n == 0) {
            return new int[0][];
        }
        List<int[]> list = new ArrayList<>();
        while (i < m && j < n) {
            int[] array1 = firstList[i], array2 = secondList[j];
            if (Math.min(array1[1], array2[1]) >= Math.max(array1[0], array2[0])) {
                list.add(new int[]{Math.max(array1[0], array2[0]), Math.min(array1[1], array2[1])});
            }
            if (array1[0] < array2[0]) {
                i++;
            } else {
                j++;
            }
        }
        int size = list.size();
        int[][] result = new int[size][2];
        for (i = 0;i < size; i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
