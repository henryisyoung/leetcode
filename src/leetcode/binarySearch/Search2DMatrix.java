package leetcode.binarySearch;

public class Search2DMatrix {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null ){
            return false;
        }
        if(matrix.length == 0 || matrix[0] == null || matrix[0].length == 0){
            return false;
        }
        int m = matrix.length, n = matrix[0].length;
        int start = 0, end = m * n - 1;
        if(matrix[0][0] > target || matrix[m - 1][n - 1] < target){
            return false;
        }
        while(start + 1 < end){
            int mid = start + (end - start)/2;
            int r = mid/n, c = mid%n;
            if(matrix[r][c] == target){
                return true;
            } else if(matrix[r][c] < target){
                start = mid;
            }else {
                end = mid;
            }
        }
        int r = start/n, c = start%n;
        if(matrix[r][c] == target) return true;
        r = end/n;
        c = end%n;
        if(matrix[r][c] == target) return true;
        return false;
    }
}
