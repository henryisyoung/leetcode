package datastructure.binarySearch;

public class SearchTwoDMatrix {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) return false;

        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = m * n - 1;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            int row = mid / n, col = mid % n;
            if (matrix[row][col] == target) return true;
            else if (matrix[row][col] < target) left = mid;
            else right = mid;
        }

        int row = left / n, col = left % n;
        if (matrix[row][col] == target) return true;
        row = right / n;
        col = right % n;
        if (matrix[row][col] == target) return true;

        return false;
    }
}
