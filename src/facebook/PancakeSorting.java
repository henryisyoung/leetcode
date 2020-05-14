package facebook;

import java.util.ArrayList;
import java.util.List;

public class PancakeSorting {
    public List<Integer> pancakeSort(int[] A) {
        List<Integer> result = new ArrayList<>();
        if (A == null || A.length == 0) return result;
        int n = A.length;
        for (int i = n; i >= 1; i--) {
            // put i on place i
            int index = 0;
            while (A[index] != i && index < n) index++;
            if (index == i - 1) continue;
            flip(index + 1, A, result);
            flip(i, A, result);
        }
        return result;
    }

    void flip(int k, int[] A, List<Integer> result) {
        for (int i = 0; i < k - i - 1; i++){
            int temp = A[i];
            A[i] = A[k - i - 1];
            A[k - i - 1] = temp;
        }
        result.add(k);
    }
}
