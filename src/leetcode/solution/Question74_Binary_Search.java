package leetcode.solution;

public class Question74_Binary_Search {
    public boolean searchMatrix(int[][] matrix, int target) {
        if(matrix == null || matrix.length == 0){
            return false;
        }
        if(matrix[0] == null || matrix[0].length == 0){
            return false;
        }
        int rows = matrix.length;
        int cols = matrix[0].length;
        int start = 0;
        int end = rows * cols - 1;
        if(matrix[0][0]>target) return false;
        if(matrix[rows-1][cols-1]<target) return false;
        while(start + 1 < end){
            int mid = start + (end - start)/2;
            int r = mid/cols;
            int c = mid%cols;
            if(matrix[r][c] == target){
                return true;
            }else if(matrix[r][c] > target){
                end = mid;
            }else {
                start = mid;
            }
        }
        int r1 = start/cols, c1 = start%cols;
        int r2 = end/cols, c2 = end%cols;
        return matrix[r1][c1] == target || matrix[r2][c2] == target;
    }
}
