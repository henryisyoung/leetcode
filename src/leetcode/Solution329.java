package leetcode;

public class Solution329 {
	
	public static void main(String[] args) {
		Solution329 t = new Solution329();
		int[][] matrix = {{9,9,4},{6,6,8},{2,1,1}};
		System.out.println(t.longestIncreasingPath(matrix));
	}
	
	private int[][] dp;
	private int[][] isVisted;
	
    public int longestIncreasingPath(int[][] matrix) {
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0){
        	return 0;
        }
        int rows = matrix.length, cols = matrix[0].length;
        int ans = 0;
        
        dp = new int[rows][cols];
        isVisted = new int[rows][cols];
        
        for(int i = 0; i < rows; i++){
        	for(int j = 0; j < cols; j++){
        		dp[i][j] = search(matrix, i, j);
        		ans = Math.max(ans, dp[i][j]);
        	}
        }
        return ans;
    }
    
	private int search(int[][] matrix, int x, int y) {
		int[][] dir = {{-1, 0},{+1, 0},{0, -1},{0, +1}};
		if(isVisted[x][y] == 1){
			return dp[x][y];
		}
		int ans = 1;
		for(int k = 0; k < 4; k++){
			int nx = x + dir[k][0];
			int ny = y + dir[k][1];
			if(nx < matrix.length && nx >= 0 && ny < matrix[0].length && ny >= 0){
				if(matrix[nx][ny] < matrix[x][y]){
					ans = Math.max(ans, search(matrix, nx, ny) + 1);
				}
			}
		}
		dp[x][y] = ans;
		isVisted[x][y] = 1;
		return ans;
	}
}
