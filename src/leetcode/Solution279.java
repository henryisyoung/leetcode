package leetcode;
import java.util.*;
public class Solution279 {
	public static void main(String[] args) {
		Solution279 t = new Solution279();
		System.out.println(t.numSquares(12));
	}
	
    public int numSquares(int n) {
        if(n == 0){
        	return 0;
        }
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        for(int i = 1; i*i <= n; i++){
        	dp[i*i] = 1;
        }
        for(int i = 1; i <= n; i++){
        	for(int j = 0; j*j + i <=n; j++){
        		dp[i + j*j] = Math.min(dp[i + j*j], dp[i] + 1);
        	}
        }
        return dp[n];
    }
}
