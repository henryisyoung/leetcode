package leetcode;

public class Solution74 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution74 t = new Solution74();
		int[][] matrix={{1,3}};
		System.out.println(t.searchMatrix(matrix, 3));
		int i=0;
		do{++i;}while(i<6);
		System.out.println(i);
	}
    public boolean searchMatrix(int[][] matrix, int target) {
        if(matrix==null || matrix.length==0) return false;
        if(matrix[0]==null || matrix[0].length==0) return false;
        int m=matrix.length,n=matrix[0].length,row=0;
        if(matrix[0][0]>target) return false;
        if(matrix[m-1][n-1]<target) return false;
        int start=0,end=m-1;
        while(start+1<end){
        	int mid = start + (end-start)/2;
        	if(matrix[mid][0]==target) return true;
        	else if(matrix[mid][0]<target){
        		start=mid;
        	}else{
        		end=mid;
        	}
        }
        if(matrix[end][0]<=target) row=end;
        else{
        	row=start;
        	}
        start=0; end=n-1;
        while(start+1<end){
        	int mid = start + (end-start)/2;
        	if(matrix[row][mid]==target) return true;
        	else if(matrix[row][mid]<target){
        		start=mid;
        	}else{
        		end=mid;
        	}
        }
        if(matrix[row][start]==target||matrix[row][end]==target){
        	return true;
        }
        return false;
    }
    
    public boolean searchMatrix2(int[][] matrix, int target) {
        if(matrix==null || matrix.length==0) return false;
        if(matrix[0]==null || matrix[0].length==0) return false;
        int m=matrix.length,n=matrix[0].length;
        if(matrix[0][0]>target) return false;
        if(matrix[m-1][n-1]<target) return false;
        int start=0,end=m*n-1;
        while(start+1<end){
        	int mid = start + (end - start)/2;
        	int r = mid/n, c= mid%n;
        	if(matrix[r][c]==target){
        		return true;
        	}else if(matrix[r][c]>target){
        		end = mid;
        	}else{
        		start = mid;
        	}
        }
        int r = start/n, c= start%n;
        int r2 = end/n, c2= end%n;
        return (matrix[r][c]==target||matrix[r2][c2]==target);
    }
}
