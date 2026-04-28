package recovery;

public class Search2DMatrix {
    public boolean searchMatrix(int[][] matrix, int target) {
        int rows = matrix.length, cols = matrix[0].length;
        int left = 0, right = rows * cols - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            int r = mid / cols, c = mid % cols;
            if (matrix[r][c] == target) {
                return true;
            } else if (matrix[r][c] > target) {
                right = mid;
            } else {
                left = mid;
            }
        }
        int leftR = left / cols, leftC = left % cols;
        if (matrix[leftR][leftC] == target) return true;
        int rightR = right / cols, rightC = right % cols;

        return matrix[rightR][rightC] == target;
    }
}
