package leetcode.solution;

public class Question4_Sorted_Array {
    public double findMedianSortedArrays(int[] A, int[] B) {
        int len = A.length + B.length;
        if(len%2 == 0){
            return 0.5*(helper(A,0,B,0,len/2) + helper(A,0,B,0,len/2 + 1));
        }else{
            return (double) helper(A,0,B,0,len/2 + 1);
        }

    }

    private int helper(int[] A, int A_start, int[] B, int B_start, int k) {
        if(A_start >= A.length) {
            return B[B_start + k - 1];
        }
        if(B_start >= B.length) {
            return A[A_start + k - 1];
        }
        if(k == 1) return Math.min(A[A_start], B[B_start]);
        int A_val = (A_start + k/2 - 1 < A.length)?A[A_start + k/2 - 1]:Integer.MAX_VALUE;
        int B_val = (B_start + k/2 - 1 < B.length)?B[B_start + k/2 - 1]:Integer.MAX_VALUE;
        if(A_val > B_val){
            return helper(A, A_start, B, B_start + k/2, k - k/2);
        }else{
            return helper(A, A_start + k/2, B, B_start, k - k/2);
        }
    }
}
