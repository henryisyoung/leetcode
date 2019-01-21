package leetcode;

public class Solution240 {
    public boolean searchMatrix(int[][] matrix, int target) {
        if(matrix == null || matrix.length == 0) return false;
        if(matrix[0] == null || matrix[0].length == 0) return false;
        int m = matrix.length, n = matrix[0].length;
        int r = m - 1, c = 0;
        while(r >=0 && c < n){
        	if(matrix[r][c] == target){
        		return true;
        	}else if(matrix[r][c] < target){
        		c++;
        	}else if(matrix[r][c] > target){
        		r--;
        	}
        }
        return false;
    }
}
