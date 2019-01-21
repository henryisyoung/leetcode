package leetcode;

import java.util.Arrays;

public class Solution286 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int INF = 2147483647;
		int[][] rooms = {{INF,  -1,  0,  INF},
						 {INF, INF, INF,  -1},
						 {INF,  -1, INF,  -1},
						 {0,  -1, INF, INF}};
		Solution286 t = new Solution286();
		t.wallsAndGates(rooms);
		System.out.println(Arrays.toString(rooms[3]));
	}
    public void wallsAndGates(int[][] rooms) {
        if(rooms == null || rooms.length == 0 || rooms[0].length == 0){
        	return;
        }
        int m = rooms.length, n = rooms[0].length;
        for(int i = 0; i < m; i++){
        	for(int j = 0; j < n; j++){
        		if(rooms[i][j] == 0){
        			dfs(i, j , rooms, 1);
        		}
        	}
        }
    }
	private void dfs(int r, int c, int[][] A, int level) {
		int m = A.length, n = A[0].length;
		int[] dr = {1, -1, 0, 0};
		int[] dc = {0, 0, -1, 1};
		for(int i = 0;i < 4; i++){
			int nr = r + dr[i], nc = c + dc[i];
			if(nr >= 0 && nr < m && nc >= 0 && nc < n && A[nr][nc] > level){
				A[nr][nc] = level;
				dfs(nr, nc , A, level + 1);
			}
		}
	}
}
