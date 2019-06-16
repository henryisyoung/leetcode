package leetcode.twoPointer;

import java.util.Arrays;

public class DuplicateZeros {
    public static void duplicateZeros(int[] A) {
        int n = A.length, i = 0, j = 0;
        for (i = 0; i < n; ++i, ++j) {
            if (A[i] == 0) ++j;
        }
        for (i = i - 1; i >= 0; --i) {
            if (--j < n)
                A[j] = A[i];
            if (A[i] == 0 && --j < n)
                A[j] = 0;
        }
    }

    public static void main(String[] args) {
        int[] A = {1,0,2,3};
        duplicateZeros(A);
    }
}
