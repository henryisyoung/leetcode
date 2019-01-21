package leetcode;

public class Question240_Binary_Search {
    public boolean searchMatrix(int[][] matrix, int target) {
        if(matrix == null || matrix.length == 0) return false;
        if(matrix[0] == null || matrix[0].length == 0) return false;
        int rows = matrix.length;
        int cols = matrix[0].length;
        int r = rows - 1, c = 0;
        while (r >= 0 && c < cols){
            if(matrix[r][c] == target){
                return true;
            }else if(matrix[r][c] < target){
                c++;
            }else {
                r--;
            }
        }
        return false;
    }
}
