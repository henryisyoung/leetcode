package intuit;

import java.util.ArrayList;
import java.util.List;

public class SpiralMatrix {
    public List<Integer> spiralOrder(int[][] matrix) {
        int r = 0, c = 0, rows = matrix.length, cols = matrix[0].length;
        List<Integer> result = new ArrayList<>();
        while (rows > 0 && cols > 0) {
            if (rows == 1) {
                for (int i = 0; i < cols; i++) {
                    result.add(matrix[r][c++]);
                }
            } else if (cols == 1) {
                for (int i = 0; i < rows; i++) {
                    result.add(matrix[r++][c]);
                }
            } else {
                for (int i = 0; i < cols - 1; i++) {
                    result.add(matrix[r][c++]);
                }
                for (int i = 0; i < rows - 1; i++) {
                    result.add(matrix[r++][c]);
                }
                for (int i = 0; i < cols - 1; i++) {
                    result.add(matrix[r][c--]);
                }
                for (int i = 0; i < rows - 1; i++) {
                    result.add(matrix[r--][c]);
                }

            }
            r++;
            c++;
            cols -= 2;
            rows -= 2;
        }
        return result;
    }
}
