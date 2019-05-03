package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class NQueens2 {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        dfsSearchAll(n, result, cols);
        return result;
    }

    private void dfsSearchAll(int n, List<List<String>> result, List<Integer> cols) {
        if (cols.size() == n) {
            List<String> board = printBoard(cols);
            result.add(board);
            return;
        }
        for (int c = 0; c < n; c++) {
            if (isValidMove(c, cols, n)) {
                cols.add(c);
                dfsSearchAll(n, result, cols);
                cols.remove(cols.size() - 1);
            }
        }
    }

    private boolean isValidMove(int c, List<Integer> cols, int n) {
        for (int r = 0; r < cols.size(); r++) {
            int curCol = cols.get(r);
            if (curCol == c) {
                return false;
            }
            int colDiff = Math.abs(c - curCol);
            int rowDiff = Math.abs(r - cols.size());
            if (colDiff == rowDiff) {
                return false;
            }
        }
        return true;
    }

    private List<String> printBoard(List<Integer> list) {
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
