package leetcode.highFrequency;

import java.util.*;

public class SubmatrixSum {
    public int[][] submatrixSum(int[][] matrix) {
        // write your code here
        int[][] result = new int[2][2];
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return result;
        }
        int m = matrix.length, n = matrix[0].length;
        int[][] sum = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                sum[i][j] = matrix[i - 1][j - 1] + sum[i - 1][j] + sum[i][j - 1] - sum[i - 1][j - 1];
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j <= m; j++) {
                Map<Integer, Integer> map = new HashMap<>();
                for (int k = 0; k <= n; k++) {
                    int diff = sum[j][k] - sum[i][k];
                    if (map.containsKey(diff)) {
                        int c = map.get(diff);
                        int[] a1 = new int[]{i, c}, a2 = new int[]{j - 1, k - 1};
                        result[0] = a1;
                        result[1] = a2;
                        return result;
                    } else {
                        map.put(diff, k);
                    }
                }
            }
        }
        return result;
    }
}
