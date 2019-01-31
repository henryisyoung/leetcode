package leetcode.binarySearch;

public class KthSmallestElementInSortedMatrix {
//    public int kthSmallest(int[][] matrix, int k) {
//        if (matrix == null || matrix.length == 0
//                || matrix[0] == null || matrix[0].length == 0) {
//            return 0;
//        }
//        int n = matrix.length, m = matrix[0].length;
//        int start = matrix[0][0], end = matrix[n - 1][m - 1];
//        while (start + 1 < end) {
//            int mid = start + (end - start) / 2;
//            int temp = 0;
//            for (int i = 0; i < n; i++) {
//                temp += findLessEqual(matrix[i], mid);
//            }
//            if (temp == k) {
//                return mid;
//            } else if (temp < k) {
//                start = mid;
//            } else {
//                end = mid;
//            }
//        }
//        return start;
//    }
//
//    private int findLessEqual(int[] matrix, int target) {
//        int start = 0, end =matrix.
//    }
}
