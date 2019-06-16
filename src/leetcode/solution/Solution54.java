package leetcode.solution;
import java.util.*;
public class Solution54 {

	public static void main(String[] args) {
		Solution54 t= new Solution54();
		int[][] matrix = {{1,2,3},{4,5,6},{7,8,9}};
		int[][] matrix2 = {};
		System.out.println(t.spiralOrder(matrix2));

	}
    public List<Integer> spiralOrder(int[][] matrix) {
    	List<Integer> list = new ArrayList<Integer>();
    	if(matrix.length==0||matrix[0].length==0||matrix==null) return list;
        int m=matrix.length,n=matrix[0].length,r=0,c=0;
        while(m>0&&n>0){
        	if(m==1){
        		for(int i=0;i<n;i++){
        			list.add(matrix[r][c++]);
        		}
        	}else if(n==1){
        		for(int i=0;i<m;i++){
        			list.add(matrix[r++][c]);
        		}
        	}else{
        		for(int i=0;i<n-1;i++){
        			list.add(matrix[r][c++]);
        		}
        		for(int i=0;i<m-1;i++){
        			list.add(matrix[r++][c]);
        		}
        		for(int i=0;i<n-1;i++){
        			list.add(matrix[r][c--]);
        		}
        		for(int i=0;i<m-1;i++){
        			list.add(matrix[r--][c]);
        		}
        		r++;c++;
        	}
        	m-=2;n-=2;
        }
        return list;
    }
}
