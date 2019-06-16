package leetcode.solution;

import java.util.ArrayList;
import java.util.List;

public class Question51_NQueens {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        queensHelper(n, list, result);
        return result;
    }

    private void queensHelper(int n, List<Integer> list, List<List<String>> result) {
        if (list.size() == n){
            result.add(drawTable(list));
            return;
        }
        for(int col = 0; col < n; col++){
            if(!isValid(col,list,n)) continue;
            list.add(col);
            queensHelper(n, list, result);
            list.remove(list.size()-1);
        }
    }

    private boolean isValid(int col, List<Integer> cols, int n) {
        for(int row = 0; row < cols.size(); row++){
            int curC = cols.get(row);
            if (col == curC) return false;
            int rowDis = Math.abs(cols.size() - row);
            int colDis = Math.abs(col - curC);
            if (rowDis == colDis) return false;
        }
        return true;
    }

    private List<String> drawTable(List<Integer> list) {
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
