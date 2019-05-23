package twitter;

public class KthSmallestElementInSortedMatrix {
    public int kthSmallest(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return 0;
        }
        int rows = matrix.length, cols = matrix[0].length;
        int left = matrix[0][0], right = matrix[rows - 1][cols - 1];
        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = countLessEqual(mid, matrix);
            if (k <= count) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private int countLessEqual(int val, int[][] matrix) {
        int count = 0;
        int rows = matrix.length, cols = matrix[0].length;
        int r = rows - 1, c = 0;
        while (r >= 0 && c < cols) {
            if (matrix[r][c] > val) {
                r--;
            } else {
                c++;
                count += r + 1;
            }
        }
        return count;
    }
}
