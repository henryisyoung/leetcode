package leetcode.binaryTree;

public class UniqueBinarySearchTrees {
    public int numTrees(int n) {
        if (n == 0) {
            return 0;
        }
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++){
            for(int j = 0; j < i; j++) {
                dp[i] += dp[j] * dp[i - 1 - j];
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        int n = 3;
        UniqueBinarySearchTrees solver = new UniqueBinarySearchTrees();
        System.out.println(solver.numTrees(n));
    }
}
