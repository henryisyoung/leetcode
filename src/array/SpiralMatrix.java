package array;

import java.util.*;

public class SpiralMatrix {
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return result;
        }
        int m = matrix.length, n = matrix[0].length;
        int r = 0, c = 0;
        while (m > 0 && n > 0) {
            if (m == 1) {
                for (int i = 0; i < n; i++) {
                    result.add(matrix[r][c++]);
                }
            } else if (n == 1) {
                for (int i = 0; i < m; i++) {
                    result.add(matrix[r++][c]);
                }
            } else {
                for (int i = 0; i < n - 1; i++) {
                    result.add(matrix[r][c++]);
                }
                for (int i = 0; i < m - 1; i++) {
                    result.add(matrix[r++][c]);
                }
                for (int i = 0; i < n - 1; i++) {
                    result.add(matrix[r][c--]);
                }
                for (int i = 0; i < m - 1; i++) {
                    result.add(matrix[r--][c]);
                }
                r++;
                c++;
            }
            m -= 2;
            n -= 2;
        }

        return result;
    }
}
