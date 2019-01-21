package leetcode;

public class Solution36 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public boolean isValidSudoku(char[][] board) {
	    int [] vset = new int [9];
	    int [] hset = new int [9];
	    int [] bckt = new int [9];
	    int idx = 0;
	    for (int i = 0; i < 9; i++) {
	        for (int j = 0; j < 9; j++) {
	            if (board[i][j] != '.') {
	                idx = 1 << (board[i][j] - '0') ;
	                if ((hset[i] & idx) > 0 ||
	                    (vset[j] & idx) > 0 ||
	                    (bckt[(i / 3) * 3 + j / 3] & idx) > 0) return false;
	                hset[i] |= idx;
	                vset[j] |= idx;
	                bckt[(i / 3) * 3 + j / 3] |= idx;
	            }
	        }
	    }
	    return true;
	}
	
    public boolean isValidSudoku2(char[][] board) {
    boolean[][] rowValid = new boolean[9][9];
    boolean[][] colValid = new boolean[9][9];
    boolean[][] subValid = new boolean[9][9];

    for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
            if (board[i][j] == '.') {
                continue;
            }

            int index = board[i][j] - '1';
            if (rowValid[i][index] || colValid[j][index] || subValid[(i/3)*3 + j/3][index]) {
                return false;
            }

            rowValid[i][index] = colValid[j][index] = subValid[(i/3)*3 + j/3][index] = true;
        }
    }

    return true;
}
}
