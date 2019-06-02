package leetcode.graphAndSearch.backTracking;

import java.util.ArrayList;
import java.util.List;

public class FactorCombinations {
    public List<List<Integer>> getFactors(int n) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        if (n == 1) {
            return result;
        }
        dfsSearchAll(n, result, list, 2);
        return result;
    }

    private void dfsSearchAll(int cur, List<List<Integer>> result, List<Integer> list, int start) {
        if (cur == 1) {
            if(list.size() > 1) result.add(new ArrayList<>(list));
            return;
        }
        for (int i = start; i <= cur; i++) {
            if (cur % i == 0) {
                list.add(i);
                dfsSearchAll(cur / i, result, list, i);
                list.remove(list.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        FactorCombinations solver = new FactorCombinations();
        List<List<Integer>> result = solver.getFactors(8);
        for (List<Integer> list : result) {
            System.out.println(list.toString());
        }
    }
}
