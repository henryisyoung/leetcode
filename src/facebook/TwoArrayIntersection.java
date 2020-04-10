package facebook;

import java.util.ArrayList;
import java.util.List;

public class TwoArrayIntersection {
    public List<Integer> intersection(int[] A, int[] B) {
        int n = A.length, m = B.length;
        int i = 0, j = 0;
        List<Integer> list = new ArrayList<>();
        while (i < n && j < m) {
            if (A[i] < B[j]) i++;
            else if (A[i] > B[j]) j++;
            else {
                list.add(A[i]);
                i++; j++;
            }
        }
        return list;
    }
}
