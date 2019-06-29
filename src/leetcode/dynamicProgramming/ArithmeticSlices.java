package leetcode.dynamicProgramming;

public class ArithmeticSlices {
    public static int numberOfArithmeticSlices(int[] A) {
        int n = A.length;
        int[] dp = new int[n];
        if (n < 3) return 0;
        int sum = 0;
        for (int i = 2; i < n; i++) {
            if (A[i] - A[i - 1] == A[i - 1] - A[i - 2]) {
                dp[i] = dp[i - 1] + 1;
            }
            sum += dp[i];
        }
        return sum;
    }

    public static void main(String[] args) {
        int[] A = {1, 2, 3, 4};
        System.out.println(numberOfArithmeticSlices(A));
    }
}
