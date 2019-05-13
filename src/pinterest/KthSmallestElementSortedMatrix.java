package pinterest;

public class KthSmallestElementSortedMatrix {
    public static int kthSmallest(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return 0;
        }
        int m = matrix.length, n = matrix[0].length;
        if (m * n < k) {
            return 0;
        }
        int left = matrix[0][0], right = matrix[m - 1][n - 1];
        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = 0;
            for (int[] nums : matrix) {
                count += lessEqualThanMid(mid, nums);
            }
            if (count >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private static int lessEqualThanMid(int val, int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= val) {
                left = mid;
            } else {
                right = mid;
            }
        }
        if (nums[left] > val) {
            return left;
        }
        if (nums[right] > val) {
            return right;
        }
        return nums.length;
    }

    public static int kthSmallestBetter(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return 0;
        }
        int m = matrix.length, n = matrix[0].length;
        if (m * n < k) {
            return 0;
        }
        int left = matrix[0][0], right = matrix[m - 1][n - 1];
        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = lessEqualThanMidAllMatrix(mid, matrix);
            if (count >= k) {
                right = mid;
            }else {
                left = mid + 1;
            }
        }
        return left;
    }

    private static int lessEqualThanMidAllMatrix(int val, int[][] matrix) {
        int count = 0;
        int rows = matrix.length, cols = matrix[0].length;
        int r = rows - 1, c = 0;
        while (r >= 0 && c < cols) {
            if (matrix[r][c] > val) {
                r--;
            } else {
                count += r + 1;
                c++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums= {1,2,11,11};
        int val = 5, k = 8;
        int[][] matrix = {
                { 1,  5,  9},
                {10, 11, 13},
                {12, 13, 15}};
        System.out.println(kthSmallest(matrix, k));
    }
}
