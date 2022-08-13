package databricks;

import java.util.ArrayList;
import java.util.List;

public class ProductOfSparseMatrix {
//    题就是 product of sparse matrix
//    怎么表达sparse matrix我用map< row number, map< column number,no-zero value>>
//    他给了一个idea是用3个数组表示；val[]， col[]， row[]
class SparseMatrix {
    List<Integer> values, rowsIndex, colsIndex;

    // Compressed Sparse Row
    public SparseMatrix(int[][] matrix) {
        this.values = new ArrayList<>();
        this.rowsIndex = new ArrayList<>();
        this.colsIndex = new ArrayList<>();
        rowsIndex.add(0);
        int rows = matrix.length, cols = matrix[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    values.add(matrix[i][j]);
                    colsIndex.add(j);
                }
            }
            rowsIndex.add(colsIndex.size());
        }
    }

    // Compressed Sparse Column
    public SparseMatrix(int[][] matrix, boolean colWise) {
        this.values = new ArrayList<>();
        this.rowsIndex = new ArrayList<>();
        this.colsIndex = new ArrayList<>();
        colsIndex.add(0);
        int rows = matrix.length, cols = matrix[0].length;
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                if (matrix[i][j] != 0) {
                    values.add(matrix[i][j]);
                    rowsIndex.add(i);
                }
            }
            colsIndex.add(rowsIndex.size());
        }
    }
};


    public int[][] multiply(int[][] mat1, int[][] mat2) {
        SparseMatrix m1 = new SparseMatrix(mat1);
        SparseMatrix m2 = new SparseMatrix(mat2, true);
        int rows = mat1.length, cols = mat2[0].length;
        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                List<Integer> rowsIndex = m1.rowsIndex;
                List<Integer> colsIndex = m2.colsIndex;

                int rowStart = rowsIndex.get(i), rowEnd = rowsIndex.get(i + 1);
                int colStart = colsIndex.get(j), colEnd = colsIndex.get(j + 1);

                while (rowStart < rowEnd && colStart < colEnd) {
                    if (m1.colsIndex.get(rowStart) < m2.rowsIndex.get(colStart)) {
                        rowStart++;
                    } else if (m1.colsIndex.get(rowStart) > m2.rowsIndex.get(colStart)) {
                        colStart++;
                    } else {
                        result[i][j] += m1.values.get(rowStart) * m2.values.get(colStart);
                        rowStart++;
                        colStart++;
                    }
                }
            }
        }
        return result;
    }
}
