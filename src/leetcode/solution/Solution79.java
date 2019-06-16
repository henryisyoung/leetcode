package leetcode.solution;

public class Solution79 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution79 t = new Solution79();
		char[][] board = {
				{'A','B','C','E'},
				{'S','F','C','S'},
				{'A','D','E','E'}
		};
		System.out.println(t.exist(board, "SEEa"));
		///word = "ABCCED", -> returns true,
		///word = "SEE", -> returns true,
		///word = "ABCB", -> returns false.
	}
	
    public boolean exist(char[][] board, String word) {
        if(board==null || board[0]==null) return false;
        if(board.length==0 || board[0].length==0) return false;
        int rows=board.length, cols=board[0].length;
        boolean[][] visited = new boolean[rows][cols];
        for(int r=0; r<rows; r++){
        	for(int c=0; c<cols; c++){
        		if(board[r][c] == word.charAt(0)){
            			if(existHelper(board,r,c,word.toCharArray(),visited,0)) 
            				return true;
        			}
        		}
        	}
        return false;
    }

	private boolean existHelper(char[][] board, int sr, int sc, char[] target, boolean[][] visited, int start) {
		int rows=board.length, cols=board[0].length;
		int[][] dir = {{+1,0},{-1,0},{0,+1},{0,-1}};
	    if (board[sr][sc] == target[start]) {
	        visited[sr][sc] = true;
	        if (start == target.length - 1) return true;
	        for(int i=0; i<dir.length;i++){
	        	int nr = sr + dir[i][0], nc = sc + dir[i][1];
	        	if( nr>=0 && nr<rows && nc>=0 && nc<cols &&!visited[nr][nc]&& existHelper(board,nr,nc,target,visited,start+1)){
	        		return true;
	        	}
	        }
	    }
	    visited[sr][sc] = false;
	    return false;
	}
}
