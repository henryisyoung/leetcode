package Bloomberg;

import java.util.Arrays;

public class printX {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		printX t = new printX();
		char[][] board = t.createX(1);
		System.out.println(Arrays.toString(board[0]));
	}
	
	public char[][] createX(int n){
		char[][] board = new char[n][n];
		if(n == 0){
			return board;
		}
		int mid = n/2;
		int count = 0;
		while(count <= mid){
			int r = count, c = count;
			for(int i = 0; i < 4; i++){
				board[r][c] = 'X';
				if(i % 2 == 1){
					r = n - 1 - r;
					
				}else{
					c = n - 1 - c;
				}				
			}
			count++;
		}
		return board;
	}
}
