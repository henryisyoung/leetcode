package leetcode.graphAndSearch;

import java.util.*;

public class Combinations {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        dfsSearchAll(result, list, 1, k, n);
        return result;
    }

    private void dfsSearchAll(List<List<Integer>> result, List<Integer> list, int pos, int k, int n) {
        if (list.size() == k) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i <= n; i++) {
            list.add(i);
            dfsSearchAll(result, list, i + 1, k, n);
            list.remove(list.size() - 1);
        }
    }
}
