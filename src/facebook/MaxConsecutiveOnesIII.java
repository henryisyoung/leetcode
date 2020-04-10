package facebook;

public class MaxConsecutiveOnesIII {
    public int longestOnes(int[] A, int K) {
        int i = 0, j = 0, max = 0;

        for (;j < A.length; j++) {
            if(A[j] == 0) K--;
            max = Math.max(max, j - i);
            if (K < 0) {
                if (A[i++] == 0) K++;
            }
        }

        return max;
    }
}
