package recovery;

public class Search2DMatrix2 {
    public boolean searchMatrix(int[][] matrix, int target) {
        int rows = matrix.length, cols = matrix[0].length;
        int sr = rows - 1, sc = 0;
        while (sr >= 0 && sc < cols) {
            if (matrix[sr][sc] == target) {
                return true;
            } else if (matrix[sr][sc] > target) {
                sr--;
            } else {
                sc++;
            }
        }
        return false;
    }
}
