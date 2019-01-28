package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class NQueens {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        solveHelper(0, result, new ArrayList<Integer>(), n);
        return result;
    }

    private void solveHelper(int row, List<List<String>> result, ArrayList<Integer> list, int n) {
        if (row == n) {
            result.add(drawTable(list));
            return;
        }
        for (int col = 0; col < n; col++) {
            if(isValid(col, list)) {
                list.add(col);
                solveHelper(row + 1, result, list, n);
                list.remove(list.size() - 1);
            }
        }
    }

    private boolean isValid(int col, ArrayList<Integer> cols) {
        for (int row = 0; row < cols.size(); row++) {
            int curCol = cols.get(row);
            if(col == curCol) {
                return false;
            }
            int rowDiff = Math.abs(cols.size() - row);
            int colDiff = Math.abs(col - curCol);
            if (rowDiff == colDiff) {
                return false;
            }
        }
        return true;
    }

    private List<String> drawTable(ArrayList<Integer> list) {
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
