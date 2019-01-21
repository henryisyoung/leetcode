package leetcode;

public class Solution289 {
    public void gameOfLife(int[][] board) {
        int m = board.length, n = board[0].length;
		for(int i = 0; i < m; i++){
		    for(int j = 0; j < n; j++){
		        int lives = 0;
		        // 判断上边
		        if(i > 0){
		            lives += board[i - 1][j] == 1 || board[i - 1][j] == 2 ? 1 : 0;
		        }
		        // 判断左边
		        if(j > 0){
		            lives += board[i][j - 1] == 1 || board[i][j - 1] == 2 ? 1 : 0;
		        }
		        // 判断下边
		        if(i < m - 1){
		            lives += board[i + 1][j] == 1 || board[i + 1][j] == 2 ? 1 : 0;
		        }
		        // 判断右边
		        if(j < n - 1){
		            lives += board[i][j + 1] == 1 || board[i][j + 1] == 2 ? 1 : 0;
		        }
		        // 判断左上角
		        if(i > 0 && j > 0){
		            lives += board[i - 1][j - 1] == 1 || board[i - 1][j - 1] == 2 ? 1 : 0;
		        }
		        //判断右下角
		        if(i < m - 1 && j < n - 1){
		            lives += board[i + 1][j + 1] == 1 || board[i + 1][j + 1] == 2 ? 1 : 0;
		        }
		        // 判断右上角
		        if(i > 0 && j < n - 1){
		            lives += board[i - 1][j + 1] == 1 || board[i - 1][j + 1] == 2 ? 1 : 0;
		        }
		        // 判断左下角
		        if(i < m - 1 && j > 0){
		            lives += board[i + 1][j - 1] == 1 || board[i + 1][j - 1] == 2 ? 1 : 0;
		        }
		        // 根据周边存活数量更新当前点，结果是0和1的情况不用更新
		        if(board[i][j] == 0 && lives == 3){
		            board[i][j] = 3;
		        } else if(board[i][j] == 1){
		            if(lives < 2 || lives > 3) board[i][j] = 2;
		        }
		    }
		}
		// 解码
		for(int i = 0; i < m; i++){
		    for(int j = 0; j < n; j++){
		        board[i][j] = board[i][j] % 2;
		    }
		}
	}
}
