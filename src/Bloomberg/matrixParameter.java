package Bloomberg;

public class matrixParameter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		matrixParameter t = new matrixParameter();
		int[][] A = {{0,1,0,0},
				     {0,1,1,0},
				     {1,0,0,1},
				     {1,0,0,1}};
		System.out.println(t.parameter(A, 1, 1));
	}
	
	public int parameter(int[][] A, int r, int c){
		if(A == null || A.length == 0 || A[0].length == 0){
			return 0;
		}
		int m = A.length, n = A[0].length;
		if(m <= r || n <= c){
			return 0;
		}
		int target = A[r][c];
		boolean[][] isVisited = new boolean[m][n];
		return dfs(A, r, c, isVisited, target);
	}

	private int dfs(int[][] A, int r, int c, boolean[][] isVisited, int target) {
		int m = A.length, n = A[0].length;
		if(r < 0 || r >= m || c < 0 || c >= n){
			return 1;
		}
		if(isVisited[r][c]){
			return 0;
		}
		
		if(A[r][c] == target){
			isVisited[r][c] = true;
			return dfs(A, r + 1, c, isVisited, target)  +
					dfs(A, r - 1, c, isVisited, target) +
					dfs(A, r, c + 1, isVisited, target) +
					dfs(A, r, c - 1, isVisited, target);
		}
		return 1;
	}
}
