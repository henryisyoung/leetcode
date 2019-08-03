package array;

import java.util.*;

public class SpiralMatrix {
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();

        int r = 0, c = 0, m = matrix.length, n = matrix[0].length;

        while (m > 0 && n > 0) {
            if (m == 1) {
                for (int i = 0; i < n; i++) {
                    result.add(matrix[r][c++]);
                }
            } else if (n == 1) {
                for (int j = 0; j < m; j++) {
                    result.add(matrix[r++][c]);
                }
            } else {
                for (int i = 0; i < n - 1; i++) {
                    result.add(matrix[r][c++]);
                }
                for (int j = 0; j < m - 1; j++) {
                    result.add(matrix[r++][c]);
                }
                for (int i = n - 1; i > 0; i--) {
                    result.add(matrix[r][c--]);
                }
                for (int j = m - 1; j > 0; j--) {
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
