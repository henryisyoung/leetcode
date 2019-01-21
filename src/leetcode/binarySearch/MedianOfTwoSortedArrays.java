package leetcode.binarySearch;

public class MedianOfTwoSortedArrays {
    public double findMedianSortedArrays(int A[], int B[]) {
        int m = A.length, n = B.length;
        if ((m + n) % 2 == 0) {
            int d1 = fintKth(A, 0, B, 0, (m + n)/2);
            int d2 = fintKth(A, 0, B, 0, (m + n)/2 + 1);
            return 0.5 * (d1 + d2);
        }else {
            return (double) fintKth(A, 0, B, 0, (m + n)/2 + 1);
        }
    }

    private int fintKth(int[] A, int aStart, int[] B, int bStart, int k) {
        if(aStart >= A.length){
            return B[bStart + k - 1];
        }
        if(bStart >= B.length){
            return A[aStart + k - 1];
        }
        if (k == 1){
            return Math.min(A[aStart], B[bStart]);
        }
        int aValue = aStart + k/2 - 1>= A.length ? Integer.MAX_VALUE : A[aStart + k/2 - 1];
        int bValue = bStart + k/2 - 1>= B.length ? Integer.MAX_VALUE : B[bStart + k/2 - 1];
        if ( aValue < bValue){
            return fintKth(A, aStart + k/2, B, bStart, k - k/2);
        }else {
            return fintKth(A, aStart, B, bStart + k/2, k - k/2);
        }
    }

}
