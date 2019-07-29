package uber;

import java.util.Arrays;

public class SquaresOfSortedArray {
    public static int[] sortedSquares(int[] A) {
        if (A == null || A.length == 0) return A;

        int n = A.length, pos = n;
        int[] result = new int[n];
        for (int i = 0; i < A.length; i++) {
            if (A[i] >= 0) {
                pos = i;
                break;
            }
        }
        int i = pos - 1, j = pos, index = 0;
        while (i >= 0 && j < n) {
            if (A[i] * A[i] < A[j] * A[j]) {
                result[index++] = A[i] * A[i];
                i--;
            } else {
                result[index++] = A[j] * A[j];
                j++;
            }
        }
        while (i >= 0) {
            result[index++] = A[i] * A[i];
            i--;
        }
        while (j < n) {
            result[index++] = A[j] * A[j];
            j++;
        }
        return result;
    }

    public static void main(String[] args) {
        int[] A = {-4,-1,0,3,10};
        int[] A2 = {-7,-3,2,3,11};
        int[] A3 = {-7,-3,-2};
        int[] result = sortedSquares(A);
        int[] result2 = sortedSquares(A2);
        int[] result3 = sortedSquares(A3);
        System.out.println(Arrays.toString(result3));
    }
}
