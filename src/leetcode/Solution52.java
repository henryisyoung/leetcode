package leetcode;


public class Solution52 {
	public static void main(String[] args) {
		Solution52 t = new Solution52();
		System.out.println(t.totalNQueens(4));
	}
	int count=0;
    public int totalNQueens(int n) {
        boolean[] mark=new boolean[n];
        
        int[] cols = new int[n];
        solveNQ(mark,cols,n,0);
        return count;
    }

	private void solveNQ(boolean[] mark, int[] cols, int n,int row) {
		if(n==row){
			count++;
			//System.out.println(Arrays.toString(cols));
			return;
		}else{
			for(int col=0;col<mark.length;col++){
				if(!mark[col] && isValid(cols,row,col)){
					//System.out.println(n);
					mark[col]=true;
					cols[row]=col;
					solveNQ(mark,cols,n,row+1);
					mark[col]=false;
				}
			}
		}
		
	}

	private boolean isValid(int[] cols, int row, int col) {
		for(int r=0;r<row;r++){
			int c=cols[r];
			if(row-r==Math.abs(c-col)) return false;
		}
		return true;
	}
}
