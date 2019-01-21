package Bloomberg;

public class ReachTarget {

	public static void main(String[] args) {
		ReachTarget t = new ReachTarget();
		int[][] A = {{0,1,2,3,4},{0,3,0,1,7},{0,3,0,1,7},{0,3,3,1,7}};
		System.out.println(t.reach(A, 2, 1));
		

	}
	public int reach(int[][] A, int k, int t){
		if(A == null || A.length == 0 || A[0].length == 0){
			return 0;
		}
		int m = A.length, n = A[0].length;
		if(m <= k || n <= t){
			return 0;
		}
		int target = A[k][t];
		boolean[][] isVisited = new boolean[m][n];
		int count = 0;
		for(int i = 0; i < m; i++){
			for(int j = 0; j < n; j++){
				if(!isVisited[i][j] && target == A[i][j]){
					//System.out.println(i + " " + j);
					count += dfs(target, A, i, j, isVisited);
				}
			}
		}
		return count;
	}
	
	private int dfs(int target, int[][] A, int r, int c, boolean[][] isVisited) {
		int m = A.length, n = A[0].length;
		if(r < 0 || c < 0 || r >= m || c >= n){
			return 0;
		}
		if(isVisited[r][c]){
			return 0;
		}
		isVisited[r][c] = true;
		if(target == A[r][c]){
			return dfs(target, A, r - 1, c, isVisited) +
					dfs(target, A, r + 1, c, isVisited) +
					dfs(target, A, r, c + 1, isVisited) +
					dfs(target, A, r, c - 1, isVisited);
		}else{
			
			return 1;
		}
	}
}
