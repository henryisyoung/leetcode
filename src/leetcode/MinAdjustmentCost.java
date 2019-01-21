package leetcode;

import java.util.ArrayList;

public class MinAdjustmentCost {
    public int MinAdjustmentCost(ArrayList<Integer> A, int target) {
       if(A == null || A.size() == 0){
    	   return 0;
       }
       int n = A.size();
       int max = 0;
       for(int i : A){
    	   max = Math.max(max, i);
       }
       int[][] dp = new int[n][max + 1];
       for(int j = 0; j <= max; j++){
    	   dp[0][j] = Math.abs(A.get(0) - j);  
       }
       int curMin = 0;
       for(int i = 1; i < n; i++){
    	   curMin = Integer.MAX_VALUE;
    	   for(int j = 0; j <= max; j++){
    		   dp[i][j] = Integer.MAX_VALUE;
    		   for(int k = Math.max(0, j - target); k <= Math.min(max, j + target); k++){
    			   dp[i][j] = Math.min(dp[i][j], dp[i-1][k] + Math.abs(j - A.get(i)));
    			   curMin = Math.min(curMin, dp[i][j]);  
    		   }
    	   }
       }
       return curMin;  
    }
}
