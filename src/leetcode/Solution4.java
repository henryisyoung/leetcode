package leetcode;

public class Solution4 {

	public static void main(String[] args) {
		Solution4 t = new Solution4();
		int[] a = {1,2};
		int[] b = {1,2};
		System.out.println(t.findMedianSortedArrays2(a, b));

	}
	public double findMedianSortedArrays2(int A[], int B[]) {
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
		/***int A_val = 0, B_val = 0;
		if(A_start + k/2 - 1 >= A.length){
			A_val = Integer.MAX_VALUE;
		}else{
			A_val = A[A_start + k/2 - 1];
		}
		if(B_start + k/2 - 1 >= B.length){
			B_val = Integer.MAX_VALUE;
		}else{
			B_val = B[B_start + k/2 - 1];
		}**///
		if(k == 1) return Math.min(A[A_start], B[B_start]);
		int A_val = (A_start + k/2 - 1 < A.length)?A[A_start + k/2 - 1]:Integer.MAX_VALUE;
		int B_val = (B_start + k/2 - 1 < B.length)?B[B_start + k/2 - 1]:Integer.MAX_VALUE;
		if(A_val > B_val){
			return helper(A, A_start, B, B_start + k/2, k - k/2);
		}else{
			return helper(A, A_start + k/2, B, B_start, k - k/2);
		}
	}
	public double findMedianSortedArrays(int A[], int B[]) {
	        int lengthA = A.length;
	        int lengthB = B.length;
	        if ((lengthA + lengthB) % 2 == 0) {
	            double r1 = (double) findMedianSortedArrays(A, 0, lengthA, B, 0, lengthB, (lengthA + lengthB) / 2);
	            double r2 = (double) findMedianSortedArrays(A, 0, lengthA, B, 0, lengthB, (lengthA + lengthB) / 2 + 1);
	            return (r1 + r2) / 2;
	        } else
	            return findMedianSortedArrays(A, 0, lengthA, B, 0, lengthB, (lengthA + lengthB + 1) / 2);
	    }

	    public int findMedianSortedArrays(int A[], int startA, int endA, int B[], int startB, int endB, int k) {
	        int n = endA - startA;
	        int m = endB - startB;

	        if (n <= 0)
	            return B[startB + k - 1];
	        if (m <= 0)
	            return A[startA + k - 1];
	        if (k == 1)
	            return A[startA] < B[startB] ? A[startA] : B[startB];

	        int midA = (startA + endA) / 2;
	        int midB = (startB + endB) / 2;

	        if (A[midA] <= B[midB]) {
	            if (n / 2 + m / 2 + 1 >= k)
	                return findMedianSortedArrays(A, startA, endA, B, startB, midB, k);
	            else
	                return findMedianSortedArrays(A, midA + 1, endA, B, startB, endB, k - n / 2 - 1);
	        } else {
	            if (n / 2 + m / 2 + 1 >= k)
	                return findMedianSortedArrays(A, startA, midA, B, startB, endB, k);
	            else
	                return findMedianSortedArrays(A, startA, endA, B, midB + 1, endB, k - m / 2 - 1);

	        }
	    }
}
