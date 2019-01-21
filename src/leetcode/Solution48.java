package leetcode;

public class Solution48 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public void rotate(int[][] matrix) {
        int rows=matrix.length;
        for(int i=0;i<rows/2;i++){
        	for(int j=i;j<rows-i-1;j++){
        		int tmp=matrix[i][j];
        		matrix[i][j]=matrix[rows-1-j][i];
        		matrix[rows-1-j][i]=matrix[rows-1-i][rows-1-j];
        		matrix[rows-1-i][rows-1-j]=matrix[j][rows-1-i];
        		matrix[j][rows-1-i]=tmp;
        	}
        }
    }
}
