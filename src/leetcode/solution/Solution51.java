package leetcode.solution;
import java.util.*;
public class Solution51 {

	public static void main(String[] args) {
		Solution51 t = new Solution51();
		System.out.println(t.solveNQueens(4));

	}
    public List<List<String>> solveNQueens(int n) {
    	List<List<String>> ls = new ArrayList<List<String>>();
    	int[] cols = new int[n];
    	solveNQueens(cols,0,n,ls);
    	return ls;
    }
	private void solveNQueens(int[] cols, int row,int n, List<List<String>> ls) {
		if(row==n) {
			ls.add(construct(cols));
			return;
		}
		else{
			for(int col=0;col<n;col++){
				if(isValid(cols,row,col)){
					cols[row]=col;
					solveNQueens(cols,row+1,n,ls);
				}
			}
		}
	}
	private boolean isValid(int[] cols, int row, int col) {
		for(int row2=0;row2<row;row2++){
			int col2=cols[row2];
			if(col2==col) return false;
			int rowDis = row-row2;
			int colDis = Math.abs(col-col2);
			if(rowDis==colDis) return false;
		}
		return true;
	}
	private List<String> construct(int[] columns) {
	    List<String> rst = new ArrayList<>();
	    for(int row = 0; row < columns.length; row++) {
	        int col = columns[row];
	        StringBuffer sb = new StringBuffer();
	        for(int i = 0; i < columns.length; i++) {
	            if(i == col) sb.append('Q');
	            else sb.append('.');
	        }
	        rst.add(sb.toString());
	    }
	    return rst;
	}
	
	 public List<List<String>> solveNQueens2(int n) { //faster
         int mark[] = new int[n];
         boolean x_mark[] = new boolean[n];
         List<List<String>> ret= new ArrayList<List<String>>();
         Recur(n, mark, x_mark, ret, n);
         return ret;
     }
     public boolean valid(int n, int row, int index, int mark[])
     {
         for(int i = row + 1; i < n; i++) {
             int colDis = Math.abs(index - mark[i]);
             int rowDis = i - row;
             if(colDis == rowDis) return false;
         }
         return true;
     }
     public boolean Recur(int n, int mark[], boolean x_mark[], List<List<String>> ret, int depth)
     {
         if(depth == 0)
         {
             List<String> s_ret = new ArrayList<String>();
             char[] lin = new char[n];
             for(int i = 0; i < n; i++)
                 lin[i] = '.';
             for(int i = 0; i < n; i++)
             {
                 lin[mark[i]] = 'Q';
                 s_ret.add(new String(lin));
                 lin[mark[i]] = '.';
             }

             ret.add(s_ret);
             return true;
         }
         else
         {
             for(int i = 0; i < n; i++){
                 if(!x_mark[i] && valid(n, depth - 1, i, mark)){
                     x_mark[i] = true;
                     mark[depth - 1] = i;
                     Recur(n, mark, x_mark, ret, depth - 1);
                     x_mark[i] = false;
                 }
             }
         }
         return true;
     }
}
