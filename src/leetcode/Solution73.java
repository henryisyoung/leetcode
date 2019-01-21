package leetcode;

public class Solution73 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public void setZeroes(int[][] matrix) {
        if(matrix==null || matrix.length==0) return;
        if(matrix[0]==null || matrix[0].length==0) return;
        int m=matrix.length,n=matrix[0].length;
        boolean firstRow=false,firstCol=false;
        for(int i=0;i<m;i++){
        	if(matrix[i][0]==0){
        		firstCol=true;
        		break;
        	}
        }
        for(int j=0;j<n;j++){
        	if(matrix[0][j]==0){
        		firstRow=true;
        		break;
        	}
        }
        for(int i=1;i<m;i++){
        	for(int j=1;j<n;j++){
        		if(matrix[i][j]==0){
        			matrix[i][0]=0;
        			matrix[0][j]=0;
        		}
        	}
        }
        for(int i=1;i<m;i++){
        	if(matrix[i][0]==0){
        		changeRow(matrix,i);
        	}
        }
        for(int j=1;j<n;j++){
        	if(matrix[0][j]==0){
        		changeCol(matrix,j);
        	}
        }
        if(firstRow) changeRow(matrix,0);
        if(firstCol) changeCol(matrix,0);
    }
	private void changeCol(int[][] matrix, int j) {
		// TODO Auto-generated method stub
		int m=matrix.length;
		for(int i=0;i<m;i++){
			matrix[i][j]=0;
		}
	}
	private void changeRow(int[][] matrix, int i) {
		// TODO Auto-generated method stub
		int  m=matrix[0].length;
		for(int j=0;j<m;j++){
			matrix[i][j]=0;
		}
	}
}
