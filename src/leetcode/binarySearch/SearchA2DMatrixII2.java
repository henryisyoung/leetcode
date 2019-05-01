package leetcode.binarySearch;

public class SearchA2DMatrixII2 {
    public int searchMatrix(int[][] matrix, int target) {
        // write your code here
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return 0;
        }
        int count = 0;
        int rows = matrix.length, cols = matrix[0].length;
        int r = rows - 1, c = 0;
        while (r >= 0 && c < cols) {
            if (matrix[r][c] < target) {
                c++;
            } else if (matrix[r][c] > target){
                r--;
            } else {
                count++;
                r--;
                c++;
            }
        }
        return count;
    }
}
