package leetcode.solution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Solution130 {
	class Node{
		int x, y;
		public Node(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
    public void solve(char[][] board) {
        if(board == null || board.length == 0 || board[0].length == 0){
        	return;
        }
        int n = board.length, m = board[0].length;
        for(int i = 0; i < n; i++ ){
        	bfs(board, i, 0);
        	bfs(board, i, m - 1);
        }
        for(int j = 1; j < m - 1; j++ ){
        	bfs(board, 0, j);
        	bfs(board, n - 1, j);
        }
        for(int i = 0; i < n; i++ ){
        	for(int j = 0; j < m; j++ ){
        		if(board[i][j] == 'O'){
        			board[i][j] = 'X';
        		}else if(board[i][j] == 'F'){
        			board[i][j] = 'O';
        		}
        	}
        }
    }

	private void bfs(char[][] board, int r, int c) {
		if(board[r][c] != 'O'){
			return;
		}
		Queue<Node> queue = new LinkedList<Node>();
		queue.offer(new Node(r, c));
		
		while(!queue.isEmpty()){
			Node cur = queue.poll();
			board[cur.x][cur.y] = 'F';
			for(Node n : expansion(board, cur)){
				queue.offer(n);
			}
		}
		
	}

	private List<Node> expansion(char[][] board, Node cur) {
		List<Node> list = new ArrayList<Node>();
		int[] dx = {0,0,-1,1};
		int[] dy = {-1,1,0,0};
		for(int i = 0; i < 4; i++){
			int nr = cur.y + dy[i];
			int nc = cur.x + dx[i];
			if(nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length && board[nr][nc] == 'O'){
				board[nr][nc] = 'T';
				list.add(new Node(nr, nc));
			}
		}
		
		return list;
	}
	
}
