package leetcode;

public class Question63_DP {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        if(obstacleGrid == null || obstacleGrid.length == 0) return 0;
        if(obstacleGrid[0] == null || obstacleGrid[0].length == 0) return 0;
        int m = obstacleGrid.length, n = obstacleGrid[0].length;
        int[][] f = new int[m][n];
        int i = 0;
        while (i < m && obstacleGrid[i][0] != 1) {
            f[i][0] = 1;
            i++;
        }
        i = 0;
        while (i < n && obstacleGrid[0][i] != 1) {
            f[0][i] = 1;
            i++;
        }
        for(i = 1; i < m; i++){
            for (int j = 1; j < n ;j++){
                if(obstacleGrid[i][j] != 1){
                    f[i][j] = f[i-1][j] + f[i][j-1];
                }
            }
        }
        return f[m-1][n-1];
    }
}
