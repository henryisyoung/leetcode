package datastructure.backtracking;

import java.util.*;

public class NQueens {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        dfsSearch(result, new ArrayList<Integer>(), n);

        return result;
    }

    private void dfsSearch(List<List<String>> result, ArrayList<Integer> cols, int n) {
        if (cols.size() == n) {
            result.add(printBoard(cols));
            return;
        }
        for (int c = 0; c < n; c++) {
            if (isValid(c, cols)) {
                cols.add(c);
                dfsSearch(result, cols, n);
                cols.remove(cols.size() - 1);
            }
        }
    }

    private boolean isValid(int nc, ArrayList<Integer> cols) {
        if (cols.contains(nc)) return false;
        int nr = cols.size();
        for (int r = 0; r < cols.size(); r++) {
            if (nr - nc == r - cols.get(r)) return false;
            if (nr + nc == r + cols.get(r)) return false;
        }
        return true;
    }

    private List<String> printBoard(ArrayList<Integer> list) {
        List<String> rst = new ArrayList<>();
        for(int row = 0; row < list.size(); row++) {
            int col = list.get(row);
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < list.size(); i++) {
                if(i == col) sb.append('Q');
                else sb.append('.');
            }
            rst.add(sb.toString());
        }
        return rst;
    }
}
