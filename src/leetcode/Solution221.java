package leetcode;

public class Solution221 {

	public static void main(String[] args) {
		char[][] matrix = {{'1'}};
		Solution221 t = new Solution221();
		System.out.println(t.maximalSquare(matrix));
	}
	
    public int maximalSquare(char[][] matrix) {
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0){
        	return 0;
        }
        int m = matrix.length, n = matrix[0].length;
        int[][] dp = new int[m][n];
        int rlt = 0;
        for(int i = 0; i < m; i++){
        	dp[i][0] = matrix[i][0] - '0';
        	rlt = Math.max(rlt, dp[i][0]);
        }
        for(int j = 0; j < n; j++){
        	dp[0][j] = matrix[0][j] - '0';
        	rlt = Math.max(rlt, dp[0][j]);
        }
        
        for(int i = 1; i < m; i++){
        	for(int j = 1; j < n; j++){
        		dp[i][j] = 0;
        		if(matrix[i][j] != '0'){
        			//System.out.println("asd");
        			dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1], dp[i][j - 1]),dp[i - 1][j]) + 1;
        		}
        		rlt = Math.max(rlt, dp[i][j]);
        	}
        }
        return rlt*rlt;
    }
    
    public int maximalSquare2(char[][] matrix) {
        int ans = 0;
        int n = matrix.length;
        int m;
        if(n > 0)
            m = matrix[0].length;
        else 
            return ans;
        int [][]res = new int [2][m];
        for(int i = 0; i < n; i++){
            res[i%2][0] = matrix[i][0] - '0';
            ans = Math.max(res[i%2][0] , ans);
            for(int j = 1; j < m; j++) {
                if(i > 0) {
                    if(matrix[i][j] > '0') {
                        res[i%2][j] = Math.min(res[(i - 1)%2][j],Math.min(res[i%2][j-1], res[(i-1)%2][j-1])) + 1;
                    } else {
                        res[i%2][j] = 0;
                    }
                    
                }
                else {
                    res[i%2][j] = matrix[i%2][j] - '0';
                }
                ans = Math.max(res[i%2][j], ans);
            }
        }
        return ans*ans;
    }
}
