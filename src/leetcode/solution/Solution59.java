package leetcode.solution;

import java.util.Arrays;

public class Solution59 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution59 t = new Solution59();
		int[][] arr= {{1,2,3},{8,9,4},{7,6,5}};
		System.out.println(Arrays.toString(t.generateMatrix(3)[1]));
	}
    public int[][] generateMatrix(int n) {
    	int[][] arr = new int[n][n];
        if(n==0) return arr;
        
        int count=1,r=0,c=0;
        while(n>0){
        	if(n==1) arr[r][c]=count;
        	else{
        		for(int i=0;i<n-1;i++){
        			arr[r][c++]=count;
        			count++;
        		}
        		for(int i=0;i<n-1;i++){
        			arr[r++][c]=count;
        			count++;
        		}	
        		for(int i=0;i<n-1;i++){
        			arr[r][c--]=count;
        			count++;
        		}
        		for(int i=0;i<n-1;i++){
        			arr[r--][c]=count;
        			count++;
        		}
        		r++;c++;
        	}n-=2;
        }
        return arr;
    }
}
